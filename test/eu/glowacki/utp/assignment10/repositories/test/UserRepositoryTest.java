package eu.glowacki.utp.assignment10.repositories.test;

import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.exceptions.Assignment10Exception;
import eu.glowacki.utp.assignment10.repositories.IUserRepository;
import eu.glowacki.utp.assignment10.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class UserRepositoryTest extends RepositoryTestBase<UserDTO, IUserRepository> {

    private final String selectUserRecords = "SELECT ID_USER, USER_LOGIN, USER_PASSWORD " +
            "FROM USERS " +
            "WHERE ID_USER = ?";
    private final String selectGroupsUsersRecords = "SELECT ID_USER, ID_GROUP " +
            "FROM GROUPS_USERS " +
            "WHERE ID_USER = ?";

    @Test //+
    public void add() {
        //creating the user
        UserDTO usr = new UserDTO(11, "test", "test");
        List<GroupDTO> groups = new LinkedList<>();
        for (int i = 3; i <= 5; i++) {
            groups.add(new GroupDTO(i, "", ""));
        }
        usr.setGroups(groups);

        int initialCount = _repository.getCount();
        //adding
        _repository.add(usr);
        //checking if number of records is correct
        Assert.assertEquals(initialCount + 1, _repository.getCount());

        checkUsersTableRecord(usr);
        checkAssignedGroups(usr);
    }

    @Test //+
    public void update() {
        //creating the user
        UserDTO usr = new UserDTO(1, "test", "test");
        List<GroupDTO> groups = new LinkedList<>();
        for (int i = 3; i <= 5; i++) {
            groups.add(new GroupDTO(i, "", ""));
        }
        usr.setGroups(groups);

        int initialCount = _repository.getCount();
        //updating
        _repository.update(usr);
        //check if number of records is the same
        Assert.assertEquals(initialCount, _repository.getCount());

        checkUsersTableRecord(usr);
        checkAssignedGroups(usr);
    }

    @Test //+
    public void addOrUpdate() {
        //adding a new user
        UserDTO addUsr = new UserDTO(11, "test", "test");
        List<GroupDTO> addUsrGroups = new LinkedList<>();
        for(int i = 2; i <=4; i++) {
            addUsrGroups.add(new GroupDTO(i, "", ""));
        }
        addUsr.setGroups(addUsrGroups);

        int initCount = _repository.getCount();
        _repository.addOrUpdate(addUsr);
        //check if the number of records increased
        Assert.assertEquals(++initCount, _repository.getCount());
        checkUsersTableRecord(addUsr);
        checkAssignedGroups(addUsr);

        //updating an existing user
        UserDTO updUser = new UserDTO(3, "test", "test"); //the user isn't assigned to any groups
        _repository.addOrUpdate(updUser);
        Assert.assertEquals(initCount, _repository.getCount());
        checkUsersTableRecord(updUser);
        checkAssignedGroups(updUser);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void delete() {
        UserDTO usr = new UserDTO(4, "", "");
        List<GroupDTO> groups = new LinkedList<>();
        for (int i = 1; i <= 2; i++) {
            groups.add(new GroupDTO(i, "", ""));
        }
        usr.setGroups(groups);
        int initCount = _repository.getCount();
        _repository.delete(usr);
        Assert.assertEquals(initCount - 1, _repository.getCount());

        //get the _repository connection
        Connection connection = getRepoConnection();

        //check if the record was removed from the users table
        try (PreparedStatement statement = connection.prepareStatement(selectUserRecords)) {
            statement.setInt(1, usr.getId());

            try(ResultSet resSet = statement.executeQuery()) {
                Assert.assertFalse(resSet.next());
            }
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }

        //check if all related records from groups_users were removed
        try (PreparedStatement statement = connection.prepareStatement(selectGroupsUsersRecords)) {
            statement.setInt(1, usr.getId());

            try (ResultSet resSet = statement.executeQuery()) {
                Assert.assertFalse(resSet.next());
            }
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }

        //check if the user was removed from it's groups' lists
        if((usr.getGroups() == null) || usr.getGroups().isEmpty()) {
            return;
        }
        for(GroupDTO g : usr.getGroups()) {
            if(g.getUsers() == null) {
                continue;
            }
            Assert.assertFalse(g.getUsers().contains(usr));
        }

    }

    @Test
    public void findById() {
        UserDTO usr = _repository.findById(2);

        checkUsersTableRecord(usr);
        checkAssignedGroups(usr);
    }

    @Test
    public void findByName() {
        List<UserDTO> list = _repository.findByName("%login%");

        for (UserDTO usr : list) {
            checkUsersTableRecord(usr);
            checkAssignedGroups(usr);
        }
    }

    @Override
    protected IUserRepository Create() {
        return new UserRepository(pooledConnection);
    }

    private void checkUsersTableRecord(UserDTO usr) {
        //getting the connection
        Connection connection = getRepoConnection();

        //check if the record is added to USERS table
        try (PreparedStatement statement = connection.prepareStatement(selectUserRecords)) {
            statement.setInt(1, usr.getId());

            try (ResultSet resSet = statement.executeQuery()) {
                Assert.assertTrue(resSet.next());
                Assert.assertEquals(usr.getId(), resSet.getInt(1));
                Assert.assertEquals(usr.getLogin(), resSet.getString(2));
                Assert.assertEquals(usr.getPassword(), resSet.getString(3));
            }
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }

    }

    private void checkAssignedGroups(UserDTO usr) {
        //getting connection
        Connection connection = getRepoConnection();

        //check if the user is assigned to correct groups
        try (PreparedStatement statement = connection.prepareStatement(selectGroupsUsersRecords)) {
            statement.setInt(1, usr.getId());

            try (ResultSet resSet = statement.executeQuery()) {

                //in case user has no assigned groups
                if ((usr.getGroups() == null) || usr.getGroups().isEmpty()) {
                    Assert.assertFalse(resSet.next());
                    return;
                }

                List<Integer> expectedGroupIdList = usr.getGroups().stream()
                        .map(g -> g.getId())
                        .collect(Collectors.toList());
                List<Integer> actualGroupIdList = new LinkedList<>();
                while (resSet.next()) {
                    actualGroupIdList.add(resSet.getInt(2));
                }

                Assert.assertTrue(expectedGroupIdList.containsAll(actualGroupIdList));
                Assert.assertTrue(actualGroupIdList.containsAll(expectedGroupIdList));
            }

        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
    }

}
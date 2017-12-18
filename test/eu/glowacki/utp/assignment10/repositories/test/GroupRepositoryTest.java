package eu.glowacki.utp.assignment10.repositories.test;

import eu.glowacki.utp.assignment10.dtos.DTOBase;
import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.exceptions.Assignment10Exception;
import eu.glowacki.utp.assignment10.repositories.GroupRepository;
import eu.glowacki.utp.assignment10.repositories.IGroupRepository;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public final class GroupRepositoryTest extends RepositoryTestBase<GroupDTO, IGroupRepository> {

    private final String selectGroupRecords = "SELECT ID_GROUP, GROUP_NAME, GROUP_DESCRIPTION " +
            "FROM GROUPS " +
            "WHERE ID_GROUP = ?";
    private final String selectGroupsUsersRecords = "SELECT ID_USER, ID_GROUP " +
            "FROM GROUPS_USERS " +
            "WHERE ID_GROUP = ?";

    @Test
	public void add() {
        //creating the group
        GroupDTO group = new GroupDTO(6, "test", "test");
        List<UserDTO> users = new LinkedList<>();
        for(int i = 3; i <= 10; i++) {
            users.add(new UserDTO(i, "", ""));
        }
        group.setUsers(users);

        int initCount = _repository.getCount();
        //adding
        _repository.add(group);
        //checking if number of records is correct
        Assert.assertEquals(initCount + 1, _repository.getCount());

        checkGroupsTableRecord(group);
        checkAssignedUsers(group);
	}

	@Test
	public void update() {
        GroupDTO group = new GroupDTO(1, "test", "test");
        List<UserDTO> users = new LinkedList<>();
        for(int i = 7; i <= 10; i++) {
            users.add(new UserDTO(i, "", ""));
        }
        group.setUsers(users);

        int initCount = _repository.getCount();
        //updating
        _repository.update(group);
        //check if number of records is the same
        Assert.assertEquals(initCount, _repository.getCount());

        checkGroupsTableRecord(group);
        checkAssignedUsers(group);
	}

	@Test
	public void addOrUpdate() {
        //adding a new group
        GroupDTO addGroup = new GroupDTO(6, "test", "test");
        List<UserDTO> addGroupUsers = new LinkedList<>();
        for(int i = 5; i <= 8; i++) {
            addGroupUsers.add(new UserDTO(i, "", ""));
        }
        addGroup.setUsers(addGroupUsers);

        int initCount = _repository.getCount();
        _repository.addOrUpdate(addGroup);
        //check if the number of records increased
        Assert.assertEquals(++initCount, _repository.getCount());
        checkGroupsTableRecord(addGroup);
        checkAssignedUsers(addGroup);

        //updating an existing group
        GroupDTO updGroup = new GroupDTO(4, "test", "test");
        List<UserDTO> udpGroupUsers = new LinkedList<>();
        for(int i = 2; i <= 8; i++) {
            udpGroupUsers.add(new UserDTO(i, "", ""));
        }
        updGroup.setUsers(udpGroupUsers);

        _repository.addOrUpdate(updGroup);
        Assert.assertEquals(initCount, _repository.getCount());
        checkGroupsTableRecord(updGroup);
        checkAssignedUsers(updGroup);
	}

	@Test
	public void delete() {
        GroupDTO group = new GroupDTO(1, "test", "test");
        List<UserDTO> users = new LinkedList<>();
        for(int i = 1; i <= 2; i++) {
            UserDTO usr = new UserDTO(i, "", "");
            usr.addGroup(group);
            users.add(usr);
        }
        group.setUsers(users);


        int initCount = _repository.getCount();
        _repository.delete(group);
        Assert.assertEquals(initCount - 1, _repository.getCount());

        //get the _repository connection
        Connection connection = getRepoConnection();

        //check if the record was removed from the groups table
        try (PreparedStatement statement = connection.prepareStatement(selectGroupRecords)) {
            statement.setInt(1, group.getId());

            try(ResultSet resSet = statement.executeQuery()) {
                Assert.assertFalse(resSet.next());
            }
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }

        //check if all related records from groups_users were removed
        try (PreparedStatement statement = connection.prepareStatement(selectGroupsUsersRecords)) {
            statement.setInt(1, group.getId());

            try (ResultSet resSet = statement.executeQuery()) {
                Assert.assertFalse(resSet.next());
            }
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }

        //check if the group was removed from its users' lists
        if((group.getUsers() == null) || group.getUsers().isEmpty()) {
            return;
        }
        for(UserDTO usr : group.getUsers()) {
            if(usr.getGroups() == null) {
                continue;
            }
            Assert.assertFalse(usr.getGroups().contains(group));
        }
	}

	@Test
	public void findById() {
        GroupDTO group = _repository.findById(3);

        checkGroupsTableRecord(group);
        checkAssignedUsers(group);
	}

	@Test
	public void findByName() {
        List<GroupDTO> list = _repository.findByName("%F%");

        for(GroupDTO group : list) {
            checkGroupsTableRecord(group);
            checkAssignedUsers(group);
        }
	}

	@Override
	protected IGroupRepository Create() {
        return new GroupRepository(pooledConnection);
    }

    private void checkGroupsTableRecord(GroupDTO group) {
        //getting the connection
        Connection connection = getRepoConnection();

        //check if the record is added to USERS table
        try (PreparedStatement statement = connection.prepareStatement(selectGroupRecords)) {
            statement.setInt(1, group.getId());

            try (ResultSet resSet = statement.executeQuery()) {
                Assert.assertTrue(resSet.next());
                Assert.assertEquals(group.getId(), resSet.getInt(1));
                Assert.assertEquals(group.getName(), resSet.getString(2));
                Assert.assertEquals(group.getDescription(), resSet.getString(3));
            }
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }

    }

    private void checkAssignedUsers(GroupDTO group) {
        //getting connection
        Connection connection = getRepoConnection();

        //check if the user is assigned to correct groups
        try (PreparedStatement statement = connection.prepareStatement(selectGroupsUsersRecords)) {
            statement.setInt(1, group.getId());

            try (ResultSet resSet = statement.executeQuery()) {

                //in case group has no assigned users
                if ((group.getUsers() == null) || group.getUsers().isEmpty()) {
                    Assert.assertFalse(resSet.next());
                    return;
                }

                List<Integer> expectedUserIdList = group.getUsers().stream()
                        .map(DTOBase::getId)
                        .collect(Collectors.toList());
                List<Integer> actualUserIdList = new LinkedList<>();
                while (resSet.next()) {
                    actualUserIdList.add(resSet.getInt(1));
                }
                //assert the lists are identical
                Assert.assertTrue(expectedUserIdList.containsAll(actualUserIdList));
                Assert.assertTrue(actualUserIdList.containsAll(expectedUserIdList));
            }

        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
    }



}
package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.DTOBase;
import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.exceptions.Assignment10Exception;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class UserRepository extends MyRepository<UserDTO> implements IUserRepository {
    // TODO: 16.12.2017 pooling
    // TODO: 16.12.2017 groups_users func
    //deleting
    //1. delete groups_users records
    //2. delete users records
    //3. delete from group's lists

    public UserRepository() {
        super();
    }

    @Override//+
    public List<UserDTO> findByName(String username) {

        List<UserDTO> resultUserList = null;

        PreparedStatement userStatement;
        ResultSet userResultSet;

        try {
            //find users with this name
            userStatement = connection.prepareStatement(
                    "SELECT ID_USER, USER_LOGIN, USER_PASSWORD " +
                            "FROM USERS " +
                            "WHERE USER_LOGIN LIKE ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            userStatement.setString(1, username);
            userResultSet = userStatement.executeQuery();
            if(!userResultSet.next()) {//checking if the resultSet is empty
                return null;
            }
            userResultSet.beforeFirst();//set cursor to initial position
            while(userResultSet.next()) {
                if(resultUserList == null) {
                    resultUserList = new LinkedList<>();
                }
                resultUserList.add(new UserDTO(userResultSet.getInt(1), userResultSet.getString(2), userResultSet.getString(3)));
            }

            resultUserList.forEach(u -> u.setGroups(findAssignedGroups(u.getId())));
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
        return resultUserList;
    }

    @Override//+
    public void add(UserDTO dto) {
        if(dto == null)
            return;
        if(exists(dto))
            throw new Assignment10Exception("USER WITH SUCH ID ALREADY EXISTS", new SQLException());
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO USERS VALUES (?, ?, ?)");
            statement.setInt(1, dto.getId());
            statement.setString(2, dto.getLogin());
            statement.setString(3, dto.getPassword());
            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO GROUPS_USERS " + "VALUES (?, ?)");
            statement.setInt(1, dto.getId());
            for(GroupDTO d : dto.getGroups()) {
                statement.setInt(2, d.getId());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
        check(dto.getId());
    }

    @Override//+
    public void update(UserDTO dto) {
        if(dto == null)
            return;
        try {
            PreparedStatement updStatement = connection.prepareStatement("UPDATE USERS " + "SET USER_LOGIN = ?, USER_PASSWORD = ? " + "WHERE ID_USER = ?");
            updStatement.setString(1, dto.getLogin());
            updStatement.setString(2, dto.getPassword());
            updStatement.setInt(3, dto.getId());
            updStatement.executeUpdate();

            updStatement = connection.prepareStatement("SELECT ID_USER, ID_GROUP " + "FROM GROUPS_USERS " + "WHERE ID_USER = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            updStatement.setInt(1, dto.getId());
            ResultSet resultSet = updStatement.executeQuery();

            //delete group_user records not present in the list
            while(resultSet.next()) {
                boolean noneMatch = dto.getGroups().stream().mapToInt(DTOBase::getId).noneMatch(i -> {
                    try {
                        return i == resultSet.getInt(2);
                    } catch (SQLException e) {
                        throw new Assignment10Exception(e);
                    }
                });
                if(noneMatch) {
                    resultSet.deleteRow();
                }
            }

            //insert group_user records not present in the database
            List<Integer> groupIds = new LinkedList<>(); //list of ID-s present in the database
            resultSet.beforeFirst();
            while(resultSet.next()) {
                groupIds.add(resultSet.getInt(2));
            }
            updStatement = connection.prepareStatement("INSERT INTO GROUPS_USERS " + "VALUES (?, ?)");
            updStatement.setInt(1, dto.getId());
            for(GroupDTO g : dto.getGroups()) {
                if(!groupIds.contains(g.getId())) {
                    updStatement.setInt(2, g.getId());
                    updStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
        check(dto.getId());
    }

    @Override//+
    public void delete(UserDTO dto) {
        if(dto == null)
            return;
        try {
            //delete from GROUPS_USERS
            PreparedStatement delStatement = connection.prepareStatement("DELETE FROM GROUPS_USERS " + "WHERE ID_USER = ?");
            delStatement.setInt(1, dto.getId());
            delStatement.executeUpdate();

            //delete from GROUPS in the list
            dto.getGroups().forEach(g -> g.deleteUser(dto));

            //delete from USERS
            delStatement = connection.prepareStatement("DELETE FROM USERS " + "WHERE ID_USER = ?");
            delStatement.setInt(1, dto.getId());
            delStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        check(dto.getId());
    }

    @Override//+
    public UserDTO findById(int id) { // assumes IDs are unique
        UserDTO resultUser;
        PreparedStatement userIdStmt;
        ResultSet userResultSet;

        try {
            //getting info about the user
            userIdStmt = connection.prepareStatement("SELECT ID_USER, USER_LOGIN, USER_PASSWORD " + "FROM USERS " + "WHERE ID_USER = ?");
            userIdStmt.setInt(1, id);
            userResultSet = userIdStmt.executeQuery();
            if(!userResultSet.next()) { // checking if the query returned any results
                return null;
            }
            resultUser = new UserDTO(userResultSet.getInt(1), userResultSet.getString(2), userResultSet.getString(3));
            resultUser.setGroups(findAssignedGroups(resultUser.getId()));

            return resultUser;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override //+
    public int getCount() {
        try {
            PreparedStatement countStatement = connection.prepareStatement("SELECT COUNT(ID_USER) " + "FROM USERS");
            ResultSet resultSet = countStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override //+
    public boolean exists(UserDTO dto) {
        if(dto == null)
            return false;
        try {
            PreparedStatement existsStatement = connection.prepareStatement("SELECT ID_USER " + "FROM USERS " + "WHERE ID_USER = ?");
            existsStatement.setInt(1, dto.getId());
            ResultSet resultSet = existsStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //delete later, just for debugging
    public void check(int id) {//temp
        try {
            PreparedStatement d = connection.prepareStatement("SELECT ID_USER, USER_LOGIN, USER_PASSWORD " + "FROM USERS " + "WHERE ID_USER = ?");
            d.setInt(1, id);
            ResultSet set = d.executeQuery();
            if(set.next()) {
                System.out.println(set.getInt(1) + " " + set.getString(2) + " " + set.getString(3));
            }
            d = connection.prepareStatement("SELECT ID_USER, ID_GROUP " + "FROM GROUPS_USERS " + "WHERE ID_USER = ?");
            d.setInt(1, id);
            set = d.executeQuery();
            while(set.next()){
                System.out.println(set.getInt(1) + " " + set.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private List<GroupDTO> findAssignedGroups(int userID) {
        List<GroupDTO> resultGroups = null;

        PreparedStatement group_user_statement;
        ResultSet group_user_resultSet;
        PreparedStatement groupStatement;
        ResultSet groupResultSet;

        try {
            //find group_user records of the user
            group_user_statement = connection.prepareStatement(
                     "SELECT ID_USER, ID_GROUP " +
                             "FROM GROUPS_USERS " +
                             "WHERE ID_USER = ?");
            //find groups connected with this user
            groupStatement = connection.prepareStatement(
                    "SELECT ID_GROUP, GROUP_NAME, GROUP_DESCRIPTION " +
                            "FROM GROUPS " +
                            "WHERE ID_GROUP = ?"
            );
            group_user_statement.setInt(1, userID);
            group_user_resultSet = group_user_statement.executeQuery();

            while(group_user_resultSet.next()) {
                groupStatement.setInt(1, group_user_resultSet.getInt(2));
                groupResultSet = groupStatement.executeQuery();

                //check if there is according record in GROUPS table
                if(!groupResultSet.next()){
                    throw new Assignment10Exception("NO ACCORDING RECORD IN GROUPS TABLE", new SQLException());
                }
                //add found group record to the user
                if(resultGroups == null) {
                    resultGroups = new LinkedList<>();
                }
                resultGroups.add(new GroupDTO(groupResultSet.getInt(1), groupResultSet.getString(2), groupResultSet.getString(3)));
            }
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
        return resultGroups;
    }
}

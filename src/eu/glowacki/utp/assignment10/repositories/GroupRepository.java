package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.DTOBase;
import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.exceptions.Assignment10Exception;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class GroupRepository extends MyRepository<GroupDTO> implements IGroupRepository {
    // TODO: 16.12.2017 pooling

    public GroupRepository() {
        super();
    }

    @Override //+
    public List<GroupDTO> findByName(String name) {

        List<GroupDTO> resultGroupList = null;

        PreparedStatement userStatement;
        ResultSet userResultSet;

        try {
            //find groups with the name
            userStatement = connection.prepareStatement(
                    "SELECT ID_GROUP, GROUP_NAME, GROUP_DESCRIPTION " +
                            "FROM GROUPS " +
                            "WHERE GROUP_NAME LIKE ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            userStatement.setString(1, name);
            userResultSet = userStatement.executeQuery();
            if(!userResultSet.next()) {//checking if the resultSet is empty
                return null;
            }
            userResultSet.beforeFirst();//set cursor to initial position
            while(userResultSet.next()) {
                if(resultGroupList == null) {
                    resultGroupList = new LinkedList<>();
                }
                resultGroupList.add(new GroupDTO(userResultSet.getInt(1), userResultSet.getString(2), userResultSet.getString(3)));
            }

            resultGroupList.forEach(g -> g.setUsers(findAssignedUsers(g.getId())));
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
        return resultGroupList;
    }

    @Override //+
    public void add(GroupDTO dto) {
        if (dto == null)
            return;
        if(exists(dto))
            throw new Assignment10Exception("GROUP WITH SUCH ID ALREADY EXISTS", new SQLException());
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO GROUPS VALUES (?, ?, ?)");
            statement.setInt(1, dto.getId());
            statement.setString(2, dto.getName());
            statement.setString(3, dto.getDescription());
            statement.executeUpdate();

            //add into assigned users' lists
            if(!(dto.getUsers() == null) && dto.getUsers().isEmpty()) {
                dto.getUsers().forEach(usr -> usr.addGroup(dto));
            }

            //connect with assigned users
            statement = connection.prepareStatement("INSERT INTO GROUPS_USERS " + "VALUES (?, ?)");
            statement.setInt(2, dto.getId());
            for(UserDTO usr : dto.getUsers()) {
                statement.setInt(1, usr.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
        check(dto.getId());
    }

    @Override //+
    public void update(GroupDTO dto) {
        if (dto == null)
            return;
        try {
            PreparedStatement updStatement = connection.prepareStatement("UPDATE GROUPS " + "SET GROUP_NAME = ?, GROUP_DESCRIPTION = ? " + "WHERE ID_GROUP = ?");
            updStatement.setString(1, dto.getName());
            updStatement.setString(2, dto.getDescription());
            updStatement.setInt(3, dto.getId());
            updStatement.executeUpdate();


            updStatement = connection.prepareStatement("SELECT ID_USER, ID_GROUP " + "FROM GROUPS_USERS " + "WHERE ID_GROUP = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            updStatement.setInt(1, dto.getId());
            ResultSet resultSet = updStatement.executeQuery();

            //delete group_user records not present in the list
            while(resultSet.next()) {
                boolean noneMatch = dto.getUsers().stream().mapToInt(DTOBase::getId).noneMatch(i -> {
                    try {
                        return i == resultSet.getInt(1);
                    } catch (SQLException e) {
                        throw new Assignment10Exception(e);
                    }
                });
                if(noneMatch) {
                    resultSet.deleteRow();
                }
            }

            //insert group_user records not present in the database
            List<Integer> userIDs = new LinkedList<>(); //list of ID-s present in the database
            resultSet.beforeFirst();
            while(resultSet.next()) {
                userIDs.add(resultSet.getInt(1));
            }
            updStatement = connection.prepareStatement("INSERT INTO GROUPS_USERS " + "VALUES (?, ?)");
            updStatement.setInt(2, dto.getId());
            for(UserDTO usr : dto.getUsers()) {
                if(!userIDs.contains(usr.getId())) {
                    updStatement.setInt(1, usr.getId());
                    updStatement.executeUpdate();
                }
            }

            //add into assigned users' lists
            if(!(dto.getUsers() == null) && dto.getUsers().isEmpty()) {
                dto.getUsers().stream()
                        .filter(usr -> usr.getGroups() == null || !usr.getGroups().contains(dto))
                        .forEach(usr -> usr.addGroup(dto));
            }

        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
        check(dto.getId());
    }

    @Override //+
    public void delete(GroupDTO dto) {
        if(dto == null)
            return;
        try {
            //delete from GROUPS_USERS
            PreparedStatement delStatement = connection.prepareStatement("DELETE FROM GROUPS_USERS " + "WHERE ID_GROUP = ?");
            delStatement.setInt(1, dto.getId());
            delStatement.executeUpdate();

            //delete from users in the list
            if(!(dto.getUsers() == null) && dto.getUsers().isEmpty()) {
                dto.getUsers().forEach(usr -> usr.deleteGroup(dto));
            }

            //delete from GROUPS
            delStatement = connection.prepareStatement("DELETE FROM GROUPS " + "WHERE ID_GROUP = ?");
            delStatement.setInt(1, dto.getId());
            delStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        check(dto.getId());
    }

    @Override //+
    public GroupDTO findById(int id) {
        GroupDTO resultGroup;
        PreparedStatement statement;
        ResultSet groupResultSet;

        try {
            statement = connection.prepareStatement("SELECT ID_GROUP, GROUP_NAME, GROUP_DESCRIPTION " + "FROM GROUPS " + "WHERE ID_GROUP = ?");
            statement.setInt(1, id);
            groupResultSet = statement.executeQuery();
            if (!groupResultSet.next()) { // checking if the query returned any results
                return null;
            }
            resultGroup = new GroupDTO(groupResultSet.getInt(1), groupResultSet.getString(2), groupResultSet.getString(3));
            resultGroup.setUsers(findAssignedUsers(id));
            return resultGroup;
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
    }

    @Override //+
    public int getCount() {
        try {
            PreparedStatement countStatement = connection.prepareStatement("SELECT COUNT(ID_GROUP) " + "FROM GROUPS");
            ResultSet resultSet = countStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
    }

    @Override //+
    public boolean exists(GroupDTO dto) {
        if (dto == null)
            return false;
        try {
            PreparedStatement existsStatement = connection.prepareStatement("SELECT ID_GROUP " + "FROM GROUPS " + "WHERE ID_GROUP = ?");
            existsStatement.setInt(1, dto.getId());
            ResultSet resultSet = existsStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
    }

    public void check(int id) {
        try {
            PreparedStatement d = connection.prepareStatement("SELECT ID_GROUP, GROUP_NAME, GROUP_DESCRIPTION " + "FROM GROUPS " + "WHERE ID_GROUP = ?");
            d.setInt(1, id);
            ResultSet set = d.executeQuery();
            if(set.next()) {
                System.out.println(set.getInt(1) + " " + set.getString(2) + " " + set.getString(3));
            }
            d = connection.prepareStatement("SELECT ID_USER, ID_GROUP " + "FROM GROUPS_USERS " + "WHERE ID_GROUP = ?");
            d.setInt(1, id);
            set = d.executeQuery();
            while(set.next()){
                System.out.println(set.getInt(1) + " " + set.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<UserDTO> findAssignedUsers(int groupID) {
        List<UserDTO> resultUsers = null;

        PreparedStatement group_user_statement;
        ResultSet group_user_resultSet;
        PreparedStatement userStatement;
        ResultSet userResultSet;

        try {
            //find group_user records of the group
            group_user_statement = connection.prepareStatement(
                    "SELECT ID_USER, ID_GROUP " +
                            "FROM GROUPS_USERS " +
                            "WHERE ID_GROUP = ?");
            //find users connected with this group
            userStatement = connection.prepareStatement(
                    "SELECT ID_USER, USER_LOGIN, USER_PASSWORD " +
                            "FROM USERS " +
                            "WHERE ID_USER = ?"
            );
            group_user_statement.setInt(1, groupID);
            group_user_resultSet = group_user_statement.executeQuery();

            while(group_user_resultSet.next()) {
                userStatement.setInt(1, group_user_resultSet.getInt(1));
                userResultSet = userStatement.executeQuery();

                //check if there is an according record in USERS table
                if(!userResultSet.next()){
                    throw new Assignment10Exception("NO ACCORDING RECORD IN USERS TABLE", new SQLException());
                }
                //add found user record to the list
                if(resultUsers == null) {
                    resultUsers = new LinkedList<>();
                }
                resultUsers.add(new UserDTO(userResultSet.getInt(1), userResultSet.getString(2), userResultSet.getString(3)));
            }
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
        return resultUsers;
    }

}

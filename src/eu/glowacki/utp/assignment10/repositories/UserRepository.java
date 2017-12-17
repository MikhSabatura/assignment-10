package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.DTOBase;
import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.exceptions.Assignment10Exception;
import oracle.jdbc.pool.OracleOCIConnectionPool;
import oracle.jdbc.proxy.annotation.Pre;

import javax.xml.transform.Result;
import java.sql.*;
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

    @Override
    public List<UserDTO> findByName(String username) {
        return null;
    }

    @Override
    public void add(UserDTO dto) {
        if(dto == null)
            return;
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO USERS VALUES (?, ?, ?)");
            statement.setInt(1, dto.getId());
            statement.setString(2, dto.getLogin());
            statement.setString(3, dto.getPassword());
            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO GROUPS_USERS " + "VALUES (?, ?)");
            statement.setInt(dto.getId(), 1);
            for(GroupDTO d : dto.getGroups()) {
                statement.setInt(d.getId(), 2);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
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

            while(resultSet.next()) { //deleting connections with groups not present in the list
                dto.getGroups().stream().map(DTOBase::getId).anyMatch(i -> {
                    try {
                        return i == resultSet.getInt(2);
                    } catch (SQLException e) {
                        throw new Assignment10Exception(e);
                    }
                });
                resultSet.deleteRow();
            }

            updStatement = connection.prepareStatement("INSERT INTO GROUPS_USERS " + "VALUES (?, ?)");
            updStatement.setInt(1, dto.getId());
            for(GroupDTO g : dto.getGroups()) {
                updStatement.setInt(2, g.getId());
                updStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        check(dto.getId());
    }

    @Override
    public void delete(UserDTO dto) {
        if(dto == null)
            return;
        try {
            //todo: first delete from groups_users
            PreparedStatement statement = connection.prepareStatement("DELETE FROM USERS " + "WHERE ID_USER = ?");
            statement.setInt(1, dto.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override //+
    public UserDTO findById(int id) { // assumes there is only 1 user with such id
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT ID_USER, USER_LOGIN, USER_PASSWORD " + "FROM USERS " + "WHERE ID_USER = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()) { // checking if the query returned any results
                return null;
            }
            return  new UserDTO(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
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

    public void check(int id) {
        try {
            PreparedStatement d = connection.prepareStatement("SELECT ID_USER, USER_LOGIN, USER_PASSWORD " + "FROM USERS " + "WHERE ID_USER = ?");
            d.setInt(1, id);
            ResultSet set = d.executeQuery();
            set.next();
            System.out.println(set.getInt(1) + " " + set.getString(2) + " " + set.getString(3));
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

}

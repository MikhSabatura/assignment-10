package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.UserDTO;
import oracle.jdbc.pool.OracleOCIConnectionPool;
import oracle.jdbc.proxy.annotation.Pre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserRepository implements IUserRepository {
    // TODO: 16.12.2017 pooling
    // TODO: 16.12.2017 groups_users func
    //deleting
    //1. delete groups_users records
    //2. delete users records
    //3. delete from group's lists

    private OracleOCIConnectionPool pool;
    private Connection connection;

    public UserRepository(OracleOCIConnectionPool pool) {
        this.pool = pool;
        getConnection();
    }

    @Override
    public List<UserDTO> findByName(String username) {
        return null;
    }

    @Override
    public Connection getConnection() {
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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
            updStatement.setInt(1, dto.getId());
            updStatement.setString(2, dto.getLogin());
            updStatement.setString(3, dto.getPassword());
            updStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override //+
    public void addOrUpdate(UserDTO dto) {
        if(dto == null)
            return;
        if(exists(dto)) {
            add(dto);
        } else {
            update(dto);
        }
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
    public void beginTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override //+
    public void commitTransaction() {
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override //+
    public void rollbackTransaction() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
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

}

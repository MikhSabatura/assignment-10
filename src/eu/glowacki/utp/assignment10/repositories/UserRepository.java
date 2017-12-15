package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.UserDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class UserRepository implements IUserRepository {
    //TODO

    private Connection connection;

    public UserRepository() {
        this.connection = getConnection();
    }

    @Override
    public List<UserDTO> findByName(String username) {
        return null;
    }

    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:oracle:thin:@db-oracle:1521:baza", "s15711", "oracle12");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(UserDTO dto) {

    }

    @Override
    public void update(UserDTO dto) {

    }

    @Override
    public void addOrUpdate(UserDTO dto) {

    }

    @Override
    public void delete(UserDTO dto) {

    }

    @Override
    public UserDTO findById(int id) {
        return null;
    }

    @Override
    public void beginTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commitTransaction() {
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollbackTransaction() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean exists(UserDTO dto) {
        return false;
    }

}

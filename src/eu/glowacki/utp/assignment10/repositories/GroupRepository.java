package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.GroupDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GroupRepository implements IGroupRepository {

    private Connection connection;

    public GroupRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<GroupDTO> findByName(String name) {
        return null;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void add(GroupDTO dto) {

    }

    @Override
    public void update(GroupDTO dto) {

    }

    @Override
    public void addOrUpdate(GroupDTO dto) {

    }

    @Override
    public void delete(GroupDTO dto) {

    }

    @Override
    public GroupDTO findById(int id) {
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
    public boolean exists(GroupDTO dto) {
        return false;
    }

}

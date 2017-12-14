package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.GroupDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class GroupRepository implements IGroupRepository {
    //TODO

    private Connection connection;

    public GroupRepository() {
        this.connection = getConnection();
    }

    @Override
    public List<GroupDTO> findByName(String name) {
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

    }

    @Override
    public void commitTransaction() {

    }

    @Override
    public void rollbackTransaction() {

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

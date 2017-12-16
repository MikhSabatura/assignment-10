package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import oracle.jdbc.pool.OracleOCIConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GroupRepository implements IGroupRepository {
    // TODO: 16.12.2017 pooling
    // TODO: 16.12.2017 groups_users func

    private OracleOCIConnectionPool pool;
    private Connection connection;

    public GroupRepository(OracleOCIConnectionPool pool) {
        this.pool = pool;
        this.connection = getConnection();
    }

    @Override
    public List<GroupDTO> findByName(String name) {
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
    public void add(GroupDTO dto) {
        if(dto == null)
            return;
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO USERS VALUES (?, ?, ?)");
            statement.setInt(1, dto.getId());
            statement.setString(2, dto.getName());
            statement.setString(3, dto.getDescription());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(GroupDTO dto) {
        if(dto == null)
            return;
        try {
            PreparedStatement updStatement = connection.prepareStatement("UPDATE GROUPS " + "SET GROUP_NAME = ?, GROUP_DESCRIPTION = ? " + "WHERE ID_GROUP = ?");
            updStatement.setInt(1, dto.getId());
            updStatement.setString(2, dto.getName());
            updStatement.setString(3, dto.getDescription());
            updStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override //+
    public void addOrUpdate(GroupDTO dto) {
        if(dto == null)
            return;
        if(exists(dto)) {
            add(dto);
        } else {
            update(dto);
        }
    }

    @Override
    public void delete(GroupDTO dto) {

    }

    @Override
    public GroupDTO findById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT ID_GROUP, GROUP_NAME, GROUP_DESCRIPTION " + "FROM GROUPS " + "WHERE ID_GROUP = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()) { // checking if the query returned any results
                return null;
            }
            return new GroupDTO(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
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
            PreparedStatement countStatement = connection.prepareStatement("SELECT COUNT(ID_GROUP) " + "FROM GROUPS");
            ResultSet resultSet = countStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override //+
    public boolean exists(GroupDTO dto) {
        if(dto == null)
            return false;
        try {
            PreparedStatement existsStatement = connection.prepareStatement("SELECT ID_GROUP " + "FROM GROUPS " + "WHERE ID_GROUP = ?");
            existsStatement.setInt(1, dto.getId());
            ResultSet resultSet = existsStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

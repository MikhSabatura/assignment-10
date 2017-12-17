package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import oracle.jdbc.pool.OracleOCIConnectionPool;

import java.sql.*;
import java.util.List;

public class GroupRepository extends MyRepository<GroupDTO> implements IGroupRepository {
    // TODO: 16.12.2017 pooling
    // TODO: 16.12.2017 groups_users func

    public GroupRepository() {
        super();
    }

    @Override
    public List<GroupDTO> findByName(String name) {
        return null;
    }

    @Override
    public void add(GroupDTO dto) {
        if (dto == null)
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
        if (dto == null)
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

    @Override
    public void delete(GroupDTO dto) {

    }

    @Override
    public GroupDTO findById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT ID_GROUP, GROUP_NAME, GROUP_DESCRIPTION " + "FROM GROUPS " + "WHERE ID_GROUP = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) { // checking if the query returned any results
                return null;
            }
            return new GroupDTO(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
        if (dto == null)
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

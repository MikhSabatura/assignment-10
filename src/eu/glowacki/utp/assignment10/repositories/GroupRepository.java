package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.GroupDTO;

import java.sql.Connection;
import java.util.List;

public class GroupRepository implements IGroupRepository {

    @Override
    public List<GroupDTO> findByName(String name) {
        return null;
    }

    @Override
    public Connection getConnection() {
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

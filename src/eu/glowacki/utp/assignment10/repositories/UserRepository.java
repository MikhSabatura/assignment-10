package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.UserDTO;

import java.sql.Connection;
import java.util.List;

public class UserRepository implements IUserRepository {

    @Override
    public List<UserDTO> findByName(String username) {
        return null;
    }

    @Override
    public Connection getConnection() {
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
    public boolean exists(UserDTO dto) {
        return false;
    }

}

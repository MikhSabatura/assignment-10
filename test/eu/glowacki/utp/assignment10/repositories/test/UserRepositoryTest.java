package eu.glowacki.utp.assignment10.repositories.test;

import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.repositories.IUserRepository;
import eu.glowacki.utp.assignment10.repositories.UserRepository;
import oracle.jdbc.pool.OracleOCIConnectionPool;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public final class UserRepositoryTest extends RepositoryTestBase<UserDTO, IUserRepository> {

    @Test
    public void add() {
        System.out.println("add");
        UserDTO u = new UserDTO(11, "test", "test");
        List<GroupDTO> groups = new LinkedList<>();
        for(int i = 1; i <= 5; i++) {
            groups.add(new GroupDTO(i, "", ""));
        }
        u.setGroups(groups);
        _repository.add(u);

    }

    @Test
    public void update() {
        System.out.println("update");
        UserDTO u = new UserDTO(1, "test", "test");
        List<GroupDTO> groups = new LinkedList<>();
        for(int i = 3; i <= 5; i++) {
            groups.add(new GroupDTO(i, "", ""));
        }
        u.setGroups(groups);
        _repository.update(u);
    }

    @Test
    public void addOrUpdate() {
    }

    @Test
    public void delete() {
        System.out.println("delete");
        UserDTO u = new UserDTO(1, "", "");
        List<GroupDTO> groups = new LinkedList<>();
        for(int i = 1; i <= 2; i++) {
            groups.add(new GroupDTO(i, "", ""));
        }
        u.setGroups(groups);
        _repository.delete(u);
    }

    @Test
    public void findById() {
        UserDTO u = _repository.findById(2);
        System.out.println(u);
        u.getGroups().forEach(System.out::println);
    }

    @Test
    public void findByName() {
        List<UserDTO> list =  _repository.findByName("%login%");
        for(UserDTO u : list) {
            System.out.println(u);
            System.out.println(u.getGroups());
        }
    }

    @Override
    protected IUserRepository Create() {
        return new UserRepository(pooledConnection);
    }

}
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
    }

    @Test
    public void update() {
        UserDTO u = new UserDTO(1, " fuck", " fuck");
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
    }

    @Test
    public void findById() {
    }

    @Test
    public void findByName() {
    }

    @Override
    protected IUserRepository Create() {
        if(_repository == null)
            return new UserRepository();
        return _repository;
    }


}
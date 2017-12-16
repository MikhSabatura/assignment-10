package eu.glowacki.utp.assignment10.repositories.test;

import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.repositories.IUserRepository;
import eu.glowacki.utp.assignment10.repositories.UserRepository;
import oracle.jdbc.pool.OracleOCIConnectionPool;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class UserRepositoryTest extends RepositoryTestBase<UserDTO, IUserRepository> {


    @Test
    public void add() {
    }

    @Test
    public void update() {
    }

    @Test
    public void addOrUpdate() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void findById() {
        System.out.println(_repository.findById(3).getLogin());
    }

    @Test
    public void findByName() {
    }

    @Override
    protected IUserRepository Create() {
        return new UserRepository(pool);
    }


}
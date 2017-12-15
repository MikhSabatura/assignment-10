package eu.glowacki.utp.assignment10.repositories.test;

import eu.glowacki.utp.assignment10.repositories.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.glowacki.utp.assignment10.UnimplementedException;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.repositories.IUserRepository;

import java.sql.Connection;
import java.sql.DriverManager;

public final class UserRepositoryTest extends RepositoryTestBase<UserDTO, IUserRepository> {

    private Connection connection;
    private UserRepository userRepo;

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
    }

    @Test
    public void findByName() {
    }

    @Override
    protected IUserRepository Create() {
        return new UserRepository(connection);
    }

    @Before
    public void beforeTest() throws Exception {
        if(connection == null) {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@10.1.1.34:1521:baza", "s15711", "oracle12");
        }
        userRepo = (UserRepository) Create();
        userRepo.beginTransaction();
    }

    @After
    public void afterTest() {
        userRepo.rollbackTransaction();
        userRepo = null;
    }
}
package eu.glowacki.utp.assignment10.repositories.test;

import eu.glowacki.utp.assignment10.repositories.GroupRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.glowacki.utp.assignment10.UnimplementedException;
import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.repositories.IGroupRepository;

import java.sql.Connection;
import java.sql.DriverManager;

public final class GroupRepositoryTest extends RepositoryTestBase<GroupDTO, IGroupRepository> {

    private Connection connection;
    private GroupRepository groupRepo;

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
	protected IGroupRepository Create() {
        return new GroupRepository(connection);
	}

    @Before
    public void beforeTest() throws Exception {
        if(connection == null) {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@10.1.1.34:1521:baza", "s15711", "oracle12");
        }
        groupRepo = (GroupRepository) Create();
        groupRepo.beginTransaction();
    }

    @After
    public void afterTest() {
        groupRepo.rollbackTransaction();
        groupRepo = null;
    }
}
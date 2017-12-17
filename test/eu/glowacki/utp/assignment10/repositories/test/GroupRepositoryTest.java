package eu.glowacki.utp.assignment10.repositories.test;

import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.repositories.GroupRepository;
import eu.glowacki.utp.assignment10.repositories.IGroupRepository;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class GroupRepositoryTest extends RepositoryTestBase<GroupDTO, IGroupRepository> {

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
		if(_repository == null)
			return new GroupRepository();
		return _repository;
	}

}
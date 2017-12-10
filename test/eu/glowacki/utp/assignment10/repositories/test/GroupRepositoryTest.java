package eu.glowacki.utp.assignment10.repositories.test;

import org.junit.Test;

import eu.glowacki.utp.assignment10.UnimplementedException;
import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.repositories.IGroupRepository;

public class GroupRepositoryTest extends RepositoryTestBase<GroupDTO, IGroupRepository> {

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
		throw new UnimplementedException();
	}
}
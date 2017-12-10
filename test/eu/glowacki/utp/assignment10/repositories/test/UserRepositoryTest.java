package eu.glowacki.utp.assignment10.repositories.test;

import org.junit.Test;

import eu.glowacki.utp.assignment10.UnimplementedException;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.repositories.IUserRepository;

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
	}
	
	@Test
	public void findByName() {
	}
	
	@Override
	protected IUserRepository Create() {
		throw new UnimplementedException();
	}
}
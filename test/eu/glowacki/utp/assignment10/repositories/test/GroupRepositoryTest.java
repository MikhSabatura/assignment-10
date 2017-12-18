package eu.glowacki.utp.assignment10.repositories.test;

import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.repositories.GroupRepository;
import eu.glowacki.utp.assignment10.repositories.IGroupRepository;
import eu.glowacki.utp.assignment10.repositories.UserRepository;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public final class GroupRepositoryTest extends RepositoryTestBase<GroupDTO, IGroupRepository> {

    @Test
	public void add() {
        GroupDTO gr = new GroupDTO(7, "test", "test");
        List<UserDTO> users = new LinkedList<>();
        for(int i = 3; i <= 10; i++) {
            users.add(new UserDTO(i, "", ""));
        }
        gr.setUsers(users);
        _repository.add(gr);
	}

	@Test
	public void update() {
        System.out.println("update");
        GroupDTO gr = new GroupDTO(1, "test", "test");
        List<UserDTO> users = new LinkedList<>();
        for(int i = 7; i <= 10; i++) {
            users.add(new UserDTO(i, "", ""));
        }
        gr.setUsers(users);
        _repository.update(gr);
	}

	@Test
	public void addOrUpdate() {
	}

	@Test
	public void delete() {
        GroupDTO gr = new GroupDTO(1, "test", "test");
        List<UserDTO> users = new LinkedList<>();
        for(int i = 1; i <= 2; i++) {
            users.add(new UserDTO(i, "", ""));
        }
        gr.setUsers(users);
        _repository.delete(gr);
	}

	@Test
	public void findById() {
        GroupDTO g = _repository.findById(3);
        System.out.println(g);
        System.out.println(g.getUsers());
	}

	@Test
	public void findByName() {
        List<GroupDTO> list = _repository.findByName("%F%");
        list.forEach(System.out::println);
        list.forEach(g -> System.out.println(g.getUsers()));
	}

	@Override
	protected IGroupRepository Create() {
        return new GroupRepository(pooledConnection);
    }

}
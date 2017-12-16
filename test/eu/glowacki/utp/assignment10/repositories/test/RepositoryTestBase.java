package eu.glowacki.utp.assignment10.repositories.test;

import oracle.jdbc.pool.OracleOCIConnectionPool;
import org.junit.After;
import org.junit.Before;

import eu.glowacki.utp.assignment10.dtos.DTOBase;
import eu.glowacki.utp.assignment10.repositories.IRepository;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class RepositoryTestBase<TDTO extends DTOBase, TRepository extends IRepository<TDTO>> {

	protected TRepository _repository;
	protected static OracleOCIConnectionPool pool;


	@Before
	public void before() {
		_repository = Create();
		if (_repository != null) {
			_repository.beginTransaction();
		}
	}

	@After
	public void after() {
		if (_repository != null) {
			_repository.rollbackTransaction();
		}
	}

    @BeforeClass
    public static void beforeClass() {
        try {
            pool = new OracleOCIConnectionPool("s15711", "oracle12", "jdbc:oracle:thin:@10.1.1.34:1521:baza", null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	protected abstract TRepository Create();
}
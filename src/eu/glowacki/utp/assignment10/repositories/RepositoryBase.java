package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.DTOBase;
import eu.glowacki.utp.assignment10.exceptions.Assignment10Exception;

import javax.sql.PooledConnection;
import java.sql.*;

public abstract class RepositoryBase<TDTO extends DTOBase> implements IRepository<TDTO>{

    protected PooledConnection pooledConnection;
    protected Connection connection;

    protected RepositoryBase(PooledConnection pooledConnection) {
        this.pooledConnection = pooledConnection;
        this.connection = getConnection();
    }

    @Override
    public Connection getConnection() {
        try {
            return pooledConnection.getConnection();
        } catch (SQLException e) {
            throw new Assignment10Exception(e);
        }
    }

    @Override //+
    public void addOrUpdate(TDTO dto) {
        if(dto == null)
            return;
        if(exists(dto)) {
            update(dto);
        } else {
            add(dto);
        }
    }

    @Override //+
    public void beginTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override //+
    public void commitTransaction() {
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override //+
    public void rollbackTransaction() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

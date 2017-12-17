package eu.glowacki.utp.assignment10.repositories;

import eu.glowacki.utp.assignment10.dtos.DTOBase;
import eu.glowacki.utp.assignment10.dtos.GroupDTO;
import eu.glowacki.utp.assignment10.dtos.UserDTO;
import eu.glowacki.utp.assignment10.exceptions.Assignment10Exception;

import java.sql.*;

public abstract class MyRepository<TDTO extends DTOBase> implements IRepository<TDTO>{

    protected Connection connection;

    protected MyRepository() {
        this.connection = getConnection();
    }

    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:oracle:thin:@10.01.01.34:1521:baza", "s15711", "oracle12");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override //+
    public void addOrUpdate(TDTO dto) {
        if(dto == null)
            return;
        if(exists(dto)) {
            add(dto);
        } else {
            update(dto);
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

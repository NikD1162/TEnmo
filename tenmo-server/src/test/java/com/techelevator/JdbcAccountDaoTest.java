//package com.techelevator;
//
//import com.techelevator.tenmo.controller.AccountController;
//import com.techelevator.tenmo.dao.AccountDao;
//import com.techelevator.tenmo.dao.JdbcAccountDao;
//import com.techelevator.tenmo.exception.UserNotFoundException;
//import com.techelevator.tenmo.model.Account;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//
//
//@DataJdbcTest
//public class JdbcAccountDaoTest {
//
//    private JdbcTemplate jdbcTemplate;
//    private JdbcAccountDao dao;
//
//    @Autowired
//    public JdbcAccountDaoTest(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//        dao = new JdbcAccountDao(jdbcTemplate);
//    }
//
//    @Test
//    public void test1() throws UserNotFoundException {
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setUrl();
//        JdbcTemplate jdbcTemplate = new JdbcTemplate();
//        Account account = dao.get(1);
//
//        AccountController controller = new AccountController(new JdbcAccountDao());
//        System.out.println(account.getBalance());
//    }
//
//}

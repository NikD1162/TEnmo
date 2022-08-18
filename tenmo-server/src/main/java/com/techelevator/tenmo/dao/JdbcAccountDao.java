package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.UserNotFoundException;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account get(long userId) throws UserNotFoundException {
        Account account = null;
        String sql = "select * from account where user_id = ?;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (sqlRowSet.next()) {
            account = mapRowToAccount(sqlRowSet);
        }
        return account;
    }

    @Override
    public boolean update(Account account) {
        String sql = "update account set balance = ? where account_id = ?;";
        int numberOfRowsUpdated = jdbcTemplate.update(sql, account.getBalance(), account.getAccountId());
        return numberOfRowsUpdated != 0;
    }

    private Account mapRowToAccount(SqlRowSet sqlRowSet) {
        Account account = new Account();
        account.setAccountId(sqlRowSet.getLong("account_id"));
        account.setUserId(sqlRowSet.getLong("user_id"));
        account.setBalance(sqlRowSet.getBigDecimal("balance"));
        return account;
    }

}

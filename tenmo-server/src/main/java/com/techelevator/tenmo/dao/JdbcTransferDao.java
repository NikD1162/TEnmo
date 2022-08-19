package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }


    @Override
    public BigDecimal sendMoney() {
        return null;
    }

    @Override
    public long create(Transfer transfer) {
        long transferId = -1;
        String sql = "insert into transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount )" +
                "values (?, ?, ?, ?, ?) returning transfer_id";
        try {
            transferId = jdbcTemplate.queryForObject(sql, long.class,
                    transfer.getTransferTypeId(), transfer.getTransferStatusId(),
                    transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        }
        catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return transferId;
    }

    @Override
    public List<Transfer> list(long accountId) {
        List<Transfer> list = new ArrayList<>();
        String sql = "select * from transfer where account_from = ? or account_to = ?;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
            while (sqlRowSet.next()) {
                list.add(mapRowToTransfer(sqlRowSet));
            }
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Transfer mapRowToTransfer(SqlRowSet sqlRowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(sqlRowSet.getLong("transfer_id"));
        transfer.setTransferTypeId(sqlRowSet.getLong("transfer_type_id"));
        transfer.setTransferStatusId(sqlRowSet.getLong("transfer_status_id"));
        transfer.setAccountFrom(sqlRowSet.getLong("account_from"));
        transfer.setAccountTo(sqlRowSet.getLong("account_to"));
        transfer.setAmount(sqlRowSet.getBigDecimal("amount"));
        return transfer;
    }
}

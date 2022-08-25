package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }


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
            System.out.println(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Transfer> listPending(long accountId, long transferStatusId) {
        List<Transfer> list = new ArrayList<>();
        String sql = "select * from transfer where account_from = ? and transfer_status_id = ?;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, accountId, transferStatusId);
            while (sqlRowSet.next()) {
                list.add(mapRowToTransfer(sqlRowSet));
            }
        }
        catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    @Override
    public Transfer get(long transferId) {
        Transfer transfer = null;
        String sql = "select * from transfer where transfer_id = ?;";
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, transferId);
            if (sqlRowSet.next()) {
                transfer = mapRowToTransfer(sqlRowSet);
            }
        }
        catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        return transfer;
    }

    @Override
    public boolean updateStatus(Transfer transfer) {
        String sql = "update transfer set transfer_status_id = ? where transfer_id = ?;";
        return jdbcTemplate.update(sql,transfer.getTransferStatusId(), transfer.getTransferId()) != 0;
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

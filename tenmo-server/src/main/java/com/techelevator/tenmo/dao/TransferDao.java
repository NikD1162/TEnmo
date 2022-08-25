package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    long create(Transfer transfer);
    List<Transfer> list(long accountId);

    List<Transfer> listPending(long accountId, long transferStatusId);

    Transfer get(long transferId) throws TransferNotFoundException;

    boolean updateStatus(Transfer transfer);
}

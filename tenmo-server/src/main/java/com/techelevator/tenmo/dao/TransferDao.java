package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    BigDecimal sendMoney();
    long create(Transfer transfer);
    List<Transfer> list(long accountId);

}

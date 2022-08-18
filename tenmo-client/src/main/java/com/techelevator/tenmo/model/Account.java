package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private long accountId;
    private long userId;
    private BigDecimal balance;

    public Account() {
    }

    public Account(long userId, long accountId, BigDecimal balance) {
        this.userId = userId;
        this.accountId = accountId;
        this.balance = balance;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return getUserId() == account.getUserId() && getAccountId() == account.getAccountId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getAccountId());
    }
}

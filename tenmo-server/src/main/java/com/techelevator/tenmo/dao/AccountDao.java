package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.UserNotFoundException;
import com.techelevator.tenmo.model.Account;

public interface AccountDao {

    boolean update(Account account);

    Account get(long userId) throws UserNotFoundException;

}

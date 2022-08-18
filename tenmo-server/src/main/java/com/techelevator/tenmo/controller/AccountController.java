package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.exception.UserNotFoundException;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("account/")
public class AccountController {

    private AccountDao dao;

    public AccountController(AccountDao dao) {
        this.dao = dao;
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public Account get(@Valid @PathVariable int id) throws UserNotFoundException {
        return dao.get(id);
    }

    @RequestMapping(path = "", method = RequestMethod.PUT)
    public boolean update(@RequestBody Account account) {
        return dao.update(account);
    }



}

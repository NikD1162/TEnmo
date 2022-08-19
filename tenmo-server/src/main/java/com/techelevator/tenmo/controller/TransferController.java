package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("transfer/")
public class TransferController {

    private TransferDao dao;

    public TransferController(TransferDao dao) {
        this.dao = dao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "", method = RequestMethod.POST)
    public long post(@RequestBody Transfer transfer) {
        return dao.create(transfer);
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Transfer> get(@Valid @RequestParam(defaultValue = "0") int account_id) {
        return dao.list(account_id);
    }

}

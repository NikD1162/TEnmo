package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public List<Transfer> getList(@Valid @RequestParam long account_id, @RequestParam(defaultValue = "0") long transfer_status_id) {
        if (transfer_status_id == 0) return dao.list(account_id);
        return dao.listPending(account_id, transfer_status_id);
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public Transfer get(@PathVariable long id) throws TransferNotFoundException {
        return dao.get(id);
    }

    @RequestMapping(path = "", method = RequestMethod.PUT)
    public boolean updateStatus(@RequestBody Transfer transfer) {
        return dao.updateStatus(transfer);
    }

}

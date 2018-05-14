package ru.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.transfer.exception.NotEnoughMoneyException;
import ru.transfer.exception.SameAccountException;
import ru.transfer.model.TransferMoneyModel;
import ru.transfer.service.TransferMoneyService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * Created by Bucky on 12.05.2018.
 */

@Controller
@RequestMapping("/money")
public class TransferMoneyController {

    @Autowired
    private TransferMoneyService transferMoneyService;

    @RequestMapping("/transfer")
    @PutMapping
    public ResponseEntity transferMoneyBetweenAccounts(
            @RequestBody @Valid TransferMoneyModel transferMoneyModel,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + " " + error.getDefaultMessage())
                    .sorted()
                    .collect(Collectors.joining(", ")));
        }
        try {
            transferMoneyService.doTransfer(transferMoneyModel);
        } catch (NotEnoughMoneyException | EntityNotFoundException | SameAccountException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}

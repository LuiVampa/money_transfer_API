package ru.transfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.transfer.entity.Account;
import ru.transfer.exception.NotEnoughMoneyException;
import ru.transfer.exception.SameAccountException;
import ru.transfer.model.Currency;
import ru.transfer.model.TransferMoneyModel;
import ru.transfer.repository.AccountRepository;

import javax.transaction.Transactional;
import java.util.Objects;

/**
 * Created by Bucky on 14.05.2018.
 */

@Service
public class TransferMoneyService {

    private static final Logger LOG = LoggerFactory.getLogger(TransferMoneyService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public void doTransfer(TransferMoneyModel transferMoneyModel) {
        Double amount = transferMoneyModel.getAmount();
        Currency currency = transferMoneyModel.getCurrency();
        Account accountFrom = accountRepository.getOne(transferMoneyModel.getAccountFrom());
        Account accountTo = accountRepository.getOne(transferMoneyModel.getAccountTo());

        if (Objects.equals(accountFrom, accountTo)) {
            LOG.error("Invalid value: accountFrom = accountTo");
            throw new SameAccountException("Invalid value: accountFrom = accountTo");
        }

        checkAmount(accountFrom, amount, currency);

        updateAmount(accountFrom, getAmountByCurrency(accountFrom, currency) - amount, currency);
        updateAmount(accountTo, getAmountByCurrency(accountTo, currency) + amount, currency);
    }

    private void checkAmount(Account accountFrom, Double amount, Currency currency) {
        Double amountByCurrency = getAmountByCurrency(accountFrom, currency);
        if (amountByCurrency < amount) {
            LOG.error("Not enough money: amount = {}, need = {}", amountByCurrency, amount);
            throw new NotEnoughMoneyException("Not enough money");
        }
    }

    private Double getAmountByCurrency(Account account, Currency currency) {
        Double amount = null;
        switch (currency) {
            case RUB:
                amount = account.getAmountRUB();
                break;
            case EUR:
                amount = account.getAmountEUR();
                break;
            case USD:
                amount = account.getAmountUSD();
        }
        return amount;
    }

    private void updateAmount(Account account, Double amount, Currency currency) {
        switch (currency) {
            case RUB:
                account.setAmountRUB(amount);
                break;
            case EUR:
                account.setAmountEUR(amount);
                break;
            case USD:
                account.setAmountUSD(amount);
        }
    }
}

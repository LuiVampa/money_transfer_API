package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.transfer.Application;
import ru.transfer.entity.Account;
import ru.transfer.model.Currency;
import ru.transfer.model.TransferMoneyModel;
import ru.transfer.repository.AccountRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Bucky on 15.05.2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
public class TransferMoneyServiceTest {

    private static final  ObjectMapper objectMapper = new ObjectMapper();
    private static Account firstAccount;
    private static Account secondAccount;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        firstAccount = createNewAccount(1_000D);
        secondAccount = createNewAccount(5_000D);
    }

    @Test
    public void testTransfer_Ok() throws Exception {
        //PREPARE
        TransferMoneyModel newTransfer1 = createNewTransfer(
                firstAccount.getId(),
                secondAccount.getId(),
                500D,
                Currency.RUB);

        TransferMoneyModel newTransfer2 = createNewTransfer(
                firstAccount.getId(),
                secondAccount.getId(),
                900D,
                Currency.EUR);

        TransferMoneyModel newTransfer3 = createNewTransfer(
                secondAccount.getId(),
                firstAccount.getId(),
                2_500D,
                Currency.USD);
        //ACT
        performTransfers(newTransfer1, newTransfer2, newTransfer3);
        //ASSERT
        Account firstAccountResult = accountRepository.getOne(firstAccount.getId());
        Account secondAccountResult = accountRepository.getOne(secondAccount.getId());

        Assert.assertTrue(firstAccountResult.getAmountRUB() == 500D);
        Assert.assertTrue(secondAccountResult.getAmountRUB() == 5_500D);
        Assert.assertTrue(firstAccountResult.getAmountEUR() == 100D);
        Assert.assertTrue(secondAccountResult.getAmountEUR() == 5_900D);
        Assert.assertTrue(firstAccountResult.getAmountUSD() == 3_500D);
        Assert.assertTrue(secondAccountResult.getAmountUSD() == 2_500D);
    }

    private void performTransfers(TransferMoneyModel... transfer) throws Exception {
        for (TransferMoneyModel transferMoneyModel : transfer) {
            mvc.perform(
                    put("/money/transfer")
                            .content(objectMapper.writeValueAsBytes(transferMoneyModel))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void testTransfer_NotEnoughMoney() throws Exception {
        //PREPARE
        TransferMoneyModel newTransfer1 = createNewTransfer(
                firstAccount.getId(),
                secondAccount.getId(),
                1_500D,
                Currency.RUB);

        //ACT
        mvc.perform(
                put("/money/transfer")
                        .content(objectMapper.writeValueAsBytes(newTransfer1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough money"));
    }

    @Test
    public void testTransfer_EntityNotFound() throws Exception {
        //PREPARE
        TransferMoneyModel newTransfer1 = createNewTransfer(
                100L,
                secondAccount.getId(),
                500D,
                Currency.RUB);

        //ACT
        mvc.perform(
                put("/money/transfer")
                        .content(objectMapper.writeValueAsBytes(newTransfer1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unable to find ru.transfer.entity.Account with id 100"));
    }

    @Test
    public void testTransfer_SameAccount() throws Exception {
        //PREPARE
        TransferMoneyModel newTransfer1 = createNewTransfer(
                firstAccount.getId(),
                firstAccount.getId(),
                500D,
                Currency.USD);

        //ACT
        mvc.perform(
                put("/money/transfer")
                        .content(objectMapper.writeValueAsBytes(newTransfer1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value: accountFrom = accountTo"));
    }

    @Test
    public void testTransfer_InvalidJSON() throws Exception {
        //PREPARE
        TransferMoneyModel newTransfer1 = createNewTransfer(
                null,
                secondAccount.getId(),
                500D,
                null);

        //ACT
        mvc.perform(
                put("/money/transfer")
                        .content(objectMapper.writeValueAsBytes(newTransfer1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("accountFrom must not be null, " +
                        "currency must not be null"));
    }

    @Test
    public void testTransfer_InvalidJSONValue() throws Exception {
        //PREPARE
        TransferMoneyModel newTransfer1 = createNewTransfer(
                -1L,
                -5L,
                -500D,
                Currency.USD);

        //ACT
        mvc.perform(
                put("/money/transfer")
                        .content(objectMapper.writeValueAsBytes(newTransfer1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("accountFrom must be greater than or equal to 0, " +
                        "accountTo must be greater than or equal to 0, " +
                        "amount must be greater than or equal to 0"));
    }

    private Account createNewAccount(Double initAmount) {
        Account account = new Account();
        account.setAmountRUB(initAmount);
        account.setAmountEUR(initAmount);
        account.setAmountUSD(initAmount);
        return accountRepository.save(account);
    }

    private TransferMoneyModel createNewTransfer(
            Long accountFrom,
            Long accountTo,
            Double amount,
            Currency currency) {
        return TransferMoneyModel.builder()
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .amount(amount)
                .currency(currency)
                .build();
    }
}

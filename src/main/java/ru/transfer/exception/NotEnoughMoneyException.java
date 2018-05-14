package ru.transfer.exception;

/**
 * Created by Bucky on 14.05.2018.
 */
public class NotEnoughMoneyException extends RuntimeException {

    public NotEnoughMoneyException(String msg) {
        super(msg);
    }
}

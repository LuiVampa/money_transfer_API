package ru.transfer.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Bucky on 14.05.2018.
 */

@Entity
@Data
public class Account {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;
    private Double amountRUB;
    private Double amountEUR;
    private Double amountUSD;
}

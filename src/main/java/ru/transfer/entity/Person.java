package ru.transfer.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Bucky on 14.05.2018.
 */

@Entity
@Data
public class Person {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    private String firstName;
    private String lastName;
    private int age;

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    private Set<Account> accounts;
}

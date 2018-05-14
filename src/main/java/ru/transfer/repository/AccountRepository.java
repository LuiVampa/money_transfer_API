package ru.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.transfer.entity.Account;

/**
 * Created by Bucky on 14.05.2018.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
}

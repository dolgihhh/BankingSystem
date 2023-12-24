package com.tofi.bankingsystem.repositiries;

import com.tofi.bankingsystem.entities.BankAccount;
import com.tofi.bankingsystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    boolean existsByNumber(String number);
    Optional<BankAccount> findByNumber(String number);

    //@Query("SELECT ba FROM BankAccounts ba WHERE ba.user = :user AND ba.credit IS NULL AND ba" +
      //      ".saving IS NULL")
    //List<BankAccount> findUserAccountsWithoutCreditAndSaving(@Param("user") User user);
    List<BankAccount> findByUserAndSavingIsNullAndCreditIsNullOrderByOpeningDateDesc(User user);

}

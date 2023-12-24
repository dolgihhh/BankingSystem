package com.tofi.bankingsystem.services;

import com.tofi.bankingsystem.dto.requests.CreditDTO;
import com.tofi.bankingsystem.dto.requests.CreditPaymentDTO;
import com.tofi.bankingsystem.dto.responses.CreditResponseDTO;
import com.tofi.bankingsystem.dto.responses.TransactionResponseDTO;
import com.tofi.bankingsystem.entities.BankAccount;
import com.tofi.bankingsystem.entities.Credit;
import com.tofi.bankingsystem.entities.User;
import com.tofi.bankingsystem.enums.CreditType;
import com.tofi.bankingsystem.exceptions.CreditNotExistsException;
import com.tofi.bankingsystem.exceptions.InsufficientFundsException;
import com.tofi.bankingsystem.exceptions.RecipientAccNotExistsException;
import com.tofi.bankingsystem.exceptions.SenderAccNotExistsException;
import com.tofi.bankingsystem.repositiries.CreditRepository;
import com.tofi.bankingsystem.utils.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final UserService userService;

    @Transactional
    public ResponseEntity<?> takeCredit(CreditDTO creditDTO) throws RecipientAccNotExistsException {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(""));

        BankAccount recipientBankAccount =
                bankAccountService.findBankAccountByNumber(creditDTO.getRecipientBankAccountNumber())
                        .orElseThrow(() -> new RecipientAccNotExistsException("No such " +
                                "recipient bank account"));

        if(!user.equals(recipientBankAccount.getUser())) {
            throw new RecipientAccNotExistsException("No such recipient bank account");
        }

        if(recipientBankAccount.getSaving() != null || recipientBankAccount.getCredit() != null) {
            throw new RecipientAccNotExistsException("No such recipient bank account");
        }

        BankAccount creditBankAccount = bankAccountService.createBankAccountInDB();

        Credit newCredit = new Credit();
        newCredit.setBankAccount(creditBankAccount);
        newCredit.setAmount(creditDTO.getAmount());
        newCredit.setCreditType(creditDTO.getCreditType());
        BigDecimal monthlyPayment = calculateMonthlyPayment(creditDTO.getAmount(),
                creditDTO.getInterestRate(), creditDTO.getTerm(), creditDTO.getCreditType());
        newCredit.setMonthlyPayment(monthlyPayment);
        newCredit.setBalanceOfMainDebt(creditDTO.getCreditType() == CreditType.ANNUITY ? null :
                creditDTO.getAmount());
        BigDecimal debtBalance = calculateDebtBalance(creditDTO.getAmount(), monthlyPayment,
                creditDTO.getInterestRate(), creditDTO.getTerm(), creditDTO.getCreditType());

        BigDecimal b = (creditDTO.getAmount().divide(BigDecimal.valueOf(creditDTO.getTerm()),
                MathContext.DECIMAL128)).setScale(2, RoundingMode.CEILING);

        newCredit.setMonthlyMainDebtPayment(creditDTO.getCreditType() == CreditType.ANNUITY ? null : b);
        newCredit.setBalanceOfDebt(debtBalance);
        newCredit.setTerm(creditDTO.getTerm());
        newCredit.setUser(user);
        newCredit.setInterestRate(creditDTO.getInterestRate());
        newCredit.setLoanPurpose(creditDTO.getLoanPurpose());

        creditRepository.save(newCredit);

        transactionService.takeCreditTransaction(creditBankAccount,
                recipientBankAccount, creditDTO.getAmount(), debtBalance);

        CreditResponseDTO creditResponseDTO = getCreditResponseDTO(newCredit);

        Map<String, Object> map = new HashMap<>();
        map.put("message", "Credit successfully taken");
        map.put("credit_info", creditResponseDTO);
        //System.out.println(newCredit);
        //return ResponseHandler.generateResponse("Credit successfully taken", HttpStatus.OK);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    private static CreditResponseDTO getCreditResponseDTO(Credit newCredit) {
        CreditResponseDTO creditResponseDTO = new CreditResponseDTO();
        creditResponseDTO.setCreditType(newCredit.getCreditType());
        creditResponseDTO.setTerm(newCredit.getTerm());
        creditResponseDTO.setRepaid(newCredit.isRepaid());
        creditResponseDTO.setAmount(newCredit.getAmount());
        creditResponseDTO.setInterestRate(newCredit.getInterestRate());
        creditResponseDTO.setBalanceOfDebt(newCredit.getBalanceOfDebt());
        creditResponseDTO.setBalanceOfMainDebt(newCredit.getBalanceOfMainDebt());
        creditResponseDTO.setMonthlyPayment(newCredit.getMonthlyPayment());
        creditResponseDTO.setMonthlyMainDebtPayment(newCredit.getMonthlyMainDebtPayment());
        creditResponseDTO.setLoanPurpose(newCredit.getLoanPurpose());
        creditResponseDTO.setStartDate(newCredit.getStartDate());
        creditResponseDTO.setId(newCredit.getId());

        return creditResponseDTO;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal creditAmount, BigDecimal interestRate,
                                               Integer term, CreditType creditType) {
        BigDecimal monthlyPayment;
        BigDecimal P = interestRate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128)
                .multiply(BigDecimal.valueOf(0.01));

        if(creditType == CreditType.ANNUITY) {
            BigDecimal denom = ((BigDecimal.ONE.add(P)).pow(term)).subtract(BigDecimal.ONE);
            monthlyPayment = creditAmount.multiply(P.add((P.divide(denom, MathContext.DECIMAL128))));
            monthlyPayment = monthlyPayment.setScale(2, RoundingMode.CEILING);
        } else {
            BigDecimal b = (creditAmount.divide(BigDecimal.valueOf(term), MathContext.DECIMAL128))
                    .setScale(2, RoundingMode.CEILING);
            BigDecimal I = (creditAmount.multiply(P)).setScale(2, RoundingMode.CEILING);
            monthlyPayment = b.add(I);
        }

        return monthlyPayment;
    }

    private BigDecimal calculateDebtBalance(BigDecimal creditAmount, BigDecimal monthlyPayment,
                                            BigDecimal interestRate, Integer term, CreditType creditType) {
        if(creditType == CreditType.ANNUITY) {
            return monthlyPayment.multiply(BigDecimal.valueOf(term));
        } else {
            BigDecimal debtBalance = BigDecimal.ZERO;
            BigDecimal b = (creditAmount.divide(BigDecimal.valueOf(term), MathContext.DECIMAL128))
                    .setScale(2, RoundingMode.CEILING);
            BigDecimal P = interestRate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128)
                    .multiply(BigDecimal.valueOf(0.01));

            for(int i = 0; i < term; i++) {
                BigDecimal I = (creditAmount.multiply(P)).setScale(2, RoundingMode.CEILING);
                BigDecimal monthPay;
                if(creditAmount.subtract(b).compareTo(BigDecimal.ZERO) < 0) {
                    monthPay = creditAmount.add(I);
                    creditAmount = BigDecimal.ZERO;
                } else {
                    creditAmount = creditAmount.subtract(b);
                    monthPay = b.add(I);
                }
                debtBalance = debtBalance.add(monthPay);
            }

            return debtBalance;
        }
    }

    @Transactional
    public ResponseEntity<?> makeCreditPayment(CreditPaymentDTO creditPaymentDTO)
            throws SenderAccNotExistsException, CreditNotExistsException, InsufficientFundsException {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(""));

        BankAccount senderBankAccount =
                bankAccountService.findBankAccountByNumber(creditPaymentDTO.getBankAccountNumber())
                        .orElseThrow(() -> new SenderAccNotExistsException("You don't have such а" +
                                "bank account"));

        if(!user.equals(senderBankAccount.getUser())) {
            throw new SenderAccNotExistsException("You don't have such а bank account");
        }

        if(senderBankAccount.getSaving() != null || senderBankAccount.getCredit() != null) {
            throw new SenderAccNotExistsException("You don't have such а bank account");
        }

        Credit credit = findCreditById(creditPaymentDTO.getCreditId())
                        .orElseThrow(() -> new CreditNotExistsException("You don't have such а" +
                                "credit"));

        if(!user.equals(credit.getUser())) {
            throw new CreditNotExistsException("You don't have such а credit");
        }

        if(credit.isRepaid()) {
            throw new CreditNotExistsException("You have already repaid this credit");
        }

        if (senderBankAccount.getBalance().compareTo(credit.getMonthlyPayment()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the bank account");
        }


        BigDecimal monthlyPayment = credit.getMonthlyPayment();
        BigDecimal newDebt = credit.getBalanceOfDebt().subtract(monthlyPayment);
        credit.setBalanceOfDebt(newDebt);
        creditRepository.save(credit);

        transactionService.creditPaymentTransaction(credit.getBankAccount(), senderBankAccount,
                monthlyPayment);

        if(credit.getCreditType() == CreditType.DIFF) {
            BigDecimal P = credit.getInterestRate().divide(BigDecimal.valueOf(12), MathContext.DECIMAL128)
                    .multiply(BigDecimal.valueOf(0.01));

            BigDecimal newMainDebt = credit.getBalanceOfMainDebt().subtract(credit.getMonthlyMainDebtPayment());
            credit.setBalanceOfMainDebt(newMainDebt);
            BigDecimal I = (newMainDebt.multiply(P)).setScale(2, RoundingMode.CEILING);
            BigDecimal newMonthPayment = credit.getMonthlyMainDebtPayment().add(I);
            credit.setMonthlyPayment(newMonthPayment);
            creditRepository.save(credit);

            if(credit.getBalanceOfMainDebt().subtract(credit.getMonthlyMainDebtPayment()).compareTo(BigDecimal.ZERO) < 0
                    && credit.getBalanceOfMainDebt().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal difference =
                        credit.getMonthlyMainDebtPayment().subtract(credit.getBalanceOfMainDebt());
                credit.setMonthlyPayment(credit.getMonthlyPayment().subtract(difference));
                credit.setMonthlyMainDebtPayment(credit.getMonthlyMainDebtPayment().subtract(difference));
                creditRepository.save(credit);
            }
        }

        if(newDebt.compareTo(BigDecimal.ZERO) == 0) {
            credit.setRepaid(true);
            creditRepository.save(credit);
            return ResponseHandler.generateResponse("Credit repaid", HttpStatus.OK);
        }

        CreditResponseDTO creditResponseDTO = getCreditResponseDTO(credit);
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Successful payment");
        map.put("credit_info", creditResponseDTO);

        return new ResponseEntity<>(map, HttpStatus.OK);
        //return ResponseHandler.generateResponse("Successful payment", HttpStatus.OK);
    }

    public Optional<Credit> findCreditById(Long id) {

        return creditRepository.findById(id);
    }

    public ResponseEntity<?> getCredits() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(""));

        List<Credit> credits = user.getCredits();
        List<CreditResponseDTO> creditsResponseDTO = new ArrayList<>();

        if (credits != null) {
            for (Credit credit : credits) {
                creditsResponseDTO.add(getCreditResponseDTO(credit));
            }
        }
        creditsResponseDTO.sort(Comparator.comparing(CreditResponseDTO::getStartDate).reversed());

        return ResponseEntity.status(HttpStatus.OK).body(creditsResponseDTO);
    }
}

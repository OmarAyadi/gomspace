package com.example.gomspace.model;

import com.example.gomspace.dto.OperationDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "to_account_id", referencedColumnName = "id")
    private Account toAccount;

    @ManyToOne
    @JoinColumn(name = "from_account_id", referencedColumnName = "id")
    private Account fromAccount;

    private double amount;

    private double currencyExchangeRate;

    private double evaluatedAmount;

    @Column(nullable = false)
    private String operationCurrency;

    @Column(nullable = false)
    private String type;

    @Builder.Default
    private Instant initiationDate = Instant.now();

    @Builder.Default
    private Instant completionDate = Instant.now();


    public static TransactionBuilder of(final Account account, final OperationDto operationDto, final double rate) {
        return Transaction.builder()
                .account(account)
                .operationCurrency(operationDto.getCurrency())
                .amount(operationDto.getAmount())
                .currencyExchangeRate(rate)
                .evaluatedAmount(rate * operationDto.getAmount());
    }
}

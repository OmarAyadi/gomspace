package com.example.gomspace.model;

import com.example.gomspace.model.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

import static com.example.gomspace.utils.Constants.INSUFFICIENT_BALANCE;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private double balance;

    @Column(nullable = false)
    private AccountType type;

    private Instant createdAt = Instant.now();

    public void addAmountToBalance(final double amount) {
        balance += amount;

        if (balance < 0) {
            ClientException.throwBadRequest(INSUFFICIENT_BALANCE);
        }
    }
}
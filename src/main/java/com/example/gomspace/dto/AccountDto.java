package com.example.gomspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AccountDto {

    @NotNull
    private Long ownerId;

    @NotBlank
    private String currency;

    @NotBlank
    private String type;
}

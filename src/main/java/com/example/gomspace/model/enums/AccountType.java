package com.example.gomspace.model.enums;

import com.example.gomspace.model.ClientException;
import com.example.gomspace.utils.StringTemplates;

import java.util.Map;

import static com.example.gomspace.utils.Constants.ACCOUNT_TYPE;
import static com.example.gomspace.utils.Utils.getAndSortKeys;
import static com.example.gomspace.utils.Utils.valuesToMap;

public enum AccountType {
    CREDIT,
    DEBIT;

    public final static Map<String, AccountType> possibleAccounts = valuesToMap(AccountType.values());

    public static AccountType getAccountType(final String type) throws ClientException {
        if (!possibleAccounts.containsKey(type)) {
            ClientException.throwBadRequest(
                    StringTemplates.invalidValue(
                            type,
                            ACCOUNT_TYPE,
                            getAndSortKeys(possibleAccounts)
                    )
            );
        }

        return possibleAccounts.get(type);
    }
}

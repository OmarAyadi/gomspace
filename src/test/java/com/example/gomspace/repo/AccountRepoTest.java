package com.example.gomspace.repo;

import com.example.gomspace.mock.AccountMock;
import com.example.gomspace.repos.AccountRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.example.gomspace.mock.AccountMock.VALID_OWNER_ID;
import static com.example.gomspace.repo.UtilMock.VALID_PAGINATION_MOCK;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AccountRepoTest {
    @Autowired
    private AccountRepo accountRepo;

    @BeforeEach
    public void setUp() {
        accountRepo.save(AccountMock.mockAccount());
    }

    @AfterEach
    public void cleanUp() {
        accountRepo.deleteAll();
    }

    @Nested
    class FindByOwnerIdTest {
        @DisplayName("no accounts found for the given owner id")
        @Test
        public void returnEmptyValues() {
            final var accounts = accountRepo
                    .findByOwnerId(0L, VALID_PAGINATION_MOCK);

            assertThat(accounts).hasSize(0);
        }


        @DisplayName("an account is found for the given owner id")
        @Test
        public void returnValidAccountsForOwner() {
            final var accounts = accountRepo
                    .findByOwnerId(VALID_OWNER_ID, VALID_PAGINATION_MOCK);

            assertThat(accounts).hasSize(1);
        }
    }
}

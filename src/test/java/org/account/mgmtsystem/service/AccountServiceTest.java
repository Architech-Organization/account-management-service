package org.account.mgmtsystem.service;

import org.account.mgmtsystem.data.StatusType;
import org.account.mgmtsystem.data.entities.Account;
import org.account.mgmtsystem.data.repository.AccountRepository;
import org.account.mgmtsystem.service.dto.AccountCreateResponse;
import org.account.mgmtsystem.service.dto.AccountDTO;
import org.account.mgmtsystem.service.dto.AccountUpdateResponse;
import org.account.mgmtsystem.service.dto.ZippopotamResponse;
import org.account.mgmtsystem.utils.CustomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomGenerator customGenerator;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given valid account data passed, it should return Account Response with same data and save account")
    void createAccount_ShouldReturnAccountCreateResponse() {
        // Given
        AccountDTO accountDTO = new AccountDTO("Test User","test@example.com", "DE", "10115", 25, "Active");
        ZippopotamResponse zippopotamResponse = createMockZippopotamResponse();
        Account savedAccount = mapDataToAccount(accountDTO, zippopotamResponse);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(ZippopotamResponse.class)))
                .thenReturn(ResponseEntity.ok(zippopotamResponse));
        MockedStatic<CustomGenerator> customGeneratorMock = Mockito.mockStatic(CustomGenerator.class);
        customGeneratorMock.when(CustomGenerator::generateSecurityPin).thenReturn("1212");

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // When
        AccountCreateResponse response = accountService.createAccount(accountDTO);

        // Then
        assertNotNull(response);
        assertEquals("Active", response.getStatus());
        verify(accountRepository, times(1)).save(savedAccount);
    }

    @Test
    @DisplayName("Given an ID is passed to getAccountById, it should return an account with same ID")
    void getAccountById_ShouldReturnAccount() {
        // Given
        String accountId = "123";
        Account account = new Account();
        account.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // When
        Optional<Account> result = accountService.getAccountById(accountId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(accountId, result.get().getId());
    }

    @Test
    @DisplayName("Given an unavailable ID is passed to getAccountById, it should not return any account")
    void getAccountById_ShouldReturnEmpty_WhenAccountNotFound() {
        // Given
        String accountId = "123";

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // When
        Optional<Account> result = accountService.getAccountById(accountId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Given passed account has valid data and in Active status, it should successfully update")
    void updateAccount_ShouldUpdateAccount_WhenStatusIsActive() {
        // Given
        AccountDTO accountDTO = new AccountDTO("Test User","test@example.com", "DE", "10115", 25, "Active");
        Account existingAccount = new Account();
        existingAccount.setStatus(StatusType.ACTIVE.getValue());
        existingAccount.setEmail("test@example.com");
        existingAccount.setCountry("DE");
        existingAccount.setPostalCode("10115");

        when(accountRepository.findByEmail(accountDTO.getEmail())).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        // When
        var response = accountService.updateAccount(accountDTO);
        var expectedResponse = new AccountUpdateResponse(HttpStatus.OK, "Account updated for Email: " + accountDTO.getEmail());

        // Then
        assertThat(response.getStatus()).isEqualTo(expectedResponse.getStatus());
        assertThat(response.getMessage()).isEqualTo(expectedResponse.getMessage());
        verify(accountRepository, times(1)).save(existingAccount);
    }

    @Test
    @DisplayName("Given passed account has valid data bit not Active status, it should not update")
    void updateAccount_ShouldReturnErrorMessage_WhenStatusIsNotActive() {
        // Given
        AccountDTO accountDTO = new AccountDTO("test@example.com", "Test User", "DE", "10115", 25, "Inactive");
        Account existingAccount = new Account();
        existingAccount.setStatus(StatusType.INACTIVE.getValue());

        when(accountRepository.findByEmail(accountDTO.getEmail())).thenReturn(Optional.of(existingAccount));

        // When
        var response = accountService.updateAccount(accountDTO);
        var expectedResponse = new AccountUpdateResponse(HttpStatus.BAD_REQUEST, "Status of the account must be Active while updating");

        // Then
        assertThat(response.getStatus()).isEqualTo(expectedResponse.getStatus());
        assertThat(response.getMessage()).isEqualTo(expectedResponse.getMessage());
        verify(accountRepository, never()).save(existingAccount);
    }

    @Test
    @DisplayName("Given passed account has valid ID for deletion, it should delete and return confirmation message")
    void deleteAccountById_ShouldReturnConfirmationMessage() {
        // Given
        String accountId = "123";

        doNothing().when(accountRepository).deleteById(accountId);

        // When
        String result = accountService.deleteAccountById(accountId);

        // Then
        assertThat(result).isEqualTo(String.format("Account deleted - ID: %s", accountId));
        verify(accountRepository, times(1)).deleteById(accountId);
    }

    private ZippopotamResponse createMockZippopotamResponse() {
        ZippopotamResponse response = new ZippopotamResponse();
        ZippopotamResponse.Place place = new ZippopotamResponse.Place();
        place.setPlaceName("Berlin");
        place.setStateAbbreviation("BE");
        place.setLongitude("13.4050");
        place.setLatitude("52.5200");

        response.setPlaces(List.of(place));
        return response;
    }

    private Account mapDataToAccount(AccountDTO accountDTO, ZippopotamResponse zippopotamResponse) {
        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setEmail(accountDTO.getEmail());
        account.setCountry(accountDTO.getCountry());
        account.setPostalCode(accountDTO.getPostalCode());
        account.setAge(accountDTO.getAge());
        account.setStatus(accountDTO.getStatus());

        var place = zippopotamResponse.getPlaces().get(0);
        account.setPlace(place.getPlaceName());
        account.setState(place.getStateAbbreviation());
        account.setLongitude(place.getLongitude());
        account.setLatitude(place.getLatitude());
        account.setSecurityPin("1212");

        return account;
    }
}

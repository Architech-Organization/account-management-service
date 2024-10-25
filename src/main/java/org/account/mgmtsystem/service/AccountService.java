package org.account.mgmtsystem.service;

import org.account.mgmtsystem.config.Config;
import org.account.mgmtsystem.data.StatusType;
import org.account.mgmtsystem.data.entities.Account;
import org.account.mgmtsystem.data.repository.AccountRepository;
import org.account.mgmtsystem.service.dto.*;
import org.account.mgmtsystem.utils.CustomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import org.account.mgmtsystem.service.dto.AccountsCountDTO.*;

@Service
public class AccountService {

    private static final Logger logger = LogManager.getLogger(AccountService.class);

    private RestTemplate restTemplate;

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(@Qualifier(Config.ZIPPOPOTAM_REST_TEMPLATE) RestTemplate restTemplate, AccountRepository accountRepository) {
        this.restTemplate = restTemplate;
        this.accountRepository = accountRepository;
    }

    public AccountCreateResponse createAccount(AccountDTO accountDTO) {
        // Fetch location details from Zippopotam
        ZippopotamResponse zippopotamResponse = fetchZippopotamData(accountDTO.getCountry(), accountDTO.getPostalCode());

        // Map AccountDTO and ZippopotamResponse to Account entity
        Account account = mapResponseToAccount(accountDTO, zippopotamResponse);

        // save account
        var newAccount = accountRepository.save(account);

        // return curated account with few fields
        return AccountCreateResponse.builder()
                .id(newAccount.getId())
                .status(newAccount.getStatus())
                .securityPin(newAccount.getSecurityPin())
                .build();
    }

    public Optional<Account> getAccountById(String id) {
        return accountRepository.findById(id);
    }

    public AccountUpdateResponse updateAccount(AccountDTO accountDTO) {
        Optional<Account> promisedAccount = accountRepository.findByEmail(accountDTO.getEmail());
        if (promisedAccount.isEmpty()) {
            return new AccountUpdateResponse(HttpStatus.NOT_FOUND, "Account not found");
        }

        Account accountToUpdate = promisedAccount.get();
        if(!StatusType.ACTIVE.getValue().equals(accountToUpdate.getStatus())) {
            return new AccountUpdateResponse(HttpStatus.BAD_REQUEST, "Status of the account must be Active while updating");
        }

        // if both postal code and country match then update the account with the account passed
        if(accountToUpdate.getCountry().equals(accountDTO.getCountry()) &&
                accountToUpdate.getPostalCode().equals(accountDTO.getPostalCode())) {

            accountToUpdate.setName(accountDTO.getName());
            accountToUpdate.setAge(accountDTO.getAge());
            accountToUpdate.setEmail(accountDTO.getEmail());
            accountToUpdate.setStatus(accountDTO.getStatus());

            accountRepository.save(accountToUpdate);
            // return to avoid fetching data in the next step
            return new AccountUpdateResponse(HttpStatus.OK,
                    String.format("Account updated for Email: %s", accountToUpdate.getEmail()));
        }

        // otherwise fetch new data and update the account with it
        ZippopotamResponse zippopotamResponse = fetchZippopotamData(accountDTO.getCountry(), accountDTO.getPostalCode());
        Account updatedAccount = mapResponseToAccount(accountDTO, zippopotamResponse);
        accountRepository.save(updatedAccount);
        return new AccountUpdateResponse(HttpStatus.OK,
                String.format("Account updated with new region for Email: ", accountToUpdate.getEmail()));
    }

    public String deleteAccountById(String id) {
        accountRepository.deleteById(id);
        return String.format("Account deleted - ID: %s", id);
    }

    private ZippopotamResponse fetchZippopotamData(String country, String postalCode) {
        ZippopotamResponse responseBody = null;
        try {
            ResponseEntity<ZippopotamResponse> response = restTemplate.exchange(
                    buildZippopotamUrl(country, postalCode), HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()), ZippopotamResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                responseBody = response.getBody();
            } else {
                logger.error("Invalid data from Zippopotam service");
            }
        } catch(Exception ex) {
            logger.error("Postal code is invalid or Zippopotam service is unavailable", ex);
        }

        return responseBody;
    }

    private String buildZippopotamUrl(String country, String postalCode) {
        return "https://api.zippopotam.us/" + country.toLowerCase() + "/" + postalCode;
    }

    private Account mapResponseToAccount(AccountDTO accountDTO, ZippopotamResponse zippopotamResponse) {
        Account account = new Account();
        account.setName(accountDTO.getName());
        account.setEmail(accountDTO.getEmail());
        account.setCountry(accountDTO.getCountry());
        account.setPostalCode(accountDTO.getPostalCode());
        account.setAge(accountDTO.getAge());
        account.setStatus(accountDTO.getStatus());


        if(zippopotamResponse != null && !zippopotamResponse.getPlaces().isEmpty()) {
            var place = zippopotamResponse.getPlaces().get(0);
            account.setPlace(place.getPlaceName());
            account.setState(place.getStateAbbreviation());
            account.setLongitude(place.getLongitude());
            account.setLatitude(place.getLatitude());
        }

        account.setSecurityPin(CustomGenerator.generateSecurityPin());

        return account;
    }

    public AccountsCountDTO getCountsByCountry(String country) {
        List<StatePlaceCount> stateEntries = accountRepository.findCountsByCountry(country);

        // Initialize a map to group counts by state
        Map<String, StateCounts> stateToPlacesMap = new HashMap<>();

        // Process each state and place
        for (StatePlaceCount entry : stateEntries) {
            String state = entry.getState();
            String place = entry.getPlace();
            long userCount = entry.getCount();

            // If the state doesn't exist in the map yet, create it
            stateToPlacesMap.putIfAbsent(state, new StateCounts(state, 0, new ArrayList<>()));

            // Add the place count to the state's list of places
            StateCounts stateCount = stateToPlacesMap.get(state);
            stateCount.getPlaces().add(new PlaceCounts(place, userCount));

            // Increment the total count for the state
            stateCount.setCount(stateCount.getCount() + userCount);
        }

        // Create the final response object
        List<StateCounts> stateCountsList = new ArrayList<>(stateToPlacesMap.values());
        long totalCount = stateCountsList.stream().mapToLong(StateCounts::getCount).sum();

        AccountsCountDTO response = new AccountsCountDTO(country, stateToPlacesMap.size(), stateCountsList);

        return response;
    }
}

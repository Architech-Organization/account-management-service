package org.account.mgmtsystem.resource;

import org.account.mgmtsystem.data.StatusType;
import org.account.mgmtsystem.data.entities.Account;
import org.account.mgmtsystem.service.AccountService;
import org.account.mgmtsystem.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountResource {

    private final AccountService accountService;

    @Autowired
    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("create")
    public ResponseEntity<AccountCreateResponse> createAccount(@RequestBody AccountDTO accountDTO) {
        // Allow only “Requested” status on POST operation
        if (!StatusType.REQUESTED.getValue().equalsIgnoreCase(accountDTO.getStatus())) {
            throw new IllegalArgumentException("Status must be 'Requested' during creation");
        }
        // On creation all Accounts are defaulted to “Active” status
        accountDTO.setStatus(StatusType.ACTIVE.getValue());

        return ResponseEntity.ok(accountService.createAccount(accountDTO));
    }

    @GetMapping("{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable String id) {
        var account = accountService.getAccountById(id);
        return account.isPresent() ? ResponseEntity.ok(account.get()) : ResponseEntity.notFound().build();
    }

    @PutMapping("update")
    public ResponseEntity<String> updateAccount(AccountDTO accountDTO) {
        AccountUpdateResponse response = accountService.updateAccount(accountDTO);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> deleteAccount(@RequestBody AccountDeleteDTO dto) {
        var optionalAccount = accountService.getAccountById(dto.getId());
        if(optionalAccount.isEmpty()) {
            return ResponseEntity.ok(String.format("Account with ID: %s not found", dto.getId()));
        }
        if(!dto.getSecurityPin().equals(optionalAccount.get().getSecurityPin())) {
            return ResponseEntity.ok(String.format("Invalid PIN for ID: %s", dto.getId()));
        }

        return ResponseEntity.ok(accountService.deleteAccountById(dto.getId()));
    }

    @GetMapping("/counts")
    public ResponseEntity<AccountsCountDTO> getCountsByCountry(@RequestParam String country) {
        AccountsCountDTO response = accountService.getCountsByCountry(country);
        return ResponseEntity.ok(response);
    }
}

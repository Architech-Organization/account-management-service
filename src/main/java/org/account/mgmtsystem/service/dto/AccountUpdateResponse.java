package org.account.mgmtsystem.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.account.mgmtsystem.data.entities.Account;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AccountUpdateResponse {

    private HttpStatus status;

    private String message;

    private Account updatedAccount;

    public AccountUpdateResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

//    public AccountUpdateResponse(HttpStatus status, Account updatedAccount) {
//        this.status = status;
//        this.updatedAccount = updatedAccount;
//    }
}

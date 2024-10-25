package org.account.mgmtsystem.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class AccountCreateResponse {
    private String id;
    private String status;
    private String securityPin;
}

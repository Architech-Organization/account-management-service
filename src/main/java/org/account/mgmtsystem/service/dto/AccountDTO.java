package org.account.mgmtsystem.service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString(callSuper=true)
@JsonPropertyOrder({ "name", "age", "email", "country", "postalCode", "status"})
public class AccountDTO {
    private String name;
    private String email;
    private String country;
    private String postalCode;
    private Integer age; // Optional
    private String status;
}


package org.account.mgmtsystem.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {

    @Id
    @GeneratedValue(generator = "custom-generator")
    @GenericGenerator(name = "custom-generator", strategy = "org.account.mgmtsystem.utils.CustomGenerator")
    private String id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 20, message = "Name can be at most 20 characters")
    private String name;

    @Column(nullable = false, unique = true)
    @Email(message = "Email format must be valid")
    private String email;

    @NotBlank(message = "Country is mandatory")
    @Pattern(regexp = "^(US|DE|ES|FR)$", message = "Country values accepted: US, DE, ES, FR")
    private String country;

    @NotBlank(message = "Postal Code is mandatory")
    @Pattern(regexp = "^\\d{5}$", message = "Postal Code must be 5-digit number only")
    private String postalCode;

    private int age;

    private String status;

    // Retrieved from Zippopotam
    private String place;
    private String state;
    private String longitude;
    private String latitude;

    // 4-digit pin
    private String securityPin;
}

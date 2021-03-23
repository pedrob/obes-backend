package com.obes.backend.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @NotBlank
    @Column(unique = true)
    @Size(min = 16, max = 16)
    private String number;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String expirationDate;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 3)
    private String cvv;

    @OneToOne(mappedBy = "creditCard")
    private ApplicationUser user;

    public Long getUser() {
        return this.user.getId();
    }

}

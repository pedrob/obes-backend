package com.obes.backend.model;

import lombok.*;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


import javax.validation.constraints.Min;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String author;

    private String imageUrl;

    @Column(length = 280)
    private String description;
    
    @Min(0)
    private Float price;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "owner_id")
    private ApplicationUser owner;

    private Date createdAt;

    public Long getOwner() {
        return this.owner.getId();
    }

    public Long getPurchase() {
        if(this.purchase == null) {
            return null;
        } else {
            return this.purchase.getId(); 
        }
    }

}

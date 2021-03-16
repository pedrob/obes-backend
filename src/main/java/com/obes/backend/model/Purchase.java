package com.obes.backend.model;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.*;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    private List<Book> books;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "buyer_id")
    private ApplicationUser buyer;

    private Date createdAt;

    public Long getBuyer() {
        return this.buyer.getId();
    }

}

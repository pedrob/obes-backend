package com.obes.backend.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class PurchaseBodyRequest {
    private List<Long> booksIds;
}

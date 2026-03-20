package com.mase.cafe.system.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingItemDTO {
    private String name;
    private Long totalSold;
    private Double percentage;

    public TopSellingItemDTO(String name, Long totalSold) {
        this.name = name;
        this.totalSold = totalSold;
    }
}

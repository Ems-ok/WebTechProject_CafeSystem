package com.mase.cafe.system.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDTO {
    private Long id;
    private LocalDate menuDate;
    private Set<ItemDTO> items;
}
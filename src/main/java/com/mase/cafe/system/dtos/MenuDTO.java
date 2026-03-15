package com.mase.cafe.system.dtos;

import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class MenuDTO {
    private Long id;
    private LocalDate menuDate;
    private Set<ItemDTO> items;
}
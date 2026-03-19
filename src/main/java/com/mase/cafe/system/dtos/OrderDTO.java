package com.mase.cafe.system.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String ordername;
    private Double totalAmount;
    private LocalDateTime orderTimestamp;
    private String username;
}
package com.mase.cafe.system.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO implements Serializable {
    
    private Long id;
    private String username;
    
    // We include password so we can send it TO the server for creation.
    // We will ensure the server doesn't send it back to us.
    private String password; 
    
    private String role;
}
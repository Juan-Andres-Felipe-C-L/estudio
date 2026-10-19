package com.JAFCL.Study.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    
    private String jwt;

    private UserDTO user;
}

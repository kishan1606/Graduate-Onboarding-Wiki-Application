package com.cooksys.groupfinal.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserResponseDto {
    private ProfileDto profile;
    private String teamName;
    private boolean active;
    private boolean admin;
    private String status;
}

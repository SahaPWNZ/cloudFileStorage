package com.sahapwnz.cloudfilestorage.dto;


import com.sahapwnz.cloudfilestorage.util.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PasswordMatches
public class UserRequestDTO {
//    @ValidEmail
    private String login;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
}

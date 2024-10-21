package com.sahapwnz.cloudfilestorage.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
    @NotEmpty
    private String confirmPassword;
}

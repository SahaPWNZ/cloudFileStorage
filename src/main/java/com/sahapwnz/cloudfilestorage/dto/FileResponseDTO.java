package com.sahapwnz.cloudfilestorage.dto;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponseDTO {
    private String prefix;
    private String name;
}

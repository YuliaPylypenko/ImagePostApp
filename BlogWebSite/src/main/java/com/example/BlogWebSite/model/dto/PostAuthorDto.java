package com.example.BlogWebSite.model.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO for {BlogWebSite.model.User}
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class PostAuthorDto {
    @NotNull
    Long id;
    @NotEmpty
    String userName;

    public PostAuthorDto(String userName) {
    }
}

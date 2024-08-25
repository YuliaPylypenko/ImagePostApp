package com.example.BlogWebSite.model.dto;

import lombok.*;

import java.time.ZonedDateTime;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SearchPostDto {
    private Long id;
    private String title;
    private PostAuthorDto author;
    private ZonedDateTime creationDate;
}

package com.example.BlogWebSite.model.dto;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private ZonedDateTime createdAt;
    private PostAuthorDto author;
    private String titleImage;
    private List<String> additionalImages;
}

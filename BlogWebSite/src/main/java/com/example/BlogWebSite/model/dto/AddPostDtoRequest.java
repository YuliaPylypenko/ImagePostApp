package com.example.BlogWebSite.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class AddPostDtoRequest {
    private Long id;
    @NotEmpty
    @Size(min = 10, max = 70)
    private String title;
    @NotEmpty
    @Size(min = 20, max = 63206)
    private String content;
}

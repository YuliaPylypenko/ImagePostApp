package com.example.BlogWebSite.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.validation.constraints.Pattern;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Column
    @Pattern(
            message = "The content should be at least 20 and not more than 63 206 characters long.",
            regexp = "^.{20,63206}$")
    String content;

    @Column(name = "created_at")
    @CreationTimestamp
    private final ZonedDateTime createdAt = ZonedDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

}

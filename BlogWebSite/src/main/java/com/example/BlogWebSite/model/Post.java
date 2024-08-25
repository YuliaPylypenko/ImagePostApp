package com.example.BlogWebSite.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.validation.constraints.Pattern;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Indexed
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @Column
    @Pattern(
            message = "The title should be at least 5 and not more than 70 characters long.",
            regexp = "^.{5,69}$")
    @FullTextField
    String title;

    @Column
    @Pattern(
            message = "The content should be at least 20 and not more than 63 206 characters long.",
            regexp = "^.{20,63206}$")
    String content;

    @Column(name = "created_at")
    @CreationTimestamp
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Pattern(
            message = "The link must start with http(s)://",
            regexp = "^(https://|http://).*")
    @Column(name = "title_image")
    private String titleImage;

    @ElementCollection
    @CollectionTable(name = "post_additional_images", joinColumns = @JoinColumn(name = "post_id"))
    private List<@Pattern(
            message = "The link must start with http(s)://",
            regexp = "^(https://|http://).*") String> additionalImages;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> postComments;

}

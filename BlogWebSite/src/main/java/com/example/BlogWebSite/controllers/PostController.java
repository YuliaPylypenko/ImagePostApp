package com.example.BlogWebSite.controllers;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.BlogWebSite.annotations.ApiPageable;
import com.example.BlogWebSite.annotations.CurrentUser;
import com.example.BlogWebSite.constant.HttpStatuses;
import com.example.BlogWebSite.interfaces.PostService;
import com.example.BlogWebSite.model.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/posts")
@Log4j2

public class PostController {

    private final PostService postService;
    private final ElasticsearchClient client;


    private static final Logger logger = LogManager.getLogger(PostController.class);

    public PostController(PostService postService, ElasticsearchClient client) {
        this.postService = postService;
        this.client = client;
    }

    /**
     * Method for creating Post.
     *
     * @param addPostDtoRequest - dto for creating Post entity.
     * @return dto {@link PostDto} instance.
     */

    @Operation(description = "Create a post with optional images.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)})
    @PostMapping(value = "/create", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseEntity<Object> createPost(
            @Parameter @RequestPart @Valid AddPostDtoRequest addPostDtoRequest, BindingResult bindingResult,
            @Parameter(description = "Images for post") @RequestPart(required = false) MultipartFile[] images,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(new ValidationExceptionDto("Validation error", errors));
        }
        PostDto savedPost = postService.save(addPostDtoRequest, images, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }


    /**
     * Method for deleting post.
     */
    @Operation(description = "Delete post.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)})
    @DeleteMapping(path = "/delete/{postId}")
    public ResponseEntity<Object> delete(@PathVariable Long postId, @Parameter(hidden = true) @CurrentUser UserVO user) {
        postService.delete(postId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for getting post by id.
     *
     * @return {@link PostDto} instance
     */
    @Operation(description = "Get the post.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)})
    @GetMapping(path = "/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.findById(postId));
    }

    /**
     * Method for getting all posts.
     *
     * @return PageableDto of {@link PostDto} instances.
     */
    @Operation(description = "Get all posts.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)})
    @GetMapping("")
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<PostDto>> getPost(@Parameter(hidden = true) Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.findAll(page));
    }

    /**
     * Method for getting all posts created by user.
     *
     * @return PageableDto of {@link PostDto} instances.
     */
    @Operation(description = "Get posts created by user.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)})
    @GetMapping("/myPosts")
    @ApiPageable
    public ResponseEntity<PageableAdvancedDto<PostDto>> getPostsCreatedByUser(@Parameter(hidden = true) Pageable page,
                                                                              @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(postService.findAllByUser(user, page));
    }

    @Operation(description = "Update post.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = PostDto.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)})

    @PutMapping(path = "/update",
            consumes = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostDto> update(
            @Parameter(required = true) @Valid @RequestPart AddPostDtoRequest addPostDtoRequest,
            @Parameter(description = "Image for post") @RequestPart(required = false) MultipartFile[] images,
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.update(addPostDtoRequest, images, user.getId()));
    }

    /**
     * Method for search.
     *
     * @param searchQuery query to search.
     * @return PageableDto of {@link SearchPostDto} instances.
     */

    @GetMapping("/search")
    public ResponseEntity<PageableDto<SearchPostDto>> searchPosts(@RequestParam String searchQuery, Pageable pageable) {
        logger.debug("Received search query: {}", searchQuery);

        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("post-000002")
                .query(q -> q
                        .match(m -> m
                                .field("title")
                                .query(searchQuery)
                        )
                )
                .build();

        SearchResponse<JsonNode> searchResponse;
        try {
            searchResponse = client.search(searchRequest, JsonNode.class);
        } catch (IOException e) {
            logger.error("Error executing search request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        List<JsonNode> hits = searchResponse.hits().hits().stream()
                .map(Hit::source)
                .toList();

        List<SearchPostDto> dtoList = hits.stream()
                .map(sourceAsJsonNode -> new SearchPostDto(
                        Long.parseLong(sourceAsJsonNode.get("id").asText()),
                        sourceAsJsonNode.get("title").asText(),
                        new PostAuthorDto(sourceAsJsonNode.get("userName").asText()),
                        ZonedDateTime.parse(sourceAsJsonNode.get("createdAt").asText())
                ))
                .collect(Collectors.toList());

        assert searchResponse.hits().total() != null;
        return ResponseEntity.ok(new PageableDto<>(
                dtoList,
                searchResponse.hits().total().value(),
                pageable.getPageNumber(),
                (int) Math.ceil((double) searchResponse.hits().total().value() / pageable.getPageSize())
        ));
    }

    @Operation(description = "Get all posts from users that the current user is subscribed to.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of posts from subscribed users with pagination details",
                    content = @Content(schema = @Schema(implementation = PageableAdvancedDto.class))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/subscribed")
    public ResponseEntity<PageableAdvancedDto<PostDto>> getPostsFromSubscribedUsers(
            @Parameter(hidden = true) Pageable page,
            @Parameter(hidden = true) @CurrentUser UserVO user) {

        PageableAdvancedDto<PostDto> posts = postService.findAllPostsBySubscribedUsers(user, page);
        return ResponseEntity.ok(posts);

    }
}

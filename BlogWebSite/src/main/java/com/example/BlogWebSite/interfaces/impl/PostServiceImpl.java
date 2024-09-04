package com.example.BlogWebSite.interfaces.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.BlogWebSite.constant.ErrorMessage;
import com.example.BlogWebSite.exeption.exceptions.LowRoleLevelException;
import com.example.BlogWebSite.exeption.exceptions.NotFoundException;
import com.example.BlogWebSite.exeption.exceptions.NotSavedException;
import com.example.BlogWebSite.exeption.exceptions.UnsupportedSortException;
import com.example.BlogWebSite.interfaces.FileService;
import com.example.BlogWebSite.interfaces.PostService;
import com.example.BlogWebSite.model.Post;
import com.example.BlogWebSite.model.Role;
import com.example.BlogWebSite.model.User;
import com.example.BlogWebSite.model.dto.*;
import com.example.BlogWebSite.repo.PostRepo;
import com.example.BlogWebSite.repo.UserRepo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final ElasticsearchClient client;

    @Override
    public PostDto save(AddPostDtoRequest addPostDtoRequest, MultipartFile[] images, Long authorId) {
        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + authorId));
        Post postToSave = modelMapper.map(addPostDtoRequest, Post.class);
        postToSave.setAuthor(author);
        validateImages(postToSave, images);
        postRepo.save(postToSave);
        return modelMapper.map(postToSave, PostDto.class);
    }


    private void validateImages(Post postToSave, MultipartFile[] images) {
        if (images != null && images.length > 0) {
            if (images.length > 5) {
                throw new IllegalArgumentException(ErrorMessage.USER_CANNOT_ADD_MORE_THAN_5_POST_IMAGES);
            }
            for (MultipartFile image : images) {
                if (image.getSize() > 10 * 1024 * 1024) {
                    throw new IllegalArgumentException(ErrorMessage.IMAGE_SIZE_EXCEEDS_10MB);
                }
                if (!isSupportedContentType(image.getContentType())) {
                    throw new IllegalArgumentException(ErrorMessage.UNSUPPORTED_IMAGE_FORMAT);
                }
            }
            String[] paths = uploadImages(images);
            if (paths.length != images.length) {
                throw new NotSavedException(ErrorMessage.POST_NOT_SAVED);
            }
            postToSave.setTitleImage(paths[0]);
            if (images.length > 1) {
                List<String> additionalImages = Arrays.stream(paths)
                        .skip(1)
                        .collect(Collectors.toList());
                postToSave.setAdditionalImages(additionalImages);
            }
        }
    }

    private boolean isSupportedContentType(String contentType) {
        var supportedContents = List.of("image/jpg", "image/jpeg", "image/png");
        return supportedContents.contains(contentType);
    }

    @Override
    public PostDto findById(Long postId) {
        Post post = postRepo
                .findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.POST_NOT_FOUND_BY_ID + postId));
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PageableAdvancedDto<PostDto> findAll(Pageable page) {
        Page<Post> pages;
        if (page.getSort().isSorted()) {
            pages = postRepo.findAll(page);
        } else {
            if (page.getSort().isUnsorted()) {
                pages = postRepo.findAllByOrderByCreatedAtDesc(page);
            } else {
                throw new UnsupportedSortException(ErrorMessage.INVALID_SORTING_VALUE);
            }
        }
        return buildPageableAdvancedDto(pages);
    }

    private PageableAdvancedDto<PostDto> buildPageableAdvancedDto(Page<Post> postPage) {
        List<PostDto> postDtos = postPage.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        return new PageableAdvancedDto<>(
                postDtos,
                postPage.getTotalElements(),
                postPage.getPageable().getPageNumber(),
                postPage.getTotalPages(),
                postPage.getNumber(),
                postPage.hasPrevious(),
                postPage.hasNext(),
                postPage.isFirst(),
                postPage.isLast());
    }

    @Override
    public PageableAdvancedDto<PostDto> findAllByUser(UserVO user, Pageable page) {
        Page<Post> pages;
        userRepo.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + user.getId()));
        if (page.getSort().isEmpty()) {
            pages = postRepo.findAllByAuthorOrderByCreatedAt(modelMapper.map(user, User.class), page);
        } else {
            throw new UnsupportedSortException(ErrorMessage.INVALID_SORTING_VALUE);
        }
        return buildPageableAdvancedDto(pages);
    }

    @Override
    public PostDto update(AddPostDtoRequest addPostDtoRequest, MultipartFile[] images, Long authorId) {
        Post updatedPost = postRepo.findById(addPostDtoRequest.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.POST_NOT_FOUND_BY_ID + addPostDtoRequest.getId()));
        userRepo.findById(authorId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + authorId));

        if (!userIsAuthorOrAdmin(authorId, addPostDtoRequest.getId())) {
            throw new AccessDeniedException(ErrorMessage.IMPOSSIBLE_UPDATE_POST);
        }
        updatedPost.setTitle(addPostDtoRequest.getTitle());
        updatedPost.setContent(addPostDtoRequest.getContent());
        validateImages(updatedPost, images);
        postRepo.save(updatedPost);
        return modelMapper.map(updatedPost, PostDto.class);
    }

    private boolean userIsAuthorOrAdmin(Long userId, Long postId) {
        if (userId.equals(getAuthorPostId(postId).getId())) {
            return true;
        }
        Optional<User> userOptional = userRepo.findById(userId);
        return userOptional.map(user -> user.getRole().equals(Role.ADMIN)).orElse(false);
    }

    private User getAuthorPostId(Long postId) {
        Optional<Post> postOptional = postRepo.findById(postId);
        return postOptional.map(Post::getAuthor)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.POST_NOT_FOUND_BY_ID + postId));
    }

    @Override
    public void delete(Long postId, UserVO user) {
        PostDto postDto = findById(postId);
        if (postDto == null) {
            throw new NotFoundException(ErrorMessage.POST_NOT_FOUND_BY_ID + postId);
        }

        if (user.getRole() != Role.ADMIN && !Objects.equals(user.getId(), postDto.getAuthor().getId())) {
            throw new LowRoleLevelException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        postRepo.deleteById(postId);
    }


    @Override
    public String[] uploadImages(MultipartFile[] images) {
        return Arrays.stream(images).map(fileService::upload).toArray(String[]::new);
    }

    @Override
    public PageableDto<SearchPostDto> searchPostsByTitle(Pageable pageable, String searchText) {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("post-000002")
                .query(q -> q
                        .match(m -> m
                                .field("title")
                                .query(searchText)
                        )
                )
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize())
                .build();

        SearchResponse<JsonNode> searchResponse;
        try {
            searchResponse = client.search(searchRequest, JsonNode.class);
        } catch (IOException e) {
            throw new RuntimeException("Error executing search request", e);
        }

        List<SearchPostDto> dtoList = searchResponse.hits().hits().stream()
                .map(hit -> {
                    assert hit.source() != null;
                    return new SearchPostDto(
                            Long.parseLong(hit.source().get("id").asText()),
                            hit.source().get("title").asText(),
                            new PostAuthorDto(hit.source().get("userName").asText()),
                            ZonedDateTime.parse(hit.source().get("createdAt").asText())
                    );
                })
                .collect(Collectors.toList());

        long totalHits = searchResponse.hits().total() != null ? searchResponse.hits().total().value() : 0;

        return new PageableDto<>(
                dtoList,
                totalHits,
                pageable.getPageNumber(),
                (int) Math.ceil((double) totalHits / pageable.getPageSize())
        );
    }

    @Override
    public PageableAdvancedDto<PostDto> findAllPostsBySubscribedUsers(UserVO currentUser, Pageable pageable) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Current user cannot be null");
        }

        Set<UserVO> subscribedUsers = currentUser.getSubscriptions();

        if (subscribedUsers == null || subscribedUsers.isEmpty()) {
            Page<Post> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            return buildPageableAdvancedDto(emptyPage);
        }

        try {
            Page<Post> pages = postRepo.findAllByAuthorInOrderByCreatedAtDesc((subscribedUsers
                    .stream().map((element) -> modelMapper.map(element, User.class)).collect(Collectors.toSet())), pageable);
            return buildPageableAdvancedDto(pages);
        } catch (Exception e) {

            throw new RuntimeException("Error retrieving posts from subscribed users", e);
        }
    }

}

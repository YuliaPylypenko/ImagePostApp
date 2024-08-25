package com.example.BlogWebSite.interfaces;

import com.example.BlogWebSite.model.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    /**
     * Method for creating the Post instance.
     *
     * @param addPostDtoRequest dto for creating Post instance.
     * @param images            post images
     * @param authorId          ID of post creator
     * @return {@link PostDto} instance.
     */
    PostDto save(AddPostDtoRequest addPostDtoRequest, MultipartFile[] images, Long authorId);

    /**
     * Method for getting the {@link PostDto} instance by its id.
     *
     * @param postId {@link PostDto} instance id.
     * @return {@link PostDto} instance.
     */
    PostDto findById(Long postId);

    /**
     * Method for getting all posts by page.
     *
     * @return PageableDto of {@link PostDto} instances.
     */
    PageableAdvancedDto<PostDto> findAll(Pageable page);

    /**
     * Method for getting all posts created by user by page.
     *
     * @param user author of posts.
     * @param page parameters of to search.
     * @return PageableDto of {@link PostDto} instances.
     */
    PageableAdvancedDto<PostDto> findAllByUser(UserVO user, Pageable page);

    /**
     * Method for updating Post instance.
     *
     * @param addPostDtoRequest - instance of {@link AddPostDtoRequest}.
     * @param images            post images.
     * @param authorId          ID of post author.
     * @return {@link PostDto} instance.
     */
    PostDto update(AddPostDtoRequest addPostDtoRequest, MultipartFile[] images, Long authorId);

    /**
     * Method for deleting the {@link PostDto} instance by its id.
     *
     * @param id   - {@link PostDto} instance id which will be deleted.
     * @param user - current {@link UserVO} that wants to delete.
     */
    void delete(Long id, UserVO user);

    /**
     * Method to upload post images.
     *
     * @param images - array of post images
     * @return array of images path
     */
    String[] uploadImages(MultipartFile[] images);

    /**
     * Method that allow you to search {@link SearchPostDto}.
     *
     * @param pageable    {@link Pageable}.
     * @param searchText query to search.
     * @return PageableDto of {@link SearchPostDto} instances.
     */
    PageableDto<SearchPostDto> searchPostsByTitle(Pageable pageable, String searchText);

    /**
     * Retrieves all posts from users that the current user is subscribed to.
     *
     * @param currentUser the user whose subscriptions are to be used to filter posts.
     * @param page a pageable object containing pagination information.
     * @return a {@link PageableAdvancedDto} containing the list of {@link PostDto} objects and pagination details.
     */
    PageableAdvancedDto<PostDto> findAllPostsBySubscribedUsers(UserVO currentUser, Pageable page);

}

package com.example.BlogWebSite.repo;

import com.example.BlogWebSite.model.Post;
import com.example.BlogWebSite.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PostRepo extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    /**
     * Method returns all {@link Post} by page.
     *
     * @param page page of posts.
     * @return all {@link Post} by page.
     */
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable page);

    /**
     * Method returns all users {@link Post} by page.
     *
     * @param user author of post.
     * @param page page of post.
     * @return all {@link Post} by page.
     */
    Page<Post> findAllByAuthorOrderByCreatedAt(User user, Pageable page);


    /**
     * Method returns all users {@link Post} by page.
     *
     * @param authors list of authors of posts.
     * @param page page of post.
     * @return all {@link Post} by page.
     */
    Page<Post> findAllByAuthorInOrderByCreatedAtDesc(Set<User> authors, Pageable page);

}

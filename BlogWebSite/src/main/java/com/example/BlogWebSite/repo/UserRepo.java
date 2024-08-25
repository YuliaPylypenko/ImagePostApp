package com.example.BlogWebSite.repo;

import com.example.BlogWebSite.model.Post;
import com.example.BlogWebSite.model.Role;
import com.example.BlogWebSite.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Find {@link User} by email.
     *
     * @param email user email.
     * @return {@link User}
     */
    Optional<User> findByEmail(String email);

    User findByRole(Role role);

    /**
     * Find id by email.
     *
     * @param email - User email
     * @return User id
     */
    @Query("SELECT id FROM User WHERE email=:email")
    Optional<Long> findIdByEmail(String email);

    /**
     * Method that checks if the email user exists.
     *
     * @param email - email of User.
     * @return - return true if User exists and false if not.
     */
    boolean existsUserByEmail(String email);

    /**
     * Find posts by userId.
     *
     * @param userId   - User userId
     * @param pageable - The pageable object used for pagination and sorting.
     * @return a Page object containing the list of posts who meet the filter
     * criteria.
     */
    @Query(value = "SELECT p FROM Post p WHERE p.author.id = :userId")
    Page<Post> findAllUserPosts(@Param("userId") Long userId, Pageable pageable);

}

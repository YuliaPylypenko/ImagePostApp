package com.example.BlogWebSite.interfaces;

import com.example.BlogWebSite.model.dto.PageableDto;
import com.example.BlogWebSite.model.dto.UserVO;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDetailsService userDetailsService();

    /**
     * Find UserVO's id by UserVO email.
     *
     * @param email - {@link UserVO} email
     * @return {@link UserVO} id
     */
    Long findIdByEmail(String email);


    /**
     * Method that allow you to find {link UserVO} by id.
     *
     * @param id a value of {@link Long}
     * @return {link UserVO} with this id.
     */
    UserVO findById(Long id) throws ChangeSetPersister.NotFoundException;

    /**
     * Method that allow you to save new {@link UserVO}.
     *
     * @param user a value of {@link UserVO}
     */
    UserVO save(UserVO user);

    /**
     * Method that allow you to delete {@link UserVO} by ID.
     *
     * @param id a value of {@link Long}
     */
    void deleteById(Long id);

    List<UserVO> findAll();

    /**
     * Method that allow you to find {@link UserVO} by email.
     *
     * @param email a value of {@link String}
     * @return {@link UserVO} with this email.
     */
    UserVO findByEmail(String email);

    UserVO subscribeToUser(Long userId, Long subscriptionId);
    UserVO unsubscribeFromUser(Long userId, Long subscriptionId);

    Set<UserVO> getSubscriptions(UserVO currentUser);

    /**
     * Find {@link UserVO}-s by page .
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@link PageableDto}.
     */
    PageableDto<UserVO> findByPage(Pageable pageable);

    /**
     * Method return user profile information {@link UserVO}.
     *
     * @param userId - {@link UserVO}'s id
     */
    UserVO getUserProfileInformation(Long userId);
}

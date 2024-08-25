package com.example.BlogWebSite.interfaces.impl;

import com.example.BlogWebSite.constant.AppConstant;
import com.example.BlogWebSite.constant.ErrorMessage;
import com.example.BlogWebSite.exeption.exceptions.BadRequestException;
import com.example.BlogWebSite.exeption.exceptions.NotFoundException;
import com.example.BlogWebSite.exeption.exceptions.WrongEmailException;
import com.example.BlogWebSite.exeption.exceptions.WrongIdException;
import com.example.BlogWebSite.interfaces.UserService;
import com.example.BlogWebSite.model.User;
import com.example.BlogWebSite.model.dto.PageableDto;
import com.example.BlogWebSite.model.dto.UserVO;
import com.example.BlogWebSite.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public List<UserVO> findAll() {
        return modelMapper.map(userRepo.findAll(), new TypeToken<List<UserVO>>() {
        }.getType());
    }

    @Override
    public UserVO findByEmail(String email) {
        if (!email.matches(AppConstant.VALIDATION_EMAIL)) {
            throw new BadRequestException(ErrorMessage.INVALID_USER_EMAIL);
        }
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return modelMapper.map(user, UserVO.class);
    }

    @Override
    public Long findIdByEmail(String email) {
        log.info("Find Id By Email" + email);
        return userRepo.findIdByEmail(email).orElseThrow(
                () -> new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
    }

    @Override
    public UserVO findById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
        return modelMapper.map(user, UserVO.class);
    }

    @Override
    public UserVO save(UserVO userVO) {
        User user = modelMapper.map(userVO, User.class);
        try {
            userVO = modelMapper.map(userRepo.save(user), UserVO.class);
        } catch (Exception e) {
            throw new BadRequestException(ErrorMessage.INVALID_USER_VO);
        }
        return userVO;

    }

    @Override
    public void deleteById(Long id) {
        UserVO userVO = findById(id);
        userRepo.delete(modelMapper.map(userVO, User.class));
    }

    @Override
    @PreAuthorize("#userId == authentication.principal.id")
    public UserVO subscribeToUser(Long userId, Long subscriptionId) {
        if (userId.equals(subscriptionId)) {
            throw new IllegalArgumentException("User cannot subscribe to themselves");
        }
        User user = findUserById(userId);
        User subscription = findUserById(subscriptionId);
        if (!user.getSubscriptions().add(subscription)) {
            throw new IllegalStateException("User is already subscribed to this user");
        }
        user.getSubscriptions().add(subscription);
        userRepo.save(user);

        return modelMapper.map(user, UserVO.class);
    }

    @Override
    @PreAuthorize("#userId == authentication.principal.id")
    public UserVO unsubscribeFromUser(Long userId, Long subscriptionId) {
        if (userId.equals(subscriptionId)) {
            throw new IllegalArgumentException("User cannot unsubscribe from themselves");
        }

        User user = findUserById(userId);
        User subscription = findUserById(subscriptionId);
        if (!user.getSubscriptions().remove(subscription)) {
            throw new IllegalStateException("User is not subscribed to this user");
        }

        userRepo.save(user);

        return modelMapper.map(user, UserVO.class);
    }

    @Override
    public Set<UserVO> getSubscriptions(UserVO currentUser) {

        if (currentUser == null) {
            throw new IllegalArgumentException("Current user cannot be null");
        }
        return currentUser.getSubscriptions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserVO> findByPage(Pageable pageable) {
        Page<User>users = userRepo.findAll(pageable);
        List<UserVO>usersVOlist = users.stream()
                .map((user) -> modelMapper.map(user, UserVO.class)).toList();
        return new PageableDto<>(
                usersVOlist,
                users.getTotalElements(),
                users.getPageable().getPageNumber(),
                users.getTotalPages()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO getUserProfileInformation(Long userId) {
        return modelMapper.map(findUserById(userId), UserVO.class);
    }

    private User findUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new WrongIdException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
    }
}
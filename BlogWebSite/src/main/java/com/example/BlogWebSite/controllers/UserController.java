package com.example.BlogWebSite.controllers;

import com.example.BlogWebSite.annotations.ApiPageable;
import com.example.BlogWebSite.annotations.CurrentUser;
import com.example.BlogWebSite.constant.HttpStatuses;
import com.example.BlogWebSite.interfaces.UserService;
import com.example.BlogWebSite.model.User;
import com.example.BlogWebSite.model.dto.PageableDto;
import com.example.BlogWebSite.model.dto.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Validated
@RestController
@RequestMapping("/user/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageable A `Pageable` object that defines pagination parameters such as page number and page size.
     * @return ResponseEntity with {@link PageableDto} containing {@link UserVO} objects.
     */

    @Operation(description = "Get all users by page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users",
                    content = @Content(schema = @Schema(implementation = Set.class))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @GetMapping("all")
    public ResponseEntity<PageableDto<UserVO>> getAllUsers(
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByPage(pageable));
    }

    /**
     * Retrieves all subscriptions of the current user.
     *
     * @param user The current user, injected by the `@CurrentUser` annotation.
     * @return `ResponseEntity` with a `Set` of {@link UserVO} objects representing the user's subscriptions.
     */

    @Operation(description = "Get all subscriptions of the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of subscriptions",
                    content = @Content(schema = @Schema(implementation = Set.class))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/subscriptions")
    public ResponseEntity<Set<UserVO>> getSubscriptions(
            @Parameter(hidden = true) @CurrentUser UserVO user) {
        Set<UserVO> subscriptions = userService.getSubscriptions(user);
        return ResponseEntity.status(HttpStatus.OK).body(subscriptions);
    }

    /**
     * Retrieves the profile information of a user by their ID.
     *
     * @param userId The ID of the current user. Cannot be empty.
     * @return `ResponseEntity` with {@link UserVO} representing the user's profile information.
     */

    @Operation(description = "Get user profile information by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{userId}/profile/")
    public ResponseEntity<UserVO> getUserProfileInformation(
            @Parameter(ref = "Id of current user. Cannot be empty.")
            @PathVariable @CurrentUser Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUserProfileInformation(userId));
    }

    /**
     * Adds a subscription to another user.
     *
     * @param currentUser    The current authenticated user, injected by `@AuthenticationPrincipal`.
     * @param subscriptionId The ID of the user to subscribe to.
     * @return `ResponseEntity` with {@link UserVO} representing the updated user information after subscribing.
     */

    @Operation(description = "Add subscription.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = UserVO.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)})

    @PostMapping("/subscribe/{subscriptionId}")
    public ResponseEntity<UserVO> subscribeToUser(@AuthenticationPrincipal UserDetails currentUser, @PathVariable Long subscriptionId) {
        Long userId = getUserIdFromUserDetails(currentUser);
        UserVO updatedUser = userService.subscribeToUser(userId, subscriptionId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof User) {
            return ((User) userDetails).getId();
        } else {
            throw new IllegalArgumentException("User details does not contain userId");
        }
    }

    /**
     * Removes a subscription from another user.
     *
     * @param currentUser    The current authenticated user, injected by `@AuthenticationPrincipal`.
     * @param subscriptionId The ID of the user to unsubscribe from.
     * @return `ResponseEntity` with {@link UserVO} representing the updated user information after unsubscribing.
     */

    @Operation(description = "Remove subscription.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = HttpStatuses.OK,
            content = @Content(schema = @Schema(implementation = UserVO.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)})

    @PostMapping("/unsubscribe/{subscriptionId}")
    public ResponseEntity<Object> unsubscribeFromUser(@AuthenticationPrincipal UserDetails currentUser, @PathVariable Long subscriptionId) {
        Long userId = getUserIdFromUserDetails(currentUser);
        UserVO updatedUser = userService.unsubscribeFromUser(userId, subscriptionId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }
}

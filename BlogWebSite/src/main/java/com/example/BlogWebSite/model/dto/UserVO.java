package com.example.BlogWebSite.model.dto;

import lombok.*;
import com.example.BlogWebSite.model.Role;
import com.example.BlogWebSite.model.User;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link User}
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserVO implements Serializable {
    private Long id;
    private String userName;
    private String email;
    private Role role;
    private Set<UserVO> subscriptions;
}

package com.example.BlogWebSite.converters;

import com.example.BlogWebSite.annotations.CurrentUser;
import com.example.BlogWebSite.exeption.exceptions.NotFoundException;
import com.example.BlogWebSite.interfaces.UserService;
import com.example.BlogWebSite.model.dto.UserVO;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

@Component
@AllArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    UserService userService;
    ModelMapper modelMapper;

    /**
     * Method checks if parameter is {@link UserVO} and is annotated with
     * {@link CurrentUser}.
     *
     * @param parameter method parameter
     * @return boolean
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null
                && parameter.getParameterType().equals(UserVO.class);
    }

    /**
     * Method returns {@link UserVO} by principal.
     *
     * @return {@link UserVO}
     */
    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Principal principal = webRequest.getUserPrincipal();
        if (principal != null) {
            UserVO user = userService.findByEmail(principal.getName());
            if (user != null) {
                return user;
            } else {
                throw new NotFoundException("User with email " + principal.getName() + " not found");
            }
        } else {
            return null;
        }
    }
}

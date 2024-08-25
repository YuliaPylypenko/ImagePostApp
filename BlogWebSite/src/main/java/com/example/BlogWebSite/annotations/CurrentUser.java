package com.example.BlogWebSite.annotations;

import com.example.BlogWebSite.converters.UserArgumentResolver;
import com.example.BlogWebSite.model.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Annotation is used for injecting {@link User} into
 * controller by {@link UserArgumentResolver}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CurrentUser {
}

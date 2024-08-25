package com.example.BlogWebSite.service;

import com.example.BlogWebSite.model.Post;
import com.example.BlogWebSite.model.Role;
import com.example.BlogWebSite.model.User;
import com.example.BlogWebSite.model.dto.AddPostDtoRequest;
import com.example.BlogWebSite.model.dto.PostAuthorDto;
import com.example.BlogWebSite.model.dto.PostDto;
import com.example.BlogWebSite.model.dto.UserVO;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelUtils {
    public static ZonedDateTime zonedDateTime = ZonedDateTime.now();
    public static String TEST_EMAIL = "test@mail.com";
    public static final String TEST_NAME = "Taras";
    public static final String IMG_NAME = "Screenshot.png";

    public static User getUser() {
        return User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .userName(TEST_NAME)
                .role(Role.USER)
                .build();
    }

    public static UserVO getUserVO() {
        UserVO user = new UserVO();
        user.setId(1L);
        user.setUserName(TEST_NAME);
        user.setEmail(TEST_EMAIL);
        user.setRole(Role.USER);
        return user;
    }

    public static Principal getPrincipal() {
        return () -> TEST_EMAIL;
    }


    public static Post getPost() {
        return new Post(1L, "post title", "post description",
                zonedDateTime, "https://google.com/", Collections.singletonList("https://google.com/additional"),
                getUser(), new ArrayList<>());
    }

    public static Post getInvalidPost() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        return new Post(
                1L,
                "",
                "post description description",
                zonedDateTime,
                "https://google.com/",
                Collections.singletonList("https://google.com/additional"),
                getUser(),
                new ArrayList<>()
        );
    }


    public static PostDto getPostDto() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        PostAuthorDto author = getPostAuthorDto();
        List<String> additionalImages = Collections.singletonList("https://example.com/additional");

        return PostDto.builder()
                .id(1L)
                .title("post title")
                .content("post description event description")
                .createdAt(getPost().getCreatedAt())
                .author(author)
                .titleImage("https://example.com/")
                .additionalImages(additionalImages)
                .build();
    }

    public static PostAuthorDto getPostAuthorDto() {
        return PostAuthorDto.builder()
                .id(1L)
                .userName("example_user")
                .build();
    }


    public static MultipartFile getFile() {
        Path path = Paths.get("src/test/resources/post.png");
        String name = IMG_NAME;
        String contentType = "image/jpeg";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new MockMultipartFile(name,
                name, contentType, content);
    }

    public static AddPostDtoRequest getAddPostDtoRequest() {
        return new AddPostDtoRequest(1L, "event title", "post description event description");
    }

    public static URL getUrl() throws MalformedURLException {
        return URI.create("https://www.example.com").toURL();
    }

}

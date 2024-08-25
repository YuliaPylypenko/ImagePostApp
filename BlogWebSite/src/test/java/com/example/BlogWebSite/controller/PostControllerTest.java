package com.example.BlogWebSite.controller;

import com.example.BlogWebSite.controllers.PostController;
import com.example.BlogWebSite.converters.UserArgumentResolver;
import com.example.BlogWebSite.exeption.handler.CustomExceptionHandler;
import com.example.BlogWebSite.interfaces.PostService;
import com.example.BlogWebSite.interfaces.UserService;
import com.example.BlogWebSite.model.dto.AddPostDtoRequest;
import com.example.BlogWebSite.model.dto.PostDto;
import com.example.BlogWebSite.model.dto.UserVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import static com.example.BlogWebSite.service.ModelUtils.getPrincipal;
import static com.example.BlogWebSite.service.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PostControllerTest {

    private static final String postsLink = "/posts";
    private MockMvc mockMvc;
    @InjectMocks
    private PostController postController;
    @Mock
    private PostService postService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ObjectMapper objectMapper;
    private final Principal principal = getPrincipal();
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(postController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes))
                .build();
    }

    @Test
    void createPostTest() throws Exception {
        UserVO userVO = mock(UserVO.class);
        when(userVO.getId()).thenReturn(1L);

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        String json = """
            {"title": "Create Post",
            "content": "I try to create new Post, and it's completely goes good! Create Post!"}
            """;
        MockMultipartFile jsonFile = new MockMultipartFile(
                "addPostDtoRequest", "addPostDtoRequest", "application/json", json.getBytes());

        MockMultipartFile imageFile = new MockMultipartFile("images", "img.jpg", "image/jpeg", "image data".getBytes());

        PostDto mockPostDto = new PostDto();
        mockPostDto.setTitle("Create Post");
        mockPostDto.setContent("I try to create new Post, and it's completely goes good! Create Post!");
        when(postService.save(any(AddPostDtoRequest.class), any(MultipartFile[].class), anyLong()))
                .thenReturn(mockPostDto);

        mockMvc.perform(multipart("/posts/create")
                        .file(jsonFile)
                        .file(imageFile)
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().xml(
                        "<PostDto>" +
                                "<id/>" +
                                "<title>Create Post</title>" +
                                "<content>I try to create new Post, and it's completely goes good! Create Post!</content>" +
                                "<createdAt/>" +
                                "<author/>" +
                                "<titleImage/>" +
                                "</PostDto>"
                ));
    }

    @Test
    void createBadRequestTest() throws Exception {
        mockMvc.perform(multipart(postsLink + "/create")
                        .content("{}")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllPosts() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(postsLink + "?page=1"))
                .andExpect(status().isOk());

        verify(postService).findAll(pageable);
    }

    @Test
    void testGetPostsCreatedByUser() throws Exception {
        int pageNumber = 5;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(postsLink + "/myPosts?page=5&size=2"))
                .andExpect(status().isOk());
        verify(postService, times(1)).findAllByUser(null, pageable);
    }

    @Test
    void  deletePostTest() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(getUserVO());
        mockMvc.perform(delete("/posts/delete/{postId}", 1L)
                .principal(principal))
                .andExpect(status().isOk());

        verify(postService).delete(1L, getUserVO());
    }
}

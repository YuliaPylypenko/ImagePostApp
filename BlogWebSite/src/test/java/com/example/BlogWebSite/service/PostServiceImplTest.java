package com.example.BlogWebSite.service;

import com.example.BlogWebSite.exeption.exceptions.NotFoundException;
import com.example.BlogWebSite.interfaces.FileService;
import com.example.BlogWebSite.interfaces.impl.PostServiceImpl;
import com.example.BlogWebSite.model.Post;
import com.example.BlogWebSite.model.dto.AddPostDtoRequest;
import com.example.BlogWebSite.model.dto.PageableAdvancedDto;
import com.example.BlogWebSite.model.dto.PostDto;
import com.example.BlogWebSite.repo.PostRepo;
import com.example.BlogWebSite.repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.BlogWebSite.service.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PostServiceImplTest {

    @Mock
    FileService fileService;
    @Mock
    private PostRepo postRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    ModelMapper modelMapper;
    @Mock
    HttpServletRequest httpServletRequest;
    @InjectMocks
    private PostServiceImpl postService;
    private final Post post = getPost();

    @Test
    void save() throws MalformedURLException {
        MultipartFile[] images = new MultipartFile[]{getFile()};
        AddPostDtoRequest addPostDtoRequest = getAddPostDtoRequest();

        when(modelMapper.map(addPostDtoRequest, Post.class)).thenReturn(post);
        when(modelMapper.map(post, PostDto.class)).thenReturn(getPostDto());
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(getUser()));
        when(postRepo.save(post)).thenReturn(post);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(fileService.upload(any())).thenReturn(ModelUtils.getUrl().toString());

        PostDto res = postService.save(addPostDtoRequest, images, 1L);

        assertEquals(res, getPostDto());
    }

    @Test()
    void saveThrowsNotFoundException() {
        MultipartFile[] images = new MultipartFile[]{getFile()};
        when(userRepo.findById(4L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.save(getAddPostDtoRequest(), images, 4L));
    }


    @Test
    void delete() {
        when(postRepo.findById(anyLong())).thenReturn(Optional.of(post));
        when(postService.findById(anyLong())).thenReturn(getPostDto());
        postService.delete(post.getId(), getUserVO());

        verify(postRepo, times(1)).deleteById(anyLong());
    }

    @Test
    void findById() {
        PostDto expected = modelMapper.map(post, PostDto.class);
        when(postRepo.findById(anyLong())).thenReturn(Optional.of(post));

        PostDto actual = postService.findById(post.getId());

        verify(postRepo, times(1)).findById(anyLong());
        assertEquals(expected, actual);
    }

    @Test
    void findAllEvents() {

        List<Post> posts = Collections.singletonList(ModelUtils.getPost());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<Post> translationPage = new PageImpl<>(posts,
                pageRequest, posts.size());

        List<PostDto> dtoList = Collections.singletonList(
                getPostDto());

        PageableAdvancedDto<PostDto> pageableDto = new PageableAdvancedDto<>(dtoList, dtoList.size(), 0, 1,
                0, false, false, true, true);

        when(postRepo.findAllByOrderByCreatedAtDesc(pageRequest)).thenReturn(translationPage);
        when(modelMapper.map(posts.get(0), PostDto.class)).thenReturn(dtoList.get(0));

        PageableAdvancedDto<PostDto> actual = postService.findAll(pageRequest);

        assertEquals(pageableDto, actual);
    }
}

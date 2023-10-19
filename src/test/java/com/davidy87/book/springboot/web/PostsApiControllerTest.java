package com.davidy87.book.springboot.web;

import com.davidy87.book.springboot.domain.posts.Posts;
import com.davidy87.book.springboot.domain.posts.PostsRepository;
import com.davidy87.book.springboot.web.dto.PostsSaveRequestDto;
import com.davidy87.book.springboot.web.dto.PostsUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostsApiControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    PostsRepository postsRepository;

    @Autowired
    WebApplicationContext context;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        postsRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void savePosts() throws Exception {
        // given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("test@gmail.com")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        // when
        mvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDto))
                )
                .andExpect(status().isOk());

        // then
        List<Posts> postsList = postsRepository.findAll();
        assertThat(postsList.get(0).getTitle()).isEqualTo(title);
        assertThat(postsList.get(0).getContent()).isEqualTo(content);
    }

    @Test
    @WithMockUser(roles = "USER")
    void updatePosts() throws Exception {
        // given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savedPosts.getId();
        String expectedTitle = savedPosts.getTitle();
        String expectedContent = savedPosts.getContent();

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        // when
        mvc.perform(put(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(requestDto))
                )
                .andExpect(status().isOk());

        // then
        List<Posts> postsList = postsRepository.findAll();
        assertThat(postsList.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(postsList.get(0).getContent()).isEqualTo(expectedContent);
    }
}
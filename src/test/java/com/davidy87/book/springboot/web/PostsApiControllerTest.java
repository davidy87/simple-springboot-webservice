package com.davidy87.book.springboot.web;

import com.davidy87.book.springboot.domain.posts.Posts;
import com.davidy87.book.springboot.domain.posts.PostsRepository;
import com.davidy87.book.springboot.web.dto.PostsSaveRequestDto;
import com.davidy87.book.springboot.web.dto.PostsUpdateRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostsApiControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    PostsRepository postsRepository;

    @AfterEach
    void tearDown() {
        postsRepository.deleteAll();
    }

    @Test
    void savePosts() {
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
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> postsList = postsRepository.findAll();
        assertThat(postsList.get(0).getTitle()).isEqualTo(title);
        assertThat(postsList.get(0).getContent()).isEqualTo(content);
    }

    @Test
    void updatePosts() {
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
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> postsList = postsRepository.findAll();
        assertThat(postsList.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(postsList.get(0).getContent()).isEqualTo(expectedContent);
    }
}
package com.davidy87.book.springboot.web;

import com.davidy87.book.springboot.config.auth.dto.SessionUser;
import com.davidy87.book.springboot.web.dto.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(@SessionAttribute(required = false) SessionUser user, Model model) {
        model.addAttribute("posts", postsService.findAllDesc());

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }

        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        model.addAttribute("post", postsService.findById(id));
        return "posts-update";
    }
}

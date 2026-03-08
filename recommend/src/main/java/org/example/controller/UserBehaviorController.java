package org.example.controller;

import org.checkerframework.checker.units.qual.A;
import org.example.domain.UserBehavior;
import org.example.mapper.UserBehaviorMapper;
import org.example.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/userRate")
public class UserBehaviorController {
    @Autowired
    private UserBehaviorService userBehaviorService;
    // 用 AtomicInteger 保证线程安全的计数
//    private AtomicInteger likeCount = new AtomicInteger(0);

//    @PostMapping("/like")
//    public String like() {
//        userBehaviorService.likeMovie(null);
//        return "success";
//    }


    @PostMapping("/like")
    public String like(@RequestParam Long userId, @RequestParam Long movieId,@RequestParam Long rate) {
        UserBehavior userBehavior = new UserBehavior();
        userBehavior.setUserId(userId);
        userBehavior.setMovieId(movieId);
        userBehavior.setRate(rate);
//        userBehavior.setCreateTime(LocalDateTime.now());
        userBehaviorService.likeMovie(userBehavior);
        return "success";
    }
}

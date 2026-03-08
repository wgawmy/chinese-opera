package org.example.service.Impl;

import org.example.domain.UserBehavior;
import org.example.mapper.UserBehaviorMapper;
import org.example.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBehaviorServiceImpl implements UserBehaviorService {
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    @Override
    public void likeMovie(UserBehavior userBehavior) {
        userBehaviorMapper.insert(userBehavior);
    }
}

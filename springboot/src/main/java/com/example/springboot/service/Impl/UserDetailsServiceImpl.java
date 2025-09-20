package com.example.springboot.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.domain.LoginUser;
import com.example.springboot.domain.Role;
import com.example.springboot.domain.User;
import com.example.springboot.mapper.MenuMapper;
import com.example.springboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 根据用户名查询用户信息
        LambdaQueryWrapper wrapper = new LambdaQueryWrapper<User>().eq(User::getUserName, username);
        User user = userMapper.selectOne(wrapper);

        //如果没有该用户就抛出异常
        if (Objects.isNull(user)) {
            throw new RuntimeException("用户名或密码错误");
        }

        //TODO: 查询权限信息封装到LoginUser中

        List<String> permissions = menuMapper.selectPermsByUserId(user.getId());

        List<Role> roles = userMapper.selectRolesByUserId(user.getId());
        List<String> rolePerms = new ArrayList<>();
        if (roles != null && !roles.isEmpty()) {
            rolePerms = roles.stream()
                    .map(role -> "ROLE_" + role.getRoleName()) // 关键：添加 ROLE_ 前缀
                    .collect(Collectors.toList());
        }
        permissions.addAll(rolePerms);
        // 将用户信息封装到UserDetails实现类中
        return new LoginUser(user,permissions);
    }
}

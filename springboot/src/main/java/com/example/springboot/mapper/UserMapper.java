package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.domain.Role;
import com.example.springboot.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from sys_user where id = #{userId}")
    User selectById(Long userId);

    List<User> getAllUsersWithRoles();

    List<Role> selectRolesByUserId(Long userId);
}

package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.domain.UserBehavior;

@Mapper
public interface UserBehaviorMapper {
    @Insert("INSERT INTO user_behavior(user_id,opera_id,rate) VALUES(#{userId},#{movieId},#{rate})")
    int insert(UserBehavior userBehavior);
}
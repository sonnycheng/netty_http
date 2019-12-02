package com.bank.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.bank.pojo.User;

@Mapper
@Component
public interface UserMapper {

    User fetchById(Object id);

    List<User> fetchList(Map<String, Object> query);

    int save(User user);

    int deleteById(Object id);

    int update(User user);

}
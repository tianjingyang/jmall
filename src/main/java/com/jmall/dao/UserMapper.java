package com.jmall.dao;

import com.jmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int selectByUsername(String username);

    int selectByEmail(String email);

    User selectByUsernameAndPassword(@Param("username") String username, @Param("password")String password);

    String selectQuestion(String username);

    int checkAnswer(@Param("username") String username, @Param("question")String question, @Param("answer") String answer);

    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);
}
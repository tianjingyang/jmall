package com.jmall.service.impl;

import com.jmall.common.ServerResponse;
import com.jmall.dao.UserMapper;
import com.jmall.pojo.User;
import com.jmall.service.IUserService;
import com.jmall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;



    @Override
    public ServerResponse<User> login(String username, String password) {
        int rowCount = userMapper.selectByUsername(username);
        if (rowCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectByUsernameAndPassword(username,password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户名或者密码错误");
        }
        return ServerResponse.createBySuccess(user);
    }
}

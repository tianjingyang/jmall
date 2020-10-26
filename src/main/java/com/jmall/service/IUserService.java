package com.jmall.service;

import com.jmall.common.ServerResponse;
import com.jmall.pojo.User;
import org.springframework.stereotype.Service;


public interface IUserService {

    ServerResponse<User> login(String username, String password);

}

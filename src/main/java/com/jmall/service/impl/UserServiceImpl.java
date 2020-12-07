package com.jmall.service.impl;

import com.jmall.common.Const;
import com.jmall.common.ServerResponse;
import com.jmall.dao.UserMapper;
import com.jmall.pojo.User;
import com.jmall.service.IUserService;
import com.jmall.util.MD5Util;
import com.jmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectByUsernameAndPassword(username,MD5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user) {
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int rowCount = userMapper.insert(user);
        if(rowCount > 0) {
            return ServerResponse.createBySuccessMessage("注册成功");
        }
        return ServerResponse.createByErrorMessage("注册失败");
    }

    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int rowCount = userMapper.selectByUsername(str);
                if (rowCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int rowCount = userMapper.selectByEmail(str);
                if (rowCount > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数类型错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestion(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("该用户未设置找回密码问题");
    }

    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        int rowCount = userMapper.checkAnswer(username,question,answer);
        if (rowCount == 0) {
            return ServerResponse.createByErrorMessage("问题答案错误");
        }
        String forgetToken = UUID.randomUUID().toString();
        RedisPoolUtil.setEx(Const.TOKEN_PREFIX+username,forgetToken,60*60*12);
        return ServerResponse.createBySuccess(forgetToken);
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("token需传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = RedisPoolUtil.get(Const.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token已经失效或过期");
        }
        if (StringUtils.equals(token,forgetToken)) {
            String MD5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,MD5Password);
            if (rowCount >0 ) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
            return ServerResponse.createByErrorMessage("修改密码操作失效");
        } else {
            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
        }
    }

    public ServerResponse<String> resetPassword(String username, String passwordOld, String passwordNew) {
        User user = userMapper.selectByUsernameAndPassword(username,MD5Util.MD5EncodeUtf8(passwordOld));
        if (user == null) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        //新密码
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int rowCount = userMapper.updateByPrimaryKeySelective(user);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<User> updateInformation(User user) {
        //username不能被更新
        //更新的email要检查除了自己以外的用户有没有在使用
        int rowCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (rowCount > 0) {
            return ServerResponse.createByErrorMessage("email被他人占用");
        }
        rowCount = userMapper.updateByPrimaryKeySelective(user);
        if (rowCount > 0) {
            User currentUser = userMapper.selectByPrimaryKey(user.getId());
            return ServerResponse.createBySuccess("更新信息成功",currentUser);
        }
        return ServerResponse.createByErrorMessage("更新信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    //backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }















}

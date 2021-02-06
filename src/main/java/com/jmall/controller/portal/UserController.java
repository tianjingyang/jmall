package com.jmall.controller.portal;

import com.jmall.common.Const;
import com.jmall.common.RedisCluster;
import com.jmall.common.ResponseCode;
import com.jmall.common.ServerResponse;
import com.jmall.pojo.User;
import com.jmall.service.IUserService;
import com.jmall.util.CookieUtil;
import com.jmall.util.JsonUtil;
import com.jmall.util.RedisClusterUtil;
import com.jmall.util.RedisClusterUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *前台controller
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param session session
     * @param httpServletResponse response
     * @return user
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session,
                                      HttpServletResponse httpServletResponse) {
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()) {
            //把此次请求的JSESSIONID存入cookie的value
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            //缓存进redis
            RedisClusterUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 登出
     * @param httpServletRequest request
     * @param httpServletResponse response
     * @return String
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisClusterUtil.del(loginToken);

        return ServerResponse.createBySuccess();
    }

    /**
     * 注册
     * @param user 用户名
     * @param session session
     * @return String
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user, HttpSession session) {
        return iUserService.register(user);
    }

    /**
     * 校验用户名或者email是否存在
     * @param str 输入字段
     * @param type 字段类型（username、email）
     * @return  String
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str,type);
    }

    /**
     *
     * @param httpServletRequest request
     * @return User
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest) {
        return getCurrentUser(httpServletRequest);
    }

    /**
     * 获取密码提示问题
     * @param username 用户名
     * @return String
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 验证问题答案
     * @param username 用户名
     * @param question 密码提示问题
     * @param answer 密码提示答案
     * @return String
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.forgetCheckAnswer(username,question,answer);
    }

    /**
     * 忘记密码时重设密码
     * @param username 用户名
     * @param passwordNew 新密码
     * @param forgetToken 忘记密码token
     * @return String
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken) {
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    /**
     * 登录状态重置密码
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @param httpServletRequest request
     * @return String
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, HttpServletRequest httpServletRequest) {
        ServerResponse response = getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        String username = user.getUsername();
        return iUserService.resetPassword(username, passwordOld, passwordNew);
    }

    /**
     *
     * @param httpServletRequest request
     * @param user user
     * @return user
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpServletRequest httpServletRequest, User user) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonStr = RedisClusterUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            //缓存进redis
            RedisClusterUtil.setEx(loginToken, JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 获取个人信息
     * @param httpServletRequest request
     * @return user
     */
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpServletRequest httpServletRequest){
        ServerResponse response = getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iUserService.getInformation(user.getId());
    }

    public ServerResponse<User> getCurrentUser(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
        }
        String userJsonStr = RedisClusterUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createBySuccess(user);
    }
}

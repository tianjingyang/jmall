package com.jmall.controller.backend;

import com.jmall.common.Const;
import com.jmall.common.ServerResponse;
import com.jmall.pojo.User;
import com.jmall.service.IUserService;
import com.jmall.util.CookieUtil;
import com.jmall.util.JsonUtil;
import com.jmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse) {
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                CookieUtil.writeLoginToken(httpServletResponse,session.getId());
                //缓存进redis
                RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
                return response;
            } else {
                return ServerResponse.createByErrorMessage("需要管理员权限");
            }
        }
        return response;
    }



}

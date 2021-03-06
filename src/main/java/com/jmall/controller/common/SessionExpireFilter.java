package com.jmall.controller.common;

import com.jmall.common.Const;
import com.jmall.pojo.User;
import com.jmall.util.CookieUtil;
import com.jmall.util.JsonUtil;
import com.jmall.util.RedisClusterUtil;
import com.jmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //强转servletRequest
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = RedisClusterUtil.get(loginToken);
            User user = JsonUtil.string2Obj(userJsonStr,User.class);
            if (user != null) {
                RedisClusterUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}

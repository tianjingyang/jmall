package com.jmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.jmall.common.Const;
import com.jmall.common.ResponseCode;
import com.jmall.common.ServerResponse;
import com.jmall.pojo.Shipping;
import com.jmall.pojo.User;
import com.jmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;
    @Autowired
    private UserController userController;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest httpServletRequest, Shipping shipping){
//        User user = userController.getCurrentUser(httpServletRequest).getData();
//        if(user ==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iShippingService.add(user.getId(),shipping);
    }


    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpServletRequest httpServletRequest,Integer shippingId){
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iShippingService.del(user.getId(),shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest httpServletRequest,Shipping shipping){
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iShippingService.update(user.getId(),shipping);
    }


    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpServletRequest httpServletRequest,Integer shippingId){
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iShippingService.select(user.getId(),shippingId);
    }


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                         HttpServletRequest httpServletRequest){
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iShippingService.list(user.getId(),pageNum,pageSize);
    }


}

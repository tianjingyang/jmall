package com.jmall.controller.portal;

import com.jmall.common.Const;
import com.jmall.common.ResponseCode;
import com.jmall.common.ServerResponse;
import com.jmall.pojo.User;
import com.jmall.service.ICartService;
import com.jmall.util.CookieUtil;
import com.jmall.util.JsonUtil;
import com.jmall.util.RedisShardedPoolUtil;
import com.jmall.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService iCartService;
    @Autowired
    private UserController userController;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpServletRequest httpServletRequest) {
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iCartService.list(user.getId());
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpServletRequest httpServletRequest,Integer productId,Integer count) {
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpServletRequest httpServletRequest,Integer productId,Integer count) {
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iCartService.update(user.getId(),productId,count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpServletRequest httpServletRequest,String productIds) {
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpServletRequest httpServletRequest) {
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iCartService.selectOrUnSelectProduct(user.getId(),null,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpServletRequest httpServletRequest) {
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iCartService.selectOrUnSelectProduct(user.getId(),null,Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpServletRequest httpServletRequest,Integer productId) {
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iCartService.selectOrUnSelectProduct(user.getId(),productId,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpServletRequest httpServletRequest,Integer productId) {
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iCartService.selectOrUnSelectProduct(user.getId(),productId,Const.Cart.UN_CHECKED);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest httpServletRequest) {
        ServerResponse response = userController.getCurrentUser(httpServletRequest);
        if (!response.isSuccess()) {
            return response;
        }
        User user = (User)response.getData();
        return iCartService.getCartProductCount(user.getId());
    }
}

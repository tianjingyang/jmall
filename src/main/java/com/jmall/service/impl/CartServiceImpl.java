package com.jmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jmall.common.Const;
import com.jmall.common.ResponseCode;
import com.jmall.common.ServerResponse;
import com.jmall.dao.CartMapper;
import com.jmall.dao.ProductMapper;
import com.jmall.pojo.Cart;
import com.jmall.pojo.Product;
import com.jmall.service.ICartService;
import com.jmall.util.BigDecimalUtil;
import com.jmall.util.PropertiesUtil;
import com.jmall.vo.CartProductVo;
import com.jmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUerIdProductId(userId,productId);
        if (cart == null) {
            //说明购物车里没有该商品，需要新增
            Cart cartItem = new Cart();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartMapper.insert(cartItem);
        } else {
            //说明购物车里已经有了该商品，直接count相加
            count = count + cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUerIdProductId(userId,productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds) {
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteProductByUserIdProductIds(userId,productIdList);
        return this.list(userId);
    }

    public ServerResponse<CartVo> selectProduct(Integer userId,Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUerIdProductId(userId,productId);
        if (cart != null) {
            cart.setChecked(Const.Cart.CHECKED);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }


    /*
    全选，全反选，单选，取消单选
     */
    public ServerResponse<CartVo> selectOrUnSelectProduct(Integer userId,Integer productId,Integer checked) {
        cartMapper.selectOrUnSelectProduct(userId,productId,checked);
        return this.list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId) {
       return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }


    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        //首先获取用户ID下的所有Cart的集合
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        //通过Cart集合拼装CartProductVo
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)) {
            //遍历Cart集合
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算商品总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
                    //是否勾选
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                //计算购物车总价
                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                //把拼装好的cartProductVo添加到集合里
                cartProductVoList.add(cartProductVo);
          }
        }
        //开始拼装CartVo
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        cartVo.setAllChecked(this.isAllChecked(userId));
        return cartVo;
    }

    private boolean isAllChecked(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectProductCheckedStatusByUserId(userId) == 0;
    }


}

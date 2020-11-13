package com.jmall.dao;

import com.jmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<Cart> selectByUserId(Integer userId);

    Cart selectByUerIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    int selectProductCheckedStatusByUserId(Integer userId);

    int deleteProductByUserIdProductIds(@Param("userId") Integer userId, @Param("productIds") List<String> productIds);

    int selectOrUnSelectProduct(@Param("userId") Integer userId, @Param("productId") Integer productId,@Param("checked") Integer checked);

    int selectCartProductCount(Integer userId);

}
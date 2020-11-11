package com.jmall.service;

import com.github.pagehelper.PageInfo;
import com.jmall.common.ServerResponse;
import com.jmall.pojo.Product;
import com.jmall.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse<String> saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum, int pageSize,String orderBy);
}

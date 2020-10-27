package com.jmall.service;

import com.jmall.common.ServerResponse;
import com.jmall.pojo.Category;
import java.util.List;

public interface ICategoryService {

    ServerResponse getParallelChildrenByPrentId(Integer categoryId);

    ServerResponse addCategory(String categoryName,Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}

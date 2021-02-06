package com.jmall.service;

import com.jmall.common.ServerResponse;
import com.jmall.pojo.Category;
import java.util.List;

public interface ICategoryService {

    ServerResponse<List<Category>> getParallelChildrenByPrentId(Integer categoryId);

    ServerResponse<String> addCategory(String categoryName,Integer parentId);

    ServerResponse<String> updateCategoryName(Integer categoryId,String categoryName);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}

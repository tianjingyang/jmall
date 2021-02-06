package com.jmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jmall.common.ServerResponse;
import com.jmall.dao.CategoryMapper;
import com.jmall.pojo.Category;
import com.jmall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse<List<Category>> getParallelChildrenByPrentId(Integer categoryId) {
        List<Category> categoryList = categoryMapper.getParallelChildrenByPrentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) {
            log.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    public ServerResponse<String> addCategory(String categoryName,Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个分类是可用的

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    public ServerResponse<String> updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    /*
      递归算法
         1,给出的categoryId的一个category
         2,给出的categoryId的子分类
         3,递归调用本方法获得子分类的下级分类
     */
    private void findChildCategory(Set<Category> categorySet, Integer categoryId) {
        //1，查出给的categoryId对应的category
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        //判断非空后加入set里
        if (category != null) {
            categorySet.add(category);
        }
        //2，查出给的categoryId的子分类，用list接收
        List<Category> categoryList = categoryMapper.getParallelChildrenByPrentId(categoryId);
        //3，遍历子分类，同时递归查询出所有下级分类
        for (Category categoryItem : categoryList) {
            findChildCategory(categorySet,categoryItem.getId());
        }
    }
}

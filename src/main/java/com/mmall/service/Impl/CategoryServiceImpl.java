package com.mmall.service.Impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by mahong on 2022/8/18.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;
    public ServerResponse<String> addCategory(String categoryName, Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int resultCount = categoryMapper.insert(category);
        if(resultCount > 0) {
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    public ServerResponse<String> setCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        category.setStatus(true);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(resultCount > 0) {
            return ServerResponse.createBySuccess("更新品类成功");
        }
        return ServerResponse.createByErrorMessage("更新品类失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        System.out.println("getChildrenParallelCategory categoryId:"+categoryId);
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        System.out.println("getChildrenParallelCategory categoryList:"+categoryList);
        if(CollectionUtils.isEmpty((Collection) categoryList)){
            logger.info("未找到当前分类的子分类");
            return ServerResponse.createByErrorMessage("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

//    /**
//     * 递归查询本节点的id及孩子节点的id
//     * @param categoryId
//     * @return
//     */
//    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
//        Set<Category> categorySet = Sets.newHashSet();
//        findChildCategory(categorySet,categoryId);
//
//
//        List<Integer> categoryIdList = Lists.newArrayList();
//        if(categoryId != null){
//            for(Category categoryItem : categorySet){
//                categoryIdList.add(categoryItem.getId());
//            }
//        }
//        return ServerResponse.createBySuccess(categoryIdList);
//    }
//
//
//    //递归算法,算出子节点
//    private Set<Category> findChildCategory(Set<Category> categorySet ,Integer categoryId){
//        Category category = categoryMapper.selectByPrimaryKey(categoryId);
//        if(category != null){
//            categorySet.add(category);
//        }
//        //查找子节点,递归算法一定要有一个退出的条件
//        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
//        for(Category categoryItem : categoryList){
//            findChildCategory(categorySet,categoryItem.getId());
//        }
//        return categorySet;
//    }

}

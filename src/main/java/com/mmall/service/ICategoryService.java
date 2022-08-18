package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import java.util.List;

/**
 * Created by mahong on 2022/8/18.
 */
public interface ICategoryService {
    ServerResponse<String> addCategory(String categoryName, Integer parentId);
    ServerResponse<String> setCategoryName(Integer categoryId,String categoryName);
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(Integer categoryId);
}

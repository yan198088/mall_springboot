package com.yxy.service;

import com.github.pagehelper.PageInfo;
import com.yxy.model.pojo.Category;
import com.yxy.model.request.AddCategoryReq;
import com.yxy.model.vo.CategoryVO;

import java.util.List;


public interface CategoryService {

    void add(AddCategoryReq addCategoryReq);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> listCategoryForCustomer(Integer id);
}

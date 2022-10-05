package com.yxy.service;


import com.github.pagehelper.PageInfo;
import com.yxy.model.pojo.Product;
import com.yxy.model.request.AddProductReq;
import com.yxy.model.request.ProductListReq;

public interface ProductService {

    void add(AddProductReq addProductReq);


    void update(Product updateProduct);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);
}

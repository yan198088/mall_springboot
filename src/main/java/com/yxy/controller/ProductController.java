package com.yxy.controller;

import com.github.pagehelper.PageInfo;
import com.yxy.common.ApiRestResponse;
import com.yxy.model.pojo.Product;
import com.yxy.model.request.ProductListReq;
import com.yxy.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @ApiOperation("商品详情")
    @GetMapping("product/detail")
    public ApiRestResponse detail(@RequestParam Integer id) {
        Product product = productService.detail(id);
        return ApiRestResponse.success(product);
    }

    @ApiOperation("商品列表展示")
    @GetMapping("product/list")
    public ApiRestResponse list( ProductListReq productListReq) {
        PageInfo pageInfo = productService.list(productListReq);
        return ApiRestResponse.success(pageInfo);
    }


}

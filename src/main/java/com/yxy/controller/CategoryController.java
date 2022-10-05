package com.yxy.controller;

import com.github.pagehelper.PageInfo;
import com.yxy.common.ApiRestResponse;
import com.yxy.common.Constant;
import com.yxy.exception.ImoocMallExceptionEnum;
import com.yxy.model.pojo.Category;
import com.yxy.model.pojo.User;
import com.yxy.model.request.AddCategoryReq;
import com.yxy.model.request.UpdateCategoryReq;
import com.yxy.model.vo.CategoryVO;
import com.yxy.service.CategoryService;
import com.yxy.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 描述：目录Controller
 */
@Controller
public class CategoryController {

    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;

    @ApiOperation("管理员后台添加目录")
    @ResponseBody
    @PostMapping("admin/category/add")
    public ApiRestResponse addCategory(HttpSession session,
                                       @Valid @RequestBody AddCategoryReq addCategoryReq) {
        //不用判断session中是否有值，也不用判断是否是管理员，因为filter已经拦截验证了
        //是管理员就可以执行下面操作
        categoryService.add(addCategoryReq);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台更新目录")
    @PostMapping("admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq) {
        //不用判断session中是否有值，也不用判断是否是管理员，因为filter已经拦截验证了
        //首先用工具直接复制到category对象中
        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryReq, category);
        categoryService.update(category);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除目录")
    @PostMapping("admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleCategory(@RequestParam Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台目录列表（给管理员看）")
    @GetMapping("admin/category/list")
    @ResponseBody
    //pageNum代表第几页，pageSize代表一页多少条数据
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        //pageinfo里面的数据可以看到建议一共几页，当前页还有没有下一页，一共多少条数据
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台目录列表（给用户看）")
    @GetMapping("category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer() {
        List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVOList);
    }


    /* @ApiOperation("后台更新目录")
    @PostMapping("admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(HttpSession session,
                                          @Valid @RequestBody UpdateCategoryReq updateCategoryReq) {
        //先获取session中的对象值
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            //如果为空返回未登录
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        //校验是否是管理员
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            //是管理员就可以执行下面操作
            //首先用工具直接复制到category对象中
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryReq, category);
            categoryService.update(category);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }*/
}

package com.yxy.controller;

import com.github.pagehelper.PageInfo;
import com.yxy.common.ApiRestResponse;
import com.yxy.common.Constant;
import com.yxy.exception.ImoocMallException;
import com.yxy.exception.ImoocMallExceptionEnum;
import com.yxy.model.pojo.Product;
import com.yxy.model.request.AddProductReq;
import com.yxy.model.request.UpdateProductReq;
import com.yxy.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * 描述:      后台商品管理Controller
 */
@RestController
public class ProductAdminController {

    @Autowired
    ProductService productService;

    @ApiOperation("管理员新增商品")
    @PostMapping("/admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    @ApiOperation("管理员新增商品的时候上传图片")
    @PostMapping("/admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest,
                                  @RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();//得到上传时的文件名
        String suffixName = fileName.substring(fileName.indexOf("."));//获取上传图片的后缀名，如jpg,png
        //生成文件名称UUID,UUID是重复几率很小的
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;   //拼接生成图片名称和后缀
        //创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);

        //判断这个文件夹存不存在
        if (!fileDirectory.exists()) {
            //创建这个文件夹，如果创建失败就抛出错误
            if (!fileDirectory.mkdir()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            //把内存图片写入磁盘中,就是把上传的图片，写入到服务器的指定位置
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //此处获取的是URL，而要传进去的是URI
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL() + "")) + "/images/" + newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.UPLOAD_FAILED);
        }
    }

    private URI getHost(URI uri) {
        URI effectiveURI; // effective 有效的
        try {
            /**
             *      scheme:[//[user:password@]host[:port]][/]path[?query][#fragment]
             *      协议:[//[用户名:密码@]域名[:端口]][/]路径[?参数][#标记]
             */
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                    null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    @ApiOperation("后台更新商品")
    @PostMapping("/admin/product/update")
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品")
    @PostMapping("/admin/product/delete")
    public ApiRestResponse deleteProduct(@RequestParam Integer id) {
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量上下架接口")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids,
                                                 @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品列表接口")
    @GetMapping("/admin/product/list")
    public ApiRestResponse list(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize) {
        PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

}

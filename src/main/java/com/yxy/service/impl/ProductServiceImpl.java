package com.yxy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yxy.common.Constant;
import com.yxy.exception.ImoocMallException;
import com.yxy.exception.ImoocMallExceptionEnum;
import com.yxy.model.dao.ProductMapper;
import com.yxy.model.pojo.Product;
import com.yxy.model.query.ProductListQuery;
import com.yxy.model.request.AddProductReq;
import com.yxy.model.request.ProductListReq;
import com.yxy.model.vo.CategoryVO;
import com.yxy.service.CategoryService;
import com.yxy.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：  商品服务实现类
 */

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryService categoryService;

    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        //看看有没有重名的，
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if (productOld != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(Product updateProduct) {
        Product productOld = productMapper.selectByName(updateProduct.getName());
        //如果名字相同，但是id不同，就不能继续修改，并提示名字重复
        if (productOld != null && !productOld.getId().equals(updateProduct.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        if (productOld == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        if (ids.length == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.PARA_NOT_NULL);
        }
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    @Override
    public PageInfo list(ProductListReq productListReq) {
        //构建Query对象，对应的类在model/query中
        ProductListQuery productListQuery = new ProductListQuery();
        //搜索处理
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            //如果搜索的名程不为空,就执行拼接，但是此处，我觉得不是很好，可以用mybatis的方法，concast方法拼接
            String keyWord = new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyWord); //把名称放进去
        }
        //目录处理：如果查某个目录下的商品，不仅是需要查出该目录下的，还要把所有子目录的所有商品都查出来，
        // 所以要拿到所有的id，放进一个list集合
        if (productListReq.getCategoryId() != null) {
            List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIdList = new ArrayList<>();
            categoryIdList.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOList, categoryIdList);
            productListQuery.setCategoryIds(categoryIdList);//把要查询的id集合放进去
        }

        /**
         *  排序处理
         *  这个里面的orderBy 代表前端传来的，前端的接口文档中写了orderBy中只能传price desc 或者 price asc
         *  而且，PageHelper的排序规则是，PageHelper.orderBy("A B");
         *  其中A为排序依据的字段名，B为排序规律，desc为降序，asc为升序
         */
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), productListReq.getOrderBy());
        } else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }
        List<Product> products = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }

    private void getCategoryIds(List<CategoryVO> categoryVOList, List<Integer> categoryIdList) {
        for (int i = 0; i < categoryVOList.size(); i++) {
            CategoryVO categoryVO = categoryVOList.get(i);
            if (categoryVO != null) {
                categoryIdList.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIdList);
            }
        }
    }

}

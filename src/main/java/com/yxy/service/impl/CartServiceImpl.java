package com.yxy.service.impl;

import com.yxy.common.Constant;
import com.yxy.exception.ImoocMallException;
import com.yxy.exception.ImoocMallExceptionEnum;
import com.yxy.model.dao.CartMapper;
import com.yxy.model.dao.ProductMapper;
import com.yxy.model.pojo.Cart;
import com.yxy.model.pojo.Product;
import com.yxy.model.vo.CartVO;
import com.yxy.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：     购物车Service实现类
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    ProductMapper productMapper;

    @Autowired
    CartMapper cartMapper;

    @Override
    public List<CartVO> list(Integer userId) {
        List<CartVO> cartVOS = cartMapper.selectList(userId);
        for (int i = 0; i < cartVOS.size(); i++) {
            CartVO cartVO = cartVOS.get(i); //查出每一个购物车的商品，算出每一个商品的总价，然后放进去
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOS;
    }

    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        //查看商品是否上架和库存不足
        //此处我觉得，可以分开写，写两个判断条件，
        // 如果购物车里面本来就有一些数量，虽然传进来的count没有超库存，但是加上购物车的数量可能就超了，
        validProduct(productId, count);


        //根据商品id和用户id查询这个商品有没有在购物车，
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //如果不在购物车，就需要新增一条记录
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);//默认状态是勾选的
            cartMapper.insertSelective(cart);
        } else {
            //如果不在购物车就更改一下勾选状态和加一下数量，就ok了
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //如果添加完了，可以把当前用户的购物车重新查询一次，再返回
        return this.list(userId);
    }

    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count) {
        //查看商品是否上架和库存不足
        validProduct(productId, count);

        //根据商品id和用户id查询这个商品有没有在购物车，
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //如果不在购物车，就无法更新
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        } else {
            //如果已经在购物车里面了，就更新数量
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //如果更新完了，可以把当前用户的购物车重新查询一次，再返回
        return this.list(userId);
    }

    @Override
    public List<CartVO> delete(Integer userId, Integer productId) {
        //根据商品id和用户id查询这个商品有没有在购物车，
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里，无法删除
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        } else {
            //这个商品已经在购物车里了，则可以删除
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        //如果删除完了，可以把当前用户的购物车重新查询一次，再返回
        return this.list(userId);
    }

    private void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        //如果商品不存在，或者是下架的状态，就抛出错误
        if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
        }
        //如果商品库存不足就抛出异常
        if (product.getStock() < count) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
        }
    }


    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里，无法选择/不选中
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        } else {
            //这个商品已经在购物车里了，则可以选中/不选中
            cartMapper.selectOrNot(userId,productId,selected);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> selectAllOrNot(Integer userId, Integer selected) {
        //改变选中状态
        //这个就可以实现sql语句的复用，当productId传入的为null时，就根据userid全部改变状态
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }
}

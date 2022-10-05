package com.yxy.model.request;


import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述：     AddCategoryReq
 * 问题：为什么已经有了一个pojo类，但是还要单独新建一个这个类呢，
 * 回答：
 * 1、因为pojo是和数据库对应的，咱们不应该让那一个类实现两个不同的功能
 * 2、因为咱们的参数只需要几个而已，用不到pojo里面那么多参数，防止黑客利用参数来更改别的
 * 3、只有单独弄出来一个类，咱们才能在这个类上添加注解，来验证参数，更方便，
 * <p>
 * 注意：下面这些注解只有在把这个类当成参数的时候，并且在参数前添加注解@Valid 才代表这些限制在本次传参中生效
 * 标上 @Valid 注解，表示我们对这个对象属性需要进行验证，
 */
public class AddCategoryReq {

    @Size(min = 2, max = 5)
    @NotNull(message = "name不能为null")
    private String name;

    @NotNull(message = "type不能为null")
    @Max(3)
    private Integer type;

    @NotNull(message = "parentId不能为null")
    private Integer parentId;

    @NotNull(message = "orderNum不能为null")
    private Integer orderNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "AddCategoryReq{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", parentId=" + parentId +
                ", orderNum=" + orderNum +
                '}';
    }
}

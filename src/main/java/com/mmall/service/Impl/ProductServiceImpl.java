package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListItemVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahong on 2022/8/25.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorMessage("新增或更新商品失败");
        }
        if (StringUtils.isNotBlank(product.getSubImages())) {
            String[] subImageArray = product.getSubImages().split(",");
            if (subImageArray.length > 0) {
                product.setMainImage(subImageArray[0]);
            }
        }

        if (product.getId() == null) {
            int rowCount = productMapper.insert(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("创建商品成功");
            }
            return ServerResponse.createByErrorMessage("创建商品失败");
        } else {
            int rowCount = productMapper.updateByPrimaryKey(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("更新商品成功");
            }
            return ServerResponse.createByErrorMessage("更新商品失败");
        }

    }

    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        // 使用mapper插入或者更新一个对象，首先创建这个对象product

        int rowCount = productMapper.updateByPrimaryKeySelective(product); // 如果product的某些属性是null就不覆盖原有值
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("更新商品状态成功");
        }
        return ServerResponse.createByErrorMessage("更新商品状态失败");
    }

    public ServerResponse manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品不存在");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://localhost/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null) {
            productDetailVo.setParentCategoryId(0); // 默认根节点
        }else{
            productDetailVo.setParentCategoryId(product.getCategoryId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return  productDetailVo;

    }

    public ServerResponse getProductList(int pageNum, int pageSize){
        //startPage--start
        //填充自己的sql查询逻辑
        //pageHelper-收尾
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();

        List<ProductListItemVo> productListVO= Lists.newArrayList();
        for(Product productItem: productList) {
            ProductListItemVo productListItemVo = assembleProductListItemVo(productItem);
            productListVO.add(productListItemVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVO);

        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListItemVo assembleProductListItemVo(Product product){
        ProductListItemVo productListItemVo = new ProductListItemVo();
        productListItemVo.setId(product.getId());
        productListItemVo.setSubtitle(product.getSubtitle());
        productListItemVo.setPrice(product.getPrice());
        productListItemVo.setMainImage(product.getMainImage());
        productListItemVo.setName(product.getName());
        productListItemVo.setStatus(product.getStatus());

        productListItemVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://localhost/"));

        return  productListItemVo;
    }

    public ServerResponse searchProduct(String productName,Integer productId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }

        System.out.println("productName%%%%%%%%:"+productName);

        List<Product> productList = productMapper.selectListByNameAndId(productName, productId);

        List<ProductListItemVo> productListVO = Lists.newArrayList();
        for(Product productItem: productList) {
            ProductListItemVo productListItemVo = assembleProductListItemVo(productItem);
            productListVO.add(productListItemVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVO);

        return  ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy){
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<>();

        if(categoryId !=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类，并且还没有关键字，这时候返回一个空的结果集，不报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListItemVo> productListItemVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListItemVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.getCategoryAndDeepChildrenCategory(category.getId()).getData();
        }

        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum, pageSize);
        // 排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListItemVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListItemVo productListVo = assembleProductListItemVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);

    }
}



package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * 品牌管理
 */
@Service
public class BrandService {


    @Resource
    private BrandMapper brandMapper;

    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌信息
        this.brandMapper.insertSelective(brand);
        //新增品牌和分类中间表
        for (Long cid : cids) {
            this.brandMapper.insertCategoryBrand(cid, brand.getId());
        }
    }

    /**
     * 删除品牌
     *
     * @param bid
     */
    @Transactional
    public void deleteBrand(Long bid) {
        int deleteCategory = this.brandMapper.deleteCategoryBrand(bid);
        int deleteByPrimary = this.brandMapper.deleteByPrimaryKey(bid);
        System.out.println(deleteCategory+deleteByPrimary);
    }

    /**
     * 修改品牌
     *
     * @param brand
     * @param cids
     */
    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {
        //修改品牌信息
        this.brandMapper.updateByPrimaryKey(brand);
    }

    /**
     * 查询所有品牌
     *
     * @return
     */
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }


    /**
     * @param brand
     * @return
     */
    public List<Brand> findAllSelect(Brand brand) {
        return brandMapper.select(brand);
    }


    /**
     * 分页查询
     */
    public List<Brand> findByPage(Brand brand, int pageNum, int pageSize) {
        // 使用分页插件:
        PageHelper.startPage(pageNum, pageSize);
        // 进行条件查询:
        return brandMapper.selectByExample(brand);
    }

    /**
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCategory(Long cid) {
        return this.brandMapper.queryByCategoryId(cid);
    }

    /**
     * @param id
     * @return
     */
    public Brand queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    /**
     *
     */
    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        // 开始分页
        PageHelper.startPage(page, rows);
        // 过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().andLike("name", "%" + key + "%")
                    .orEqualTo("letter", key);
        }
        if (StringUtils.isNotBlank(sortBy)) {
            // 排序
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        // 查询
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);
        // 返回结果
        return new PageResult<>(pageInfo.getTotal(), pageInfo);
    }

    /**
     * 根据多个id查询品牌
     * @param ids
     * @return
     */
    public List<Brand> queryBrandByIds(List<Long> ids) {
        return this.brandMapper.selectByIdList(ids);
    }
}
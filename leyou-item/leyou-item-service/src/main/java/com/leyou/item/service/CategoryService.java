package com.leyou.item.service;

import com.leyou.item.api.CategoryApi;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 分类管理
 */
@Service
public class CategoryService implements CategoryApi {

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点查询商品类目
     *
     * @param pid
     * @return
     */
    public List<Category> queryListByParent(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return this.categoryMapper.select(record);
    }

    /**
     * 根据ID查询商品列表
     *
     * @return
     */
    public List<Category> queryByBrandId(Long bid) {
        return this.categoryMapper.queryByBrandId(bid);
    }

    /**
     * 根据id删除
     *
     * @param bid
     */
    public void deleteById(Long bid) {
        categoryMapper.deleteByBrandId(bid);
    }

    /**
     * 根据id查询商品分类
     *
     * @param ids
     */
    @Override
    public List<Category> queryCategoryListByids(List<Long> ids) {
        return null;
    }

    /**
     * @param ids
     * @return
     */
    @Override
    public List<String> queryNameByIds(List<Long> ids) {
        List<Category> list = this.categoryMapper.selectByIdList(ids);
        List<String> names = new ArrayList<>();
        for (Category category : list) {
            names.add(category.getName());
        }
        return names;
    }

    /**
     * 根据三级分类id查询1~3级分类集合
     *
     * @param id
     * @return
     */
    public List<Category> queryAllByCid3(Long id) {
        Category c3 = this.categoryMapper.selectByPrimaryKey(id);
        Category c2 = this.categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = this.categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1, c2, c3);
    }
}

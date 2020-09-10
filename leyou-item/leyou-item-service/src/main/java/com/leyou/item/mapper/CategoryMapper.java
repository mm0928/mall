package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * category_id=商品类目id
 * brand_id=品牌id
 */
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category, Long> {
    /**
     * 根据品牌id查询商品分类
     *
     * @param bid
     * @return
     */
    @Select("SELECT * FROM tb_category WHERE id IN (SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> queryByParentId(Long bid);


    /**
     * 根据品牌id查询商品分类
     *
     * @param bid
     * @return
     */
    @Select("SELECT * FROM tb_category WHERE id IN (SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> queryByBrandId(Long bid);

    /**
     * 根据bid 删除商品列表
     */
    @Delete("DELETE tb_brand,tb_category FROM tb_category LEFT JOIN tb_brand ON tb_brand.id = tb_category.id WHERE tb_category.id = #{bid}")
    void deleteByBrandId(Long bid);

}

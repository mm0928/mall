package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 商品品牌
 */
public interface BrandMapper extends Mapper<Brand>, SelectByIdListMapper<Brand, Long> {

    /**
     * 新增商品分类和品牌中间表数据
     * 通用Mapper只能处理单表，也就是Brand的数据，因此我们手动编写一个方法及sql，实现中间表的新增：
     *
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return
     */
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     * 根据分类查询品牌有中间表
     *
     * @param cid
     * @return
     */
    @Select("SELECT b.* FROM tb_brand b LEFT JOIN tb_category_brand cb ON b.id = cb.brand_id WHERE cb.category_id = #{cid}")
    List<Brand> queryByCategoryId(Long cid);

    /**
     * 删除商品分类和品牌中间表数据
     */
    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    int deleteCategoryBrand(@Param("bid") Long bid);



}

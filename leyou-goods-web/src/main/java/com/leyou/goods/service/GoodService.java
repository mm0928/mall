package com.leyou.goods.service;


import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service
public class GoodService {

    @Resource
    private BrandClient brandClient;
    @Resource
    private CategoryClient categoryClient;
    @Resource
    private GoodsClient goodsClient;
    @Resource
    private SpecificationClient specificationClient;

    /**
     * @param spuId
     * @return
     */
    public Map<String, Object> loadData(Long spuId) {

        Map<String, Object> model = new HashMap<>();

        // 根据SpuId查询spu
        Spu spu = goodsClient.querySpuById(spuId);

        // 查询SpuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spuId);

        // 查询分类：Map<String,Object>
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = categoryClient.queryNameByIds(cids);

        // 初始化一个分类的map
        List<HashMap<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }

        // 查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        // skus-querySkusBySpuId
        List<Sku> skus = goodsClient.querySkuBySpuId(spuId);

        // 查询规格参数组
        List<SpecGroup> groups = specificationClient.querySpecsByCid(spu.getCid3());
        // 查询特殊的规格参数
        List<SpecParam> params = specificationClient.querySpecParams(null, spu.getCid3(), false, null);
        // 初始化特殊规格参数的map
        HashMap<Long, String> paramMap = new HashMap<>();
        params.forEach(param -> {
            paramMap.put(param.getId(), param.getName());
        });

        model.put("spu", spu);
        model.put("spuDetail", spuDetail);
        model.put("categories", categories);
        model.put("brand", brand);
        model.put("skus", skus);
        model.put("groups", groups);
        model.put("paramMap", paramMap);

        return model;
    }
}

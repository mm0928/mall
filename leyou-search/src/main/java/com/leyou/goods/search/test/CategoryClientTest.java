package com.leyou.goods.search.test;

import com.leyou.common.pojo.PageResult;
import com.leyou.goods.LySearch;
import com.leyou.goods.search.client.CategoryClient;
import com.leyou.goods.search.client.GoodsClient;
import com.leyou.goods.search.pojo.Goods;
import com.leyou.goods.search.repository.GoodsRepository;
import com.leyou.goods.search.service.SearchService;
import com.leyou.item.pojo.Spu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearch.class)
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;

    @Test
    public void createIndex() {
        // 创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Goods.class);
    }

    @Test
    public void testQueryCategories() {
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(1L, 2L, 3L));
        names.forEach(System.out::println);
    }

    @Test
    public void loadData() {
        // 创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Goods.class);
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            // 查询分页数据
            PageResult<Spu> result = this.goodsClient.querySpuByPage(page, rows, true, null);

            List<Spu> spus = result.getItems();
            size = spus.size();
            // 创建Goods集合
            List<Goods> goodsList = new ArrayList<>();
            // 遍历spu
            for (Spu spu : spus) {
                try {
                    Goods goods = this.searchService.buildGoods(spu);
                    goodsList.add(goods);
                } catch (Exception e) {
                    break;
                }
            }

            this.goodsRepository.saveAll(goodsList);
            page++;
        } while (size == 100);
    }
}
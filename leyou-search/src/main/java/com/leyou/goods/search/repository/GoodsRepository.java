package com.leyou.goods.search.repository;

import com.leyou.goods.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 导入数据只做一次,以后的更新删除等操作通过消息队列来操作索引库
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {

}

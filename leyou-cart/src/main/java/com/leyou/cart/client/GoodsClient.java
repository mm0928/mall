package com.leyou.cart.client;

import com.leyou.item.api.GoodsApi;
import com.leyou.item.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {

    Sku querySkuById(Long skuId);
}

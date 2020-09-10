package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品管理-CRUD
 * <p>
 * SPU：Standard Product Unit （标准产品单位） ，一组具有共同属性的商品集
 * SKU：Stock Keeping Unit（库存量单位），SPU商品集因具体特性不同而细分的每个商品
 */
@RestController
@RequestMapping("goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    /**
     * 分页查询Spu
     *
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    @GetMapping("/spu/page")
    @ApiOperation(value = "分页查询Spu", notes = "分页查询Spu")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", defaultValue = "true") Boolean saleable) {
        PageResult<SpuBo> result = goodsService.querySpuByPage(key, saleable, page, rows);
        if (result == null || CollectionUtils.isEmpty(result.getItems())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增商品
     *
     * @param spuBo
     * @return
     */
    @PostMapping("/save")
    @ApiOperation(value = "新增商品", notes = "新增商品")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo) {
        goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品
     *
     * @param spuBo
     * @return
     */
    @PutMapping()
    @ApiOperation(value = "修改商品", notes = "修改商品")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo) {
        goodsService.updateGoods(spuBo);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * 查询SpuDetail
     *
     * @param id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    @ApiOperation(value = "根据spuId查询SpuDetail", notes = "根据spuId查询SpuDetail")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("id") Long id) {
        SpuDetail spuDetail = goodsService.querySpuDetailBySpuId(id);
        if (spuDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 查询sku
     *
     * @param id
     * @return
     */
    @GetMapping("/sku/list")
    @ApiOperation(value = "根据spuId查询Sku集合", notes = "根据spuId查询Sku集合")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id") Long id) {
        List<Sku> skus = goodsService.querySkusBySpuId(id);
        //遍历集合
        for (Sku sku : skus) {
            System.out.println("Sku集合:" + sku);
        }
        if (CollectionUtils.isEmpty(skus)) {
            //如果为空，返回Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(skus);
    }

    /**
     * 根据spuId查询Spu
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation(value = "根据spuId查询Spu", notes = "根据spuId查询Spu")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        Spu spu = goodsService.querySpuById(id);
        if (spu == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(spu);
    }

    /**
     * 根据skuId查询Sku
     *
     * @param skuId
     * @return
     */
    @GetMapping("sku/{skuId}")
    @ApiOperation(value = "根据skuId查询Sku", notes = "根据skuId查询Sku")
    public ResponseEntity<Sku> querySkuBySkuId(@PathVariable("skuId") Long skuId) {
        Sku sku = goodsService.querySkuBySkuId(skuId);
        if (sku == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(sku);
    }


    /**
     *
     */
}

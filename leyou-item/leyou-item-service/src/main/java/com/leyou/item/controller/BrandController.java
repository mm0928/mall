package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 品牌管理-CRUD
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Resource
    private BrandService brandService;


    /**
     * 新增品牌
     */
    @PostMapping("/add")
    public ResponseEntity<Brand> addBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        this.brandService.saveBrand(brand, cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 删除品牌
     *
     * @param cid
     * @return
     */
    @PostMapping("/del/{cid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("cid") Long cid) {
        this.brandService.deleteBrand(cid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 修改商品
     */
    @PutMapping("/update")
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        this.brandService.updateBrand(brand, cids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 查询所有商品
     *
     * @param brand
     * @return
     */
    @RequestMapping("/findAllSelect")
    public ResponseEntity<List<Brand>> findAllSelect(Brand brand) {
        List<Brand> list = brandService.findAllSelect(brand);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 查询所有品牌
     *
     * @return
     */
    @RequestMapping("/all")
    public ResponseEntity<List<Brand>> findAll() {
        List<Brand> list = this.brandService.findAll();
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }


    /**
     * 根据多个id查询品牌
     *
     * @param ids
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids) {
        List<Brand> list = this.brandService.queryBrandByIds(ids);
        if (list == null) {
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 分页查询
     * pageNum：页数（第几页）
     * pageSize：每页的数据行数
     */
    @RequestMapping("/search")
    public ResponseEntity<List<Brand>> search(@RequestBody Brand brand, int pageNum, int pageSize) {

        return (ResponseEntity<List<Brand>>) brandService.findByPage(brand, pageNum, pageSize);
    }

    /**
     * 根据cid查询品牌
     *
     * @param cid
     * @return
     */
    @RequestMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandListByCid(@PathVariable("cid") Long cid) {

        List<Brand> list = brandService.queryBrandByCategory(cid);
        if (list == null) {
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 分页查询品牌
     *
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    @RequestMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key) {
        PageResult<Brand> result = brandService.queryBrandByPageAndSort(page, rows, sortBy, desc, key);
        if (result == null || result.getItems().size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }


}

package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类管理-CRUD
 */
@RestController
@RequestMapping("category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 根据父节点查询商品类目
     *
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryByParentId(
            @RequestParam(value = "pid", defaultValue = "0") Long pid) {
        List<Category> list = this.categoryService.queryListByParent(pid);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 根据ID查询商品列表
     *
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("cid") Long cid) {

        List<Category> list = this.categoryService.queryByBrandId(cid);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 根据ID删除商品
     */
    @GetMapping("del/{bid}")
    public ResponseEntity deleteByBrand(@PathVariable("bid") Long bid) {
        categoryService.deleteById(bid);
        return ResponseEntity.ok(bid);

    }

    /**
     * 根据商品分类id查询名称
     *
     * @param ids 要查询的分类id集合
     * @return 多个名称的集合
     */
    @GetMapping("names")
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids") List<Long> ids) {
        List<String> list = this.categoryService.queryNameByIds(ids);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

}

package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 规格管理-CRUD
 */
@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Resource
    private SpecificationService specificationService;

    /**
     * 根据分类cid查询规格组
     *
     * @param cid
     * @return
     */
    @RequestMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }

    /**
     * 查询规格
     *
     * @param gid
     * @param cid
     * @param searching
     * @param generic
     * @return
     */
    @RequestMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching,
            @RequestParam(value = "generic", required = false) Boolean generic) {
        return ResponseEntity.ok(specificationService.querySpecParams(gid, cid, searching, generic));
    }

    /**
     * 根据分类查询规格组及组内分类
     *
     * @param cid
     * @return
     */
    @RequestMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specificationService.querySpecsByCid(cid));
    }

    /**
     * 查询所有规格组
     *
     * @return
     */
    @RequestMapping("/group")
    public ResponseEntity<List<SpecGroup>> querySpecsAll() {
        return ResponseEntity.ok(specificationService.querySpecsAll());
    }

}

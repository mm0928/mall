package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 *
 */
public interface SpecificationApi {

    /**
     * @param gid
     * @param cid
     * @param searching
     * @param generic
     * @return
     */
    @GetMapping("spec/params")
    List<SpecParam> querySpecParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching,
            @RequestParam(value = "generic", required = false) Boolean generic);

    /**
     * 查询规格参数组，及组内参数
     *
     * @param cid
     * @return
     */
    @GetMapping("/spec/{cid}")
    List<SpecGroup> querySpecsByCid(@PathVariable("cid") Long cid);


}

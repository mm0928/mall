package com.leyou.goods.controller;

import com.leyou.goods.service.GoodService;
import com.leyou.goods.service.GoodsHtmlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

/**
 *
 */
@Controller
@RequestMapping("item")
public class GoodsController {

    @Resource
    private GoodService goodsService;

    @Resource
    private GoodsHtmlService goodsHtmlService;

    /**
     * 跳转到商品详情页
     * @param model
     * @param id
     * @return
     */
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id")Long id,Model model){

        Map<String, Object> map = goodsService.loadData(id);

        model.addAllAttributes(map);

        goodsHtmlService.createHtml(id);
        return "item";
    }
}

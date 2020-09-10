package com.leyou.goods.service;


import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


@Service
public class GoodsHtmlService {

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private GoodService goodsService;

    public void createHtml(Long spuId) {
        // 初始化运行上下文
        Context context = new Context();
        // 设置数据模型
        context.setVariables(goodsService.loadData(spuId));

        PrintWriter printWriter = null;
        try {
            // 把静态文件生成到服务器本地
            File file = new File("E:\\software\\nginx-1.15.0\\temp\\" + spuId + ".html");
            printWriter = new PrintWriter(file);
            // 执行页面静态化方法
            templateEngine.process("item", context, printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }

    }

    public void deleteHtml(Long id) {
        File file = new File("E:\\software\\nginx-1.15.0\\temp\\" + id + ".html");
        file.deleteOnExit();
    }
}

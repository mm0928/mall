package test.com.leyou.upload.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;
import com.leyou.LyUploadApplication;
import com.leyou.upload.config.FastDFSClientWrapper;
import com.leyou.upload.controller.UploadController;
import com.leyou.upload.service.UploadService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

/**
 * 上传测试
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyUploadApplication.class)
public class UploadControllerTest {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Autowired
    private UploadService uploadService;

    @Test
    public void testUploadImage() throws Exception {
        File file = new File("E:\\我的图片\\xcjx.png");
        System.out.println(file);
        // 上传并且生成缩略图
        StorePath storePath = this.storageClient.uploadFile(
                new FileInputStream(file), file.length(), "png", null);
        System.out.println("生成缩略图" + storePath);
        // 带分组的路径
        System.out.println(storePath.getFullPath());
        // 不带分组的路径
        System.out.println(storePath.getPath());
    }

    @Test
    public void testUploadAndCreateThumb() throws FileNotFoundException {
        File file = new File("E:\\我的图片\\aaa.png");
        // 上传并且生成缩略图
        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(
                new FileInputStream(file), file.length(), "png", null);
        // 带分组的路径
        System.out.println(storePath.getFullPath());
        // 不带分组的路径
        System.out.println(storePath.getPath());
        // 获取缩略图路径
        String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
        System.out.println(path);
    }

    @Autowired
    private FastDFSClientWrapper fastDFSClientWrapper;

    @Test
    public void test() throws IOException {
        File file = new File("D:\\circle.png");
        String s = fastDFSClientWrapper.uploadFile(file);
        System.out.println(s);

    }


} 

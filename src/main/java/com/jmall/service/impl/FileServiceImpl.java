package com.jmall.service.impl;

import com.google.common.collect.Lists;
import com.jmall.service.IFileService;
import com.jmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private static final Logger logger =  LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path) {
        //获取文件名（为的是获取文件的扩展名，这里要把上传的图片的名字统一用UUID做唯一处理）
        String fileName = file.getOriginalFilename();
        //获取文件扩展名，如JPG
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //拼接上传文件名
        String uploadFileName = UUID.randomUUID() + "." + fileExtensionName;
        //logback的logger.info有个重载方法，前面文本可用{}占位，{}里面可依次填充后面跟着的属性，顺序要一致
        logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);

        //新建文件路径
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //新建上传文件
        File targetFile = new File(path,uploadFileName);
        //开始上传文件
        try {
            file.transferTo(targetFile);
            //到这里文件成功上传到upload文件夹下

            //把upload文件夹下的targetFile上传到FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            //删除upload下的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }
}

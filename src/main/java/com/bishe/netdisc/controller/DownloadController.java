package com.bishe.netdisc.controller;

import com.bishe.netdisc.common.entity.Result;
import com.bishe.netdisc.common.entity.ResultCode;
import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.entity.Directory;
import com.bishe.netdisc.entity.UserFile;
import com.bishe.netdisc.hdfs.HDFSService;
import com.bishe.netdisc.mapper.DirectoryDao;
import com.bishe.netdisc.mapper.UserFileDao;
import com.bishe.netdisc.service.DownloadService;
import org.apache.hadoop.fs.FileSystem;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @author third_e
 * @create 2020/5/6 0006-下午 8:41
 */
@RestController
@RequestMapping("/download")
public class DownloadController {

    @Autowired
    private UserFileDao userFileDao;
    @Autowired
    private DirectoryDao directoryDao;
    @Autowired
    private HDFSService hdfsService;
    @Autowired
    private DownloadService downloadService;

    // 单文件下载
    @PostMapping("/onefile")
    @RequiresPermissions(logical = Logical.AND, value = {"/aaa/download"})
    public Result downloadOne (HttpServletResponse response, HttpServletRequest request, UserFile userFile) {

        System.out.println("******"+userFile);
        System.out.println(request.getParameter("id"));
        // 判断
        if (userFile.getId() == null || userFile.getId() == "" || userFile.getType() == null || userFile.getType() == "") {
            throw new CommonException("下载失败");
        }
        if (!"dir".equals(userFile.getType())){
            // 查询
            UserFile downloadFile = this.userFileDao.queryOne(userFile);
            String fileName;
            if (downloadFile.getType() != null && downloadFile.getType() == ""){
                fileName = downloadFile.getFilename()+"."+downloadFile.getType();
            }else {
                fileName = downloadFile.getFilename();
            }
            try {
                response.setHeader("Content-Disposition", "attachment;fileName=" + new String( fileName.getBytes("gb2312"), "ISO8859-1" ));
                this.hdfsService.download(downloadFile.getFilepath(),response.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 多文件下载
    @PostMapping("/morefile")
    @RequiresPermissions(logical = Logical.AND, value = {"/aaa/download"})
    public String downloadmore(HttpServletResponse response, String id, String type) throws Exception{
        if (id == null || id == "" || type ==null || type == ""){
            throw new CommonException("下载失败");
        }
        List<String> listId = Arrays.asList(id.split(","));
        List<String> listType = Arrays.asList(type.split(","));
        System.out.println(listId.size()+"--"+listType.size());
        if (listId.size() == listType.size()){
//            throw new CommonException("下载失败");
        }
        List<String> files = new ArrayList<>();
        List<String> dirs = new ArrayList<>();
        for (int i=0;i< listType.size();i++) {
            if ("dir".equals(listType.get(i))){
                dirs.add(listId.get(i));
            }else {
                files.add(listId.get(i));
            }
        }
        System.out.println(dirs);
        System.out.println(files);
        // 1、当前文件
        // 获取文件路径
        List<UserFile> userFiles = new ArrayList<>();
        for (String file:files) {
            UserFile userFile = this.userFileDao.queryById(file);
            userFiles.add(userFile);
        }
        // 2、当前目录
        List<Directory> directories = new ArrayList<>();
        for (String dir:dirs){
            Directory directory = this.directoryDao.queryById(dir);
            directories.add(directory);
        }

        OutputStream out = response.getOutputStream();
        ZipOutputStream zos = new ZipOutputStream(out);
        this.downloadService.downloadMore(directories,userFiles,zos,"");
        zos.close();
        return null;
    }
}

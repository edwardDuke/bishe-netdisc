package com.bishe.netdisc.controller;

import com.bishe.netdisc.common.entity.Result;
import com.bishe.netdisc.common.entity.ResultCode;
import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.common.utils.UserUtil;
import com.bishe.netdisc.common.utils.common.DateUtil;
import com.bishe.netdisc.common.utils.common.FileTypeUtil;
import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.entity.UserFile;
import com.bishe.netdisc.entity.common.upload.Chunk;
import com.bishe.netdisc.hdfs.HDFSService;
import com.bishe.netdisc.mapper.RoleDao;
import com.bishe.netdisc.mapper.UserDao;
import com.bishe.netdisc.mapper.UserFileDao;
import org.apache.hadoop.fs.FileStatus;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author third_e
 * @create 2020/5/4 0004-下午 1:57
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    HDFSService hdfsService;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private UserFileDao userFileDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;

    private static final String TEMPPATH = "/temp";
    /**
     *  分片存储
     *
     */
    @PostMapping("/chunkupload")
    @RequiresPermissions(logical = Logical.AND, value = {"/aaa/upload"})
    public Result chunkUpload(Chunk chunk) throws Exception{
        // 获取文件流
        MultipartFile file = chunk.getFile();
        // 临时文件路径，文件的MD5值为文件名
        String temPath = TEMPPATH + "/" + chunk.getIdentifier();
        // 创建一个临时文件夹
        this.hdfsService.mkdir(temPath);
        // 临时存储块排序名
        System.out.println(chunk);
        if (chunk.getChunkNumber().length() == 1) {
            chunk.setChunkNumber("0000"+chunk.getChunkNumber());
        }
        if (chunk.getChunkNumber().length() == 2) {
            chunk.setChunkNumber("000"+chunk.getChunkNumber());
        }
        if (chunk.getChunkNumber().length() == 3) {
            chunk.setChunkNumber("00"+chunk.getChunkNumber());
        }
        if (chunk.getChunkNumber().length() == 4) {
            chunk.setChunkNumber("0"+chunk.getChunkNumber());
        }
        String chunkName =temPath + "/" + chunk.getIdentifier()+"_"+ chunk.getChunkNumber();
        // 获取写入流
        InputStream is = file.getInputStream();
        this.hdfsService.upload(is,chunkName);
        // 上传成功则返回需要合并
        Map map = new HashMap();
        map.put("needMerge",true);
        return new Result(ResultCode.SUCCESS, map);
    }

    /**
     * 检查当前分片是否存在
     */
    @GetMapping("/chunkupload")
    @RequiresPermissions(logical = Logical.AND, value = {"/aaa/upload"})
    public Result chunkUpload(Chunk chunk,  HttpServletResponse response) throws Exception{
        // 获取当前用户
        User user = this.userUtil.getUser();
        // 获取存储总大小
        Role role = this.roleDao.queryById(user.getRoleid());
        if (role.getStoragesize()< user.getUsestoragesize()+Double.parseDouble(chunk.getTotalSize()+"")){
            Map map = new HashMap();
            map.put("skipUpload",true); // 已存在文件
            map.put("needMerge",false); // 不需要合并
            map.put("isSave",false);
            return new Result(ResultCode.SAVEFAIL,map);
        }
        System.out.println("============="+chunk);
        // 获取MD5
        String md5 = chunk.getIdentifier();
        //  当前块
        String chunkNum = null;
        if (chunk.getChunkNumber().length() == 1){
            chunkNum = "0000"+chunk.getChunkNumber();
        }else  if (chunk.getChunkNumber().length() == 2){
            chunkNum = "000"+chunk.getChunkNumber();
        }else  if (chunk.getChunkNumber().length() == 3){
            chunkNum = "00"+chunk.getChunkNumber();
        }else  if (chunk.getChunkNumber().length() == 4){
            chunkNum = "0"+chunk.getChunkNumber();
        }else {
            chunkNum = chunk.getChunkNumber();
        }
        UserFile queryfile = new UserFile();
        queryfile.setHash(md5);
        // 查询文件是否存在相同的MD5值
        UserFile userFile  = this.userFileDao.queryOne(queryfile);
        System.out.println("*****************"+userFile);
        Map<String,Object> map = new HashMap<>();
        map.put("skipUpload",false); // 初始化文件不存在
        map.put("isSave",true); // 表示可以上传
        // 如果MD5值相等，且大小相等，则直接返回文件存在，并且返回不需要文件合并
        if (userFile!= null && userFile.getFilesize().equals(Double.parseDouble(chunk.getTotalSize()+""))) {
            map.put("skipUpload",true); // 已存在文件
            map.put("needMerge",false); // 不需要合并
            // 写入需要修改的数据
            Date date = new Date();
            userFile.setId(null);
            // 获取文件类型
            int index = chunk.getFilename().lastIndexOf(".");
            String type =null;
            if (index != -1){
                type = chunk.getFilename().substring(index+1);
                String filename = chunk.getFilename().substring(0,index);
                queryfile.setDirectoryid(chunk.getDirId());
                queryfile.setFilestatus("enable");
                UserFile userFiledir  = this.userFileDao.queryOne(queryfile);
                System.out.println(filename);
                System.out.println(userFiledir);
                System.out.println(userFiledir != null);
                if (userFiledir != null ){
                    if (filename.equals(userFiledir.getFilename())) {
                        System.out.println("====================="+userFile);
                        filename = filename+DateUtil.getDateByFormatString("yyyyMMdd",date);
                    }
                    chunk.setFilename(filename);
                }else {
                    System.out.println("=====================+++++"+userFile);
                    chunk.setFilename(filename);
                }
            }
            userFile.setFilename(chunk.getFilename());
            userFile.setType(type);
            // 给文件分类别
            userFile.setTypename(FileTypeUtil.getFileTypeName(type));
            userFile.setDirectoryid(chunk.getDirId());
            userFile.setCreatetime(date);
            userFile.setFilestatus("enable");
            userFile.setDownloadnum(0);
            userFile.setUserid(user.getId());
            userFile.setLastmodifytime(date);
            // 存储文件信息
            this.userFileDao.save(userFile);
            // 修改用户存储大小
            user.setUsestoragesize(user.getUsestoragesize()+userFile.getFilesize());
            this.userDao.save(user);
            return new Result(ResultCode.SUCCESS,map);
        }
        // 如果不存在，则查询文件是否有上传
        // 查询文件是否有上传过
        // 1、获取存储路径，并查询文件是否存在
        String temPath = TEMPPATH + "/" + md5 ;
        if(!this.hdfsService.exitFile(temPath)){
            // 如果文件不存在不返回信息，表示全部上传
            return new Result(ResultCode.SUCCESS,map);
        } else {
            // 存在则查询已上传文件块
            List all = this.hdfsService.ls(temPath);
            System.out.println(all);
            map.put("uploaded",all); // 返回已存储块
            map.put("needMerge",true); // 需要合并
            return new Result(ResultCode.SUCCESS,map);
        }
    }

    /**
     * 合并分片
     */
    @PostMapping("/mergefile")
    public Result mergeFile (String fileName, String md5, String totalSize, String dirId) throws Exception {
        System.out.println(DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm:ss", new Date()));
        // 获取当前用户
        User user = userUtil.getUser();
        // 获取当天日期
        String date = DateUtil.getDateByFormatString("yyyyMMdd",new Date());
        // 获取合并目录文件路径
        FileStatus[] filesStatus = this.hdfsService.tempFile(TEMPPATH + "/" + md5);
        for (FileStatus fileStatus:filesStatus) {
           FileStatus fileStatus1 = fileStatus;
            System.out.println(fileStatus1.getPath().toUri().getPath());
        }
        //合并文件
        // 目标文件名
        String dirpPath = "/" + user.getAccount()+ "/" +date;
        // 创建hdfs目录
        this.hdfsService.mkdir(dirpPath);
        // 文件路径
        String desPath = dirpPath+"/"+fileName;
        // 合并文件
        this.hdfsService.mergeFile(filesStatus,desPath);

        // 判断合并是否正确
        String mergeMD5 = this.hdfsService.chechMD5(desPath);
        System.out.println(mergeMD5);
        System.out.println(DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm:ss", new Date()));
        if (mergeMD5.equals(md5)) {
            System.out.println("返回成功");
            // 存储文件信息
            Date nowdate = new Date();
            UserFile userFile = new UserFile();
            int index = fileName.lastIndexOf(".");
            String type = null;
            if (index != -1) {
                type = fileName.substring(index+1);
                fileName = fileName.substring(0,index);
            }
            userFile.setFilename(fileName);
            // 文件存储路径
            userFile.setFilepath(desPath);
            userFile.setDirectoryid(dirId);
            userFile.setCreatetime(nowdate);
            userFile.setFilesize(Double.valueOf(totalSize));
            userFile.setFilestatus("enable");
            userFile.setHash(md5);
            userFile.setType(type);
            // 文件类型分类
            userFile.setTypename(FileTypeUtil.getFileTypeName(type));
            userFile.setDownloadnum(0);
            userFile.setUserid(user.getId());
            userFile.setLastmodifytime(nowdate);
            // 存储文件信息
            this.userFileDao.save(userFile);
            // 修改用户存储大小
            user.setUsestoragesize(user.getUsestoragesize()+Double.valueOf(totalSize));
            this.userDao.save(user);
            // 合并完成则删除分块文件
            this.hdfsService.rm(TEMPPATH + "/" +md5);
            return new Result(ResultCode.SUCCESS);
        }else {
            System.out.println("返回失败");
            throw new CommonException("合并文件失败");
        }
    }
}

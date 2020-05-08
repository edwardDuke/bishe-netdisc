package com.bishe.netdisc.controller;

import com.bishe.netdisc.common.entity.Result;
import com.bishe.netdisc.common.entity.ResultCode;
import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.common.utils.UserUtil;
import com.bishe.netdisc.entity.Directory;
import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.entity.UserFile;
import com.bishe.netdisc.entity.common.QueryFile;
import com.bishe.netdisc.hdfs.HDFSServiceImp;
import com.bishe.netdisc.mapper.UserDao;
import com.bishe.netdisc.service.DirectoryService;
import com.bishe.netdisc.service.RoleService;
import com.bishe.netdisc.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @author third_e
 * @create 2020/4/18 0018-下午 5:51
 */
@RestController
@RequestMapping("/file")
public class UserFileController {
    @Autowired
    private UserUtil userUtil;

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserFileService userFileService;
    @Autowired
    private DirectoryService directoryService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private HDFSServiceImp hdfsServiceImp;


    /**
     * 查询某目录下的文件
     *
     * @param pid 目录id
     * @return
     */
    @GetMapping("/getdirecfile")
    public Result getfile ( String pid ) {
        System.out.println("查询某目录下的文件，当前目录id为"+pid);
        // 获取当前用户id
        String userId = userUtil.getUserId();
        List<UserFile> userFiles = new ArrayList<>();
        // 如果为空表示查询全部文件
        if (pid == null || pid == ""){
//            userFiles = userFileService.userFileDao().getFileByPid("0");
            System.out.println(pid);
        }else {

        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 文件重命名
     *
     * @param id 文件id
     * @param name 新的文件名
     * @return
     */

    @PostMapping("/renamefile")
    public Result rename (@RequestParam("id") String id,@RequestParam("name") String name){
        if (id == null || id == "" || name == null || name == ""){
            throw new CommonException("文件重命名失败");
        }
        // 查询文件
        UserFile userFile = userFileService.userFileDao().queryById(id);
        if (userFile == null) {
            throw new CommonException("文件不存在");
        }
        // 修改文件信息
        userFile.setFilename(name);
        userFile.setLastmodifytime(new Date());
        userFileService.userFileDao().save(userFile);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 文件删除
     *
     * @param id 文件id
     * @return
     */
    @GetMapping("/deletefile")
    public Result delete (@RequestParam("id") String id){
        if (id == null || id == "" ) {
            throw new CommonException("删除失败");
        }
        String[] strArr = id.split(",");
        for (int i = 0; i < strArr.length; ++i) {
            System.out.println(strArr[i]);
//            allId.add(strArr[i] + "");
            UserFile userFile = userFileService.userFileDao().queryById(strArr[i]);
            if (userFile == null){
                throw new CommonException("文件不存在，删除失败");
            }
//            allId.add(strArr[i] + "");
        }
        for (int i = 0; i < strArr.length; ++i) {
            this.userFileService.deleteById(strArr[i]);
        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 文件复制到
     *
     * @param sourceId 文件id
     * @param targetId 目标目录
     * @return
     */
    @GetMapping("/copyfile")
    public Result copy(@RequestParam("sourceId") String sourceId,@RequestParam("targetId") String targetId) {
        if (sourceId == null || sourceId == "" || targetId == null || targetId == ""){
            throw new CommonException("复制失败");
        }
        // 查询文件是否存在、目标目录是否存在
        UserFile userFile = userFileService.userFileDao().queryById(sourceId);
        Directory directory = directoryService.directoryDao().queryById(targetId);
        if (userFile == null || directory == null) {
            throw new CommonException("文件/目标目录不存在");
        }
        userFileService.copy(sourceId, targetId);
        return new Result(ResultCode.SUCCESS);
    }

    // 文件移动到
    @GetMapping("/movefile")
    public Result move (@RequestParam("sourceId") String sourceId,@RequestParam("targetId") String targetId) {
        if (sourceId == null || sourceId == "" || targetId == null || targetId == ""){
            throw new CommonException("移动失败");
        }
        // 判断文件、目标目录是否存在
        UserFile userFile = userFileService.userFileDao().queryById(sourceId);
        Directory targetDirectory = directoryService.directoryDao().queryById(targetId);
        if (userFile == null || targetDirectory == null){
            throw new CommonException("文件/目标目录不存在");
        }
        userFileService.move(sourceId, targetId);
        return new Result(ResultCode.SUCCESS);
    }

    // 通过类型查找文件
    @GetMapping("/type")
    public Result getFileByType(@RequestParam("type") String type) {

        if (type == "" || type == null) {
            throw new CommonException("操作失败");
        }
        String name = "";
        // 获取当前用户id
        String userId = userUtil.getUserId();
        // 查询类型
//        List conditions = new ArrayList();
        // 判断类型
        if ("picture".equals(type)){
            name = "图片";
//            conditions.add("jpg");
//            conditions.add("png");
//            conditions.add("jpeg");
//            conditions.add("psd");
//            conditions.add("gif");
//            conditions.add("bmp");

        }else if ("document".equals(type)) {
            name = "文档";
//            conditions.add("txt");
//            conditions.add("docx");
//            conditions.add("doc");
//            conditions.add("ppt");
//            conditions.add("pptx");
//            conditions.add("xls");
//            conditions.add("xlsx");
//            conditions.add("pdf");
        }else if ("video".equals(type)) {
            name = "视频";
//            conditions.add("mp4");
//            conditions.add("avi");
//            conditions.add("mkv");
//            conditions.add("rmvb");
        }else if ("music".equals(type)) {
            name = "音乐";
//            conditions.add("mp3");
//            conditions.add("flac");
        }else if ("other".equals(type)) {
            name = "其他";
//            conditions.add("zip");
//            conditions.add("rar");
        }
        List<UserFile> allFile = this.userFileService.getFileByType(type,userId);
        // 获取根目录
        Directory directory = this.directoryService.directoryDao().getDircByPid("0",userId);
        // 通过父目录id查找上一级目录
        List parDirectories = new ArrayList();
        parDirectories  = directoryService.getListParDircByid(directory.getId(),userId,parDirectories);
        Map nowpath = new HashMap();
        nowpath.put("id","1");
        nowpath.put("name","搜索："+ name);
        parDirectories.add(nowpath);
        Map<String,Object> map = new HashMap<>();
        map.put("id","1");
        map.put("file",allFile);
        map.put("dirc",null);
        map.put("allPath",parDirectories);
        return new Result(ResultCode.SUCCESS,map);
    }

    // 获取文件列表
    @PostMapping("/queryfiles")
    public Result queryFiles(@RequestParam(value = "userid",required = false) String userid,
                             @RequestParam(value = "filetype",required = false) String filetype,
                             @RequestParam(value = "query",required = false) String query,
                             @RequestParam(value = "pagenum",required = false) Integer pagenum,
                             @RequestParam(value = "pagesize",required = false) Integer pagesize) {
        List listUserid = new ArrayList();
        if (userid != null && userid !="") {
            System.out.println("llllll");
            String[] strArr = userid.split(",");
            for (int i = 0; i < strArr.length; ++i) {
                listUserid.add(strArr[i] + "");
            }
        }
        System.out.println("测试"+listUserid);
        System.out.println(listUserid.isEmpty());
        if (filetype == null){
            filetype ="";
        }
        if (query == null){
            query ="";
        }
        if (pagenum == null){
            pagenum =1;
        }
        if (pagesize == null){
            pagesize =10;
        }
        System.out.println("哈哈"+userid);
        // 获取文件列表
        List userFiles = this.userFileService.queryFiles(listUserid,filetype,query,pagenum,pagesize);
        // 获取文件列表个数
        Long total = this.userFileService.userFileDao().getTotal(listUserid,filetype,query);

        Map<String,Object> map = new HashMap<>();
        map.put("file", userFiles);
        map.put("total",total);
        return new Result(ResultCode.SUCCESS, map);
    }

    // 获取单个文件信息
    @GetMapping("/getfileone/{id}")
    public Result getFileOne(@PathVariable("id") String id) {
        if (id == null || id == "") {
            throw new CommonException("获取文件失败");
        }
        UserFile userFile = this.userFileService.userFileDao().queryById(id);
        User user = this.userDao.queryById(userFile.getUserid());
        Map<String,Object> map = new HashMap<>();
        map.put("id",userFile.getId());
        map.put("filename",userFile.getFilename());
        map.put("userid",userFile.getUserid());
        map.put("username",user.getAccount());
        map.put("downloadnum",userFile.getDownloadnum());
        return new Result(ResultCode.SUCCESS,map);
    }

    // 修改文件
    @PostMapping("/admin/edit")
    public Result editFile (UserFile userFile) {
        if (userFile.getId() == null || userFile.getId() == "") {
            throw new CommonException("修改文件失败");
        }
        UserFile editFile = this.userFileService.userFileDao().queryById(userFile.getId());
        if (userFile.getFilename() != null){
            editFile.setFilename(userFile.getFilename());
        }
        if (userFile.getDownloadnum() != null) {
            editFile.setDownloadnum(userFile.getDownloadnum());
        }
        this.userFileService.userFileDao().save(editFile);
        return new Result(ResultCode.SUCCESS);
    }

    // 文件审核
    @PostMapping("/reviewfile")
    public Result reviewFile(@RequestParam("id") String id,@RequestParam("filestatus") String filestatus) {
        if (id == null || id == "" ){
            throw new CommonException("文件id不能为空,文件审核操作失败！！！");
        }
        if (filestatus == null || filestatus == null || filestatus == "" || (!"enable".equals(filestatus) && !"disable".equals(filestatus))) {
            throw new CommonException("文件状态异常，文件审核操作失败！！！");
        }
        System.out.println("000000"+filestatus);
        String[] strArr = id.split(",");
        for (int i = 0; i < strArr.length; ++i) {
            UserFile userFile = userFileService.userFileDao().queryById(strArr[i]);
            if (userFile == null){
                throw new CommonException("文件不存在，删除失败");
            }
        }
        Date data = new Date();
        for (int i = 0; i < strArr.length; ++i) {
            UserFile userFile = userFileService.userFileDao().queryById(strArr[i]);
            userFile.setFilestatus(filestatus);
            userFile.setLastmodifytime(data);
            this.userFileService.userFileDao().save(userFile);
        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 上传文件
     */
    // 文件校验hash

    // HDFS创建目录测试
    @PostMapping("/test11")
    public Result uploadFile(String directoryid, MultipartFile file) throws Exception {
        System.out.println(directoryid);
        System.out.println(file);
        if (file.isEmpty()) {
            throw new CommonException("文件为空，上传失败");
        }
        // 获取当前用户
        User user = this.userUtil.getUser();

        System.out.println("==========="+directoryid+"  文件名:"+file.getOriginalFilename()+" 文件大小:"+file.getSize());
        this.userFileService.uploadFile(user,file);
//        this.hdfsServiceImp.upload("/"+file);
        return new Result(ResultCode.SUCCESS);
    }
}

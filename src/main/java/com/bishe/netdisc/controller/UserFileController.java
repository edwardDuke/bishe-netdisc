package com.bishe.netdisc.controller;

import com.bishe.netdisc.common.entity.Result;
import com.bishe.netdisc.common.entity.ResultCode;
import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.common.utils.UserUtil;
import com.bishe.netdisc.entity.Directory;
import com.bishe.netdisc.entity.UserFile;
import com.bishe.netdisc.service.DirectoryService;
import com.bishe.netdisc.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    private UserFileService userFileService;
    @Autowired
    private DirectoryService directoryService;


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
        UserFile userFile = userFileService.userFileDao().queryById(id);
        if (userFile == null){
            throw new CommonException("删除失败");
        }
        userFileService.userFileDao().deleteFileByid(id);
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

}

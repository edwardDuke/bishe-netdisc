package com.bishe.netdisc.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bishe.netdisc.common.entity.Result;
import com.bishe.netdisc.common.entity.ResultCode;
import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.common.utils.UserUtil;
import com.bishe.netdisc.entity.Directory;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.entity.UserFile;
import com.bishe.netdisc.entity.common.dire.MoveAndCopy;
import com.bishe.netdisc.service.DirectoryService;
import com.bishe.netdisc.service.UserFileService;
import javassist.bytecode.analysis.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author third_e
 * @create 2020/4/17 0017-下午 5:37
 */
@RestController
@RequestMapping("/dircetory")
public class DirectoryController {
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private DirectoryService directoryService;
    @Autowired
    private UserFileService userFileService;

    // 新建目录
    @PostMapping("/mkdir")
    public Result mkdir (@RequestParam("name") String name, @RequestParam("pid") String pid){
        if (name == null || name == "" || pid == null || pid == ""){
            throw new CommonException("创建文件失败");
        }
        Directory praDirectory = this.directoryService.directoryDao().queryById(pid);
        // 获取当前用户id
        System.out.println(praDirectory);
        String userId = userUtil.getUserId();
        if (praDirectory == null) {
            pid = this.directoryService.directoryDao().getDircByPid("0",userId).getId();
        }
        Directory mkdirDirectory = new Directory();
        mkdirDirectory.setDirectoryname(name);
        mkdirDirectory.setPid(pid);
        mkdirDirectory.setUserid(userId);
        mkdirDirectory.setCreatetime(new Date());
        mkdirDirectory.setLastmodifytime(new Date());
        // 保存
        directoryService.directoryDao().save(mkdirDirectory);
        return new Result(200,"创建成功",true);
    }

    /**
     * 查询某路径目录和文件
     * 包含两部分，一是获取目录，二是获取文件
     */

    @GetMapping("getdirc")
    public Result getDirc (@RequestParam(value = "pid", required = false) String pid) {
        System.out.println("查询某路径目录和文件"+pid);
        // 获取当前用户id
        String userId = userUtil.getUserId();
        if (pid == null || pid == ""){
            pid = directoryService.directoryDao().getDircByPid("0",userId).getId();
            System.out.println(pid);
        }
        // 是否存在目录
        Directory directory = directoryService.directoryDao().queryById(pid);
        if (directory== null){
            throw new CommonException("该目录不存在");
        }
        Map<String,Object> map = new HashMap<>();
        // 通过父目录id获取目录
        List directories = directoryService.getListDircByPid(pid,userId);
        // 通过父目录id获取文件
        List userFiles = userFileService.getListFileByPid(pid,userId);
        // 通过父目录id查找上一级目录
        List parDirectories = new ArrayList();
        parDirectories  = directoryService.getListParDircByid(pid,userId,parDirectories);
        Collections.reverse(parDirectories);
        map.put("id",pid);
        map.put("dirc", directories);
        map.put("file",userFiles);
        map.put("allPath",parDirectories);
        return new Result(ResultCode.SUCCESS,map);
    }

    // 删除某个目录
    @GetMapping("/deletedirc")
    public Result delete (@RequestParam("sourceListId") String sourceListId) {
        String userId = userUtil.getUserId();
        List listAll = commont(sourceListId);
        // 获取目录
        List listDire = (List) listAll.get(0);
        // 获取文件
        List listFile = (List) listAll.get(1);
        for (Object id:listDire) {
            System.out.println("删除目录id为==="+id);
            directoryService.deleteById((String) id, userId);
        }
        for (Object id:listFile) {
            System.out.println("删除文件id为==="+id);
            userFileService.deleteById((String) id);
        }

//        if (id == null || id == ""){
//            throw new CommonException("删除失败");
//        }
//        Directory directory = directoryService.directoryDao().queryById(id);
//        if (directory == null ){
//            throw new CommonException("删除失败");
//        }
//        String userId = userUtil.getUserId();
//        this.directoryService.deleteById(id,userId);
        return new Result(ResultCode.SUCCESS);
    }

    // 目录重命名
    @PostMapping("/renamedirc")
    public Result rename (@RequestParam("id") String id ,@RequestParam("name") String name){

        if (id == null || id == "" || name == null || name == ""){
            throw new CommonException("目录重命名失败");
        }
        // 获取当前目录
        Directory directory = directoryService.directoryDao().queryById(id);
        if (directory == null) {
            throw new CommonException("目录不存在");
        }
        // 新修改的目录
        Directory nowDirectory = new Directory();
        nowDirectory.setDirectoryname(name);
        nowDirectory.setLastmodifytime(new Date());
        directoryService.directoryDao().updateFirst(directory,nowDirectory);
        return new Result(ResultCode.SUCCESS);
    }

    // 目录复制到
    @GetMapping("copydirc")
    public Result copy (@RequestParam("sourceListId") String sourceListId,@RequestParam("targetId") String targetId) {

        List listAll = commont(sourceListId);
        // 获取目录
        List listDire = (List) listAll.get(0);
        // 获取文件
        List listFile = (List) listAll.get(1);
        for (Object id:listDire) {
            System.out.println("目录id为==="+id+"===复制到的目录id为"+targetId);
            directoryService.copy((String) id,targetId);
        }
        for (Object id:listFile) {
            System.out.println("文件id为==="+id+"===复制到的目录id为"+targetId);
            userFileService.copy((String) id,targetId);
        }
        return new Result(ResultCode.SUCCESS);
    }

    // 目录移动到
    @GetMapping("/movedirc")
    public Result move (@RequestParam("sourceListId") String sourceListId,@RequestParam("targetId") String targetId) {
        Directory targetDirectory = directoryService.directoryDao().queryById(targetId);
        if (targetDirectory == null){
            throw new CommonException("目标目录不存在");
        }
        // 获取到批量处理的目录和文件
        List listAll = commont(sourceListId);
        // 获取目录
        List listDire = (List) listAll.get(0);
        // 获取文件
        List listFile = (List) listAll.get(1);
        // 判断目标目录是否为源目录的子目录
        String checkid = targetId;
        while (true) {
            Directory directory = directoryService.directoryDao().queryById(checkid);
            for (Object id:listDire) {
                if (directory.getId().equals((String)id)){
                    Directory erroDire = directoryService.directoryDao().queryById((String)id);
                    throw new CommonException("目录"+erroDire.getDirectoryname()+"不能移动相同目录下");
                }
                if (directory.getPid().equals((String)id)) {
                    Directory erroDire = directoryService.directoryDao().queryById((String)id);
                    throw new CommonException("目录"+erroDire.getDirectoryname()+"不能移动到其子目录下");
                }
            }
            if ("0".equals(directory.getPid())){
                break;
            }
            checkid = directory.getPid();
        }

        for (Object id:listDire) {
            System.out.println("目录id为==="+id+"===移动到的目录id为"+targetId);
            directoryService.move((String) id,targetId);
        }
        for (Object id:listFile) {
            userFileService.move((String) id,targetId);
        }

//        if (sourceId == null || sourceId == "" || targetId == null || targetId == ""){
//            throw new CommonException("移动失败");
//        }
//        // 判断源目录、目标目录是否存在
//        Directory sourceDirectory = directoryService.directoryDao().queryById(sourceId);
//        Directory targetDirectory = directoryService.directoryDao().queryById(targetId);
//        if (sourceDirectory == null || targetDirectory == null){
//            throw new CommonException("源目录/目标目录不存在");
//        }
//        directoryService.move(sourceId,targetId);
        return new Result(ResultCode.SUCCESS);
    }

    // 获取所有目录和对应的子目录
    @GetMapping("/listdirc")
    public Result dircList () {

        String userId = userUtil.getUserId();
        // 从根目录开始获取
        Directory directory = directoryService.directoryDao().getDircByPid("0",userId);

        List list = new ArrayList();
        list = directoryService.dirceList(directory.getId(),list);
        System.out.println(list);
        Map<String,Object> map = new HashMap<>();
        map.put("id",directory.getId());
        map.put("name","全部文件");
        map.put("children",list);
        List allList = new ArrayList();
        allList.add(map);
        return new Result(ResultCode.SUCCESS,allList);
    }


    // 通过名字搜索目录或文件
    @GetMapping("/find")
    public Result findByName (@RequestParam("name") String name) {
        if (name == "" || name == null) {
            throw new CommonException("操作失败");
        }
        // 获取当前用户id
        String userId = userUtil.getUserId();
        // 模糊查询目录含有name的字段
        List allDire = this.directoryService.findDireByName(name, userId);
        // 模糊查询文件含有name的字段
        List allFile = this.userFileService.findFileByName(name, userId);
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
        map.put("dirc", allDire);
        map.put("file",allFile);
        map.put("allPath",parDirectories);
        return new Result(ResultCode.SUCCESS,map);
    }



    //测试传入参数数列表
    @GetMapping("/test")
    public Result test (){
        String dateString = "2019-04-30 15:59:10";
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = null;
//        try {
//            date = format.parse(dateString);
//            System.out.println(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        UserFile user = userFileService.userFileDao().queryById("5e79b8b8e24600004600756f");
        System.out.println(user);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = null;
        try {
            date = format.format(user.getLastmodifytime());
        } catch (Exception e) {

        }
        return new Result(200,date+"",true);
    }

    // 公共部分
    public List commont(String sourceListId) {
        sourceListId = sourceListId.replace("[","").replace("]","").replace("\",","\";");
        List<String> lis = Arrays.asList(sourceListId.split(","));
        // 装目录和文件
        List listAll = new ArrayList();
        // 装目录
        List listDir = new ArrayList();
        // 装文件
        List listFile = new ArrayList();
        for (String string : lis) {
            System.out.println(string);
            string = string.replace("\";","\",");
            JSONObject jsonObject = JSONArray.parseObject(string);
            System.out.println(jsonObject.getString("id"));
            if ("dir".equals(jsonObject.getString("type"))){
                Directory directory = directoryService.directoryDao().queryById(jsonObject.getString("id"));
                if (directory == null){
                    throw new CommonException("目录不存在，操作失败");
                }
                listDir.add(jsonObject.getString("id"));
            }else {
                UserFile userFile = userFileService.userFileDao().queryById(jsonObject.getString("id"));
                if (userFile == null){
                    throw new CommonException("文件不存在，操作失败");
                }
                listFile.add(jsonObject.getString("id"));
            }
        }
        listAll.add(listDir);
        listAll.add(listFile);
        return listAll;
    }
}

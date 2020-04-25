package com.bishe.netdisc.service;

import com.bishe.netdisc.common.utils.common.DateUtil;
import com.bishe.netdisc.entity.Directory;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.entity.UserFile;
import com.bishe.netdisc.mapper.DirectoryDao;
import com.bishe.netdisc.mapper.UserFileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author third_e
 * @create 2020/4/18 0018-下午 5:51
 */
@Service
public class UserFileService {
    @Autowired
    private UserFileDao userFileDao;
    @Autowired
    private UserService userService;
    @Autowired
    private DirectoryDao directoryDao;

    public UserFileDao userFileDao(){
        return this.userFileDao;
    }

    // 查询某目录下的文件
    public List<Object> getListFileByPid (String pid, String userid) {

        List files = new ArrayList();
        List<UserFile> userFiles = this.userFileDao.getListFileByPid(pid,userid);
        for (UserFile userFile:userFiles) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",userFile.getId());
            map.put("name", userFile.getFilename());
            map.put("type", userFile.getType());
            map.put("size",userFile.getFilesize());
            map.put("lastmodifytime", DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm",userFile.getLastmodifytime()));
            files.add(map);
        }
        return files;
    }

    //复制文件
    public void copy(String sourceId, String targetId){
        // 获取当前时间
        Date date = new Date();
        // 获取当前文件信息
        UserFile userFile = this.userFileDao.queryById(sourceId);
        // 复制文件
        UserFile newUserFile = userFile;
        if (userFile.getDirectoryid().equals(targetId)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            newUserFile.setFilename(newUserFile.getFilename()+df.format(date));
        }
        newUserFile.setId(null);
        newUserFile.setDirectoryid(targetId);
        newUserFile.setCreatetime(date);
        newUserFile.setLastmodifytime(date);
        this.userService.updataStorage(userFile.getUserid(),userFile.getFilesize(),"copy");
        // 保存文件
        this.userFileDao.save(newUserFile);
    }

    // 移动文件
    public void move (String sourceId, String targetId ){
        // 获取文件信息
        UserFile userFile = this.userFileDao.queryById(sourceId);
        // 修改文件信息
        userFile.setDirectoryid(targetId);
        userFile.setLastmodifytime(new Date());

        // 保存文件
        this.userFileDao.save(userFile);
    }

    // 删除文件
    public void deleteById(String id) {
        UserFile userFile = this.userFileDao.queryById(id);
        this.userService.updataStorage(userFile.getUserid(),userFile.getFilesize(),"copy");
        this.userFileDao.deleteFileByid(id);
    }

    // 通过名字搜索文件
    public List findFileByName (String name, String userid) {
        List<UserFile> userFileList = this.userFileDao.getListFindByName(name,userid);
        List allList = new ArrayList();
        for (UserFile userFile:userFileList) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",userFile.getId());
            map.put("name", userFile.getFilename());
            map.put("type", userFile.getType());
            map.put("size",userFile.getFilesize());
            map.put("lastmodifytime", DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm",userFile.getLastmodifytime()));

            if ("0".equals(userFile.getDirectoryid())){
                Map pramap = new HashMap();
                pramap.put("id","0");
                pramap.put("name","全部文件");
                map.put("parent", pramap);
            } else {
                // 查询父目录
                Directory directory = this.directoryDao.queryById(userFile.getDirectoryid());
                Map pramap = new HashMap();
                pramap.put("id",directory.getId());
                if ("0".equals(directory.getPid())){
                    pramap.put("name","全部文件");
                } else {
                    pramap.put("name",directory.getDirectoryname());
                }
                map.put("parent", pramap);
            }
            allList.add(map);
        }
        return allList;
    }

    public List getFileByType (String type, String userId) {
        // 获取文件
        List<UserFile> userFiles = this.userFileDao.getListFindByType(type,userId);
        List allList = new ArrayList();
        for ( UserFile userFile : userFiles ) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",userFile.getId());
            map.put("name", userFile.getFilename());
            map.put("type", userFile.getType());
            map.put("size",userFile.getFilesize());
            map.put("lastmodifytime", DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm",userFile.getLastmodifytime()));

            if ("0".equals(userFile.getDirectoryid())){
                Map pramap = new HashMap();
                pramap.put("id","0");
                pramap.put("name","全部文件");
                map.put("parent", pramap);
            } else {
                // 查询父目录
                Directory directory = this.directoryDao.queryById(userFile.getDirectoryid());
                Map pramap = new HashMap();
                pramap.put("id",directory.getId());
                if ("0".equals(directory.getPid())){
                    pramap.put("name","全部文件");
                } else {
                    pramap.put("name",directory.getDirectoryname());
                }
                map.put("parent", pramap);
            }
            allList.add(map);
        }

        return allList;
    }
}

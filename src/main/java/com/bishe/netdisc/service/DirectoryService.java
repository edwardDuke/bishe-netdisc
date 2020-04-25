package com.bishe.netdisc.service;

import com.bishe.netdisc.common.utils.common.DateUtil;
import com.bishe.netdisc.entity.Directory;
import com.bishe.netdisc.entity.UserFile;
import com.bishe.netdisc.mapper.DirectoryDao;
import com.bishe.netdisc.mapper.UserDao;
import com.bishe.netdisc.mapper.UserFileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author third_e
 * @create 2020/4/17 0017-下午 5:35
 */
@Service
public class DirectoryService {

    @Autowired
    private DirectoryDao directoryDao;
    @Autowired
    private UserFileDao userFileDao;
    @Autowired
    private UserService userService;

    public DirectoryDao directoryDao(){
        return this.directoryDao;
    }

    // 查询上一级目录
    public List<Object> getListParDircByid (String id, String userid,List list) {
        Directory directorie = this.directoryDao.getListParDircByid(id,userid);
        System.out.println("============="+directorie);
        Map<String,Object> map = new HashMap<>();
        map.put("id",directorie.getId());
        if (directorie.getPid().equals("0")) {
            map.put("name", "全部文件");
            list.add(map);
//            getListParDircByid(directorie.getPid(), userid, list);
        }else {
            map.put("name", directorie.getDirectoryname());
            list.add(map);
            getListParDircByid(directorie.getPid(), userid, list);
        }
        return list;
    }

    // 查询某目录
    public List<Object> getListDircByPid (String pid, String userid) {

        List dircs = new ArrayList();
        List<Directory> directories = this.directoryDao.getListDircByPid(pid,userid);
        for (Directory directory:directories){
            Map<String,Object> map = new HashMap<>();
            map.put("id",directory.getId());
            map.put("name",directory.getDirectoryname());
            map.put("type","dir");
            map.put("size","-");
            map.put("lastmodifytime",DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm",directory.getLastmodifytime()));
            dircs.add(map);
        }
        return dircs;
    }

    /**
     * id删除目录删除
     *
     * @param id
     * @param userid
     */
    public void deleteById (String id, String userid ) {
        // 递归删除当前目录下所有子目录和对应目录下子目录的所有文件
        // 1、获取当前目录
//        Directory directory = this.directoryDao.queryById(id);
        //获取当前目录下的文件
        // 2、获取下一级子目录和下一级文件
        List<Directory> listDirc = this.directoryDao.getListDircByPid(id,userid);
        List<UserFile> userFiles = this.userFileDao.getListFileByPid(id,userid);
        // 3、存在子目录则遍历子目录
        for (Directory list:listDirc) {
            deleteById(list.getId(),userid);
        }
        // 4、删除当前目录和文件
        this.directoryDao.deleteById(id);
        //这里删除文件
        for (UserFile userFile: userFiles){
            // 修改存储数据大小
            this.userService.updataStorage(userFile.getUserid(),userFile.getFilesize(),"delete");
            // 删除文件信息
            this.userFileDao.deleteFileByid(userFile.getId());
//            this.userFileDao.deleteById(userFile.getId());
        }
    }


    /**
     * 目录复制到
     *
     * @param sourceId 源目录id
     * @param targetId 目标目录id
     */
    public void copy (String sourceId, String targetId){

        Directory sourceDirectory = directoryDao.queryById(sourceId);
        // 复制当前目录信息
        Directory newDirectory = sourceDirectory;
        if (newDirectory.getPid().equals(targetId)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            newDirectory.setDirectoryname(newDirectory.getDirectoryname()+df.format(new Date()));
        }
        newDirectory.setId(null);
        newDirectory.setPid(targetId);
        newDirectory.setCreatetime(new Date());
        newDirectory.setLastmodifytime(new Date());
        // 返回新复制好的目录信息
        newDirectory = this.directoryDao.insert(newDirectory);
        // 查询需要复制的子目录和子目录下的文件
        List<Directory> directories = this.directoryDao.getListDircByPid(sourceId);
        List<UserFile> userFiles = this.userFileDao.getListFileByPid(sourceId);
        // 存在子目录则进行复制
        for (Directory directory : directories) {
            System.out.println("存在子目录："+directory.getDirectoryname());
            copy(directory.getId(), newDirectory.getId());
        }
        // 存在子目录下的文件则复制文件
        for (UserFile userFile: userFiles){
            this.userService.updataStorage(userFile.getUserid(),userFile.getFilesize(),"copy");
            userFile.setId(null);
            userFile.setDirectoryid(newDirectory.getId());
            userFile.setCreatetime(new Date());
            userFile.setLastmodifytime(new Date());
            this.userFileDao.save(userFile);
        }

    }

    /**
     *目录移动到
     *
     * @param sourceId 源目录id
     * @param targetId 目标目录id
     */
    public void move(String sourceId, String targetId) {
        // 获取当前时间
        Date date = new Date();
        // 获取源目录信息
        Directory sourceDirectory = directoryDao.queryById(sourceId);
       // 修改需要移动到的目标目录，并添加修改时间
        sourceDirectory.setPid(targetId);
        sourceDirectory.setLastmodifytime(date);
        // 获取子目录和子目录文件
        List<Directory> directories = this.directoryDao.getListDircByPid(sourceId);
        List<UserFile> userFiles = this.userFileDao.getListFileByPid(sourceId);
        //修改子文件信息
        for (UserFile userFile:userFiles) {
            userFile.setLastmodifytime(date);
            this.userFileDao.save(userFile);
        }
        // 遍历子目录
        for (Directory directory : directories) {
            move(directory.getId(),sourceDirectory.getId());
        }

        // 保存修改信息
        this.directoryDao.save(sourceDirectory);
    }

    /**
     * 获取全部目录及其子目录
     *
     * @param pid 父目录id
     * @param listdir 返回值
     * @return
     */
    public List dirceList (String pid, List listdir ){

        // 查询子目录
        List<Directory> listsChildDire = this.directoryDao.getListDircByPid(pid);
        for (Directory list : listsChildDire) {
            Map<String,Object> m1 = new HashMap<>();
            m1.put("id", list.getId());
            m1.put("name", list.getDirectoryname());
            // 子目录也是一个列表
            List test = new ArrayList();
            test = dirceList(list.getId(),test);
            m1.put("children",test);
            listdir.add(m1);
        }
        return listdir;
    }


    public List findDireByName (String name, String userid) {
        List<Directory> directoryList = this.directoryDao.getListFindByName(name, userid);
        List allList = new ArrayList();
        for (Directory directory: directoryList) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",directory.getId());
            map.put("name",directory.getDirectoryname());
            map.put("type","dir");
            map.put("size","-");
            map.put("lastmodifytime",DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm",directory.getLastmodifytime()));
            if ("0".equals(directory.getPid())){
                Map pramap = new HashMap();
                pramap.put("id","0");
                pramap.put("name","全部文件");
                map.put("parent", pramap);
            } else {
                // 查询父目录
                Directory praDirec = this.directoryDao.queryById(directory.getPid());
                Map pramap = new HashMap();
                pramap.put("id",praDirec.getId());
                if ("0".equals(praDirec.getPid())){
                    pramap.put("name","全部文件");
                } else {
                    pramap.put("name",praDirec.getDirectoryname());
                }
                map.put("parent", pramap);
            }
            allList.add(map);
        }
        return allList;
    }


}

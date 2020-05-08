package com.bishe.netdisc.service;

import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.common.utils.SaltUtil;
import com.bishe.netdisc.common.utils.common.DateUtil;
import com.bishe.netdisc.entity.Directory;
import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.entity.common.UserForm;
import com.bishe.netdisc.mapper.RoleDao;
import com.bishe.netdisc.mapper.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;


/**
 * @author third_e
 * @create 2020/4/10 0010-下午 10:06
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private RoleDao roleDao;

    public UserDao userDao() {
        return this.userDao;
    }

    // 注册用户
    public void save(User user) {
        Date date = new Date();
        this.userDao.save(user);
        //关联操作补充
        System.out.println("========="+user);
        String id =  user.getId();
        // 创建根目录
        Directory rootDirectory = new Directory();
        rootDirectory.setDirectoryname(user.getAccount());
        rootDirectory.setPid("0");
        rootDirectory.setUserid(id);
        rootDirectory.setCreatetime(date);
        rootDirectory.setLastmodifytime(date);
        directoryService.directoryDao().save(rootDirectory);

    }

    // 通过用户账号获取用户信息
    public User findByAccount(String account){
//        Query query = new Query(Criteria.where("account").is(account));
//        return mongoTemplate.findOne(query, User.class);
        User user = userDao.findByAccount(account);
        return user;
    }

    // 修改用户信息
    public void editUser(User oldUser, User nowUser) {

        if (nowUser.getId() != null) {
            nowUser.setId(oldUser.getId());
        }
        if (nowUser.getAccount() != null){
            nowUser.setAccount(oldUser.getAccount());
        }
        if (nowUser.getSalt() != null) {
            nowUser.setSalt(oldUser.getSalt());
        }
        if (nowUser.getRoleid() != null) {
            nowUser.setRoleid(oldUser.getRoleid());
        }
        if (nowUser.getStatus() != null) {
            nowUser.setStatus(oldUser.getStatus());
        }
        if (nowUser.getCreatetime() != null){
            nowUser.setCreatetime(oldUser.getCreatetime());
        }
        // 修改密码
        if (nowUser.getPassword() != null){
            String password = SaltUtil.md5Encrypt(nowUser.getPassword(),oldUser.getSalt());
            nowUser.setPassword(password);
        }
        this.userDao.updateFirst(oldUser,nowUser);
    }

    // 修改存储大小
    public void updataStorage(String id, Double storage, String type) {
        User user = this.userDao.queryById(id);
        if ("delete".equals(type)) {
            System.out.println("删除：修改存储大小");
            System.out.println(user.getUsestoragesize());
            System.out.println(storage);
            if (user.getUsestoragesize() - storage <0) {
                user.setUsestoragesize(Double.valueOf(0));
            }else {
                user.setUsestoragesize(user.getUsestoragesize() - storage);
            }
        }
        if ("copy".equals(type)) {
            System.out.println("复制：修改存储大小");
            user.setUsestoragesize(user.getUsestoragesize() + storage);
        }
        this.save(user);
    }

    // 获取角色对应的所有用户
    public List getAllUsers(String roleid) {
        // 获取对应用户信息
        List<User> users = this.userDao.findByRoleid(roleid);
        List allUser = new ArrayList();
        for (User user:users) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",user.getId());
            map.put("name",user.getAccount());
            allUser.add(map);
        }
       return allUser;
    }

    // 获取所有用户
    public List queryUsers(String query, Integer pagenum, Integer pagesize) {
        List all = new ArrayList();
        // 获取所有用户信息
        List<User> users = this.userDao.queryUsers(query, pagenum, pagesize);
        for (User user:users) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",user.getId());
            map.put("account",user.getAccount());
            map.put("name",user.getName());
            map.put("sex",user.getSex());
            map.put("status",user.getStatus());
            map.put("roleid",user.getRoleid());
            map.put("usestoragesize",user.getUsestoragesize());
            map.put("createtime", DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm",user.getCreatetime()));
            // 获取角色
            Role role = this.roleDao.queryById(user.getRoleid());
            map.put("rolename",role.getName());
            all.add(map);
        }
        return  all;
    }

    // 查询用户
    public Map<String,Object> adminGetUserById(String id) {
        // 获取用户
        User user = this.userDao.queryById(id);
        Map<String,Object> map = new HashMap<>();
        map.put("id",user.getId());
        map.put("account",user.getAccount());
        map.put("name",user.getName());
        map.put("sex",user.getSex());
        map.put("status",user.getStatus());
        map.put("roleid",user.getRoleid());
        map.put("createtime",DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm",user.getCreatetime()));
        return map;
    }

    // 删除用户
    public void delete (String id ) {
        // 查询用户
        User user = this.userDao.queryById(id);
        user.setStatus("delete");
        this.userDao.save(user);
    }

}

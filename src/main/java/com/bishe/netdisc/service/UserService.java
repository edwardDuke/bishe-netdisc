package com.bishe.netdisc.service;

import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.common.utils.SaltUtil;
import com.bishe.netdisc.entity.Directory;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.entity.common.UserForm;
import com.bishe.netdisc.mapper.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Date;


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

    public UserDao userDao() {
        return this.userDao;
    }

    // 注册用户
    public void save(User user) {
        Date date = new Date();
        this.userDao.save(user);
        //关联操作补充
        String id =  findByAccount(user.getAccount()).getId();
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
            user.setUsestoragesize(user.getUsestoragesize() - storage);
        }
        if ("copy".equals(type)) {
            user.setUsestoragesize(user.getUsestoragesize() + storage);
        }
        this.save(user);
    }
}

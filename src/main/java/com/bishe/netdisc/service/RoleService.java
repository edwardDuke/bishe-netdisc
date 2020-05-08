package com.bishe.netdisc.service;

import com.bishe.netdisc.common.utils.common.DateUtil;
import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.mapper.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author third_e
 * @create 2020/4/16 0016-下午 3:13
 */
@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;

    public RoleDao roleDao(){
        return this.roleDao;
    }

    // 通过角色名查找角色信息
    public Role findByname(String rolename){
        return this.roleDao.findByname(rolename);
    }

    // 查询所有角色

    public List allRoles() {
        List all = new ArrayList();
        List<Role> roles = this.roleDao.queryList(new Role());
        for (Role role:roles) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",role.getId());
            map.put("name",role.getName());
            map.put("description",role.getDescription());
            map.put("storagesize",role.getStoragesize());
            map.put("downloadspeed",role.getDownloadspeed());
            map.put("createtime", DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm",role.getCreatetime()));
            all.add(map);
        }
        return all;
    }

    public List queryRoles (String query, Integer pagenum, Integer pagesize) {

        // 查询所有角色
        List<Role> roles = this.roleDao.queryRoles(query,pagenum,pagesize);
        List all = new ArrayList();

        for (Role role:roles) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",role.getId());
            map.put("name",role.getName());
            map.put("description",role.getDescription());
            map.put("storagesize",role.getStoragesize());
            map.put("downloadspeed",role.getDownloadspeed());
            map.put("createtime",DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm",role.getCreatetime()));
            // 获取上一级角色
            Role pranRole = this.roleDao.queryById(role.getPid());
            // 如果不存在则是最高
            if (pranRole == null) {
                map.put("roleid","0");
                map.put("pranrole",null);
            }else {
                map.put("roleid",pranRole.getId());
                map.put("pranrole",pranRole.getName());
            }
            all.add(map);
        }
        return all;
    }

}

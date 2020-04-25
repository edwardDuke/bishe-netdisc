package com.bishe.netdisc.service;

import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.mapper.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}

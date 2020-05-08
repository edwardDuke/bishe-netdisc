package com.bishe.netdisc.controller;

import com.bishe.netdisc.common.entity.Result;
import com.bishe.netdisc.common.entity.ResultCode;
import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.common.utils.common.DateUtil;
import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author third_e
 * @create 2020/4/28 0028-下午 5:32
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // 获取所有角色
    @GetMapping("/all")
    public Result allRoles() {
        List all = this.roleService.allRoles();
        return new Result(ResultCode.SUCCESS,all);
    }

    // 查询角色列表
    @GetMapping("/admin/queryroles")
    public Result queryRoles(@RequestParam(value = "query", required = false) String query,
                             @RequestParam(value = "pagenum", required = false) Integer pagenum,
                             @RequestParam(value = "pagesize", required = false) Integer pagesize) {
        if (query == null){
            query ="";
        }
        if (pagenum == null){
            pagenum =1;
        }
        if (pagesize == null){
            pagesize =10;
        }
        // 查询角色列表
        List allRoles = this.roleService.queryRoles(query,pagenum,pagesize);
        // 查询总条数
        Long total = this.roleService.roleDao().getTotal(query);

        Map<String,Object> map = new HashMap<>();
        map.put("roles",allRoles);
        map.put("total",total);
        return new Result(ResultCode.SUCCESS, map);
    }

    // 添加角色
    @PostMapping("/admin/add")
    public Result add (Role role) {
        if (role.getName() == null || role.getName() == "") {
            throw new CommonException("角色名不能为空");
        }
        Role newrole = role;
        newrole.setCreatetime(new Date());
        this.roleService.roleDao().save(newrole);
        return new Result(ResultCode.SUCCESS);
    }

    // 获取单个角色
    @GetMapping("/admin/{id}")
    public Result getRoleById (@PathVariable("id") String id ) {
        // 查询角色
        Role role = this.roleService.roleDao().queryById(id);
        if (role == null) {
            throw new CommonException("角色不存在");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("id",role.getId());
        map.put("name",role.getName());
        map.put("description",role.getDescription());
        map.put("storagesize",role.getStoragesize());
        map.put("downloadspeed",role.getDownloadspeed());
        map.put("createtime", DateUtil.getDateByFormatString("yyyy-MM-dd HH:mm",role.getCreatetime()));
        // 获取上一级角色
        Role pranRole = this.roleService.roleDao().queryById(role.getPid());
        // 如果不存在则是最高
        if (pranRole == null) {
            map.put("roleid","0");
            map.put("pranrole",null);
        }else {
            map.put("roleid",pranRole.getId());
            map.put("pranrole",pranRole.getName());
        }
        return new Result(ResultCode.SUCCESS,map);
    }

    // 编辑角色
    @PostMapping("/admin/edit")
    public Result edit (Role role) {
        if (role.getId() == null || role.getId() == "") {
            throw new CommonException("角色id不能为空");
        }
        // 查询当前角色
        Role nowRole = this.roleService.roleDao().queryById(role.getId());

        if (role.getName() != null && role.getName() != "") {
            nowRole.setName(role.getName());
        }
        if (role.getDescription() != null && role.getDescription() != "") {
            nowRole.setDescription(role.getDescription());
        }
        if (role.getPid() != null && role.getPid() != "") {
            Role pranRole = this.roleService.roleDao().queryById(role.getPid());
            if (pranRole == null) {
                throw new CommonException("上一级角色不存在");
            }
            nowRole.setPid(role.getPid());
        }
        if (role.getStoragesize() != null ) {
            nowRole.setStoragesize(role.getStoragesize());
        }
        if (role.getDownloadspeed() != null) {
            nowRole.setDownloadspeed(role.getDownloadspeed());
        }
        this.roleService.roleDao().save(nowRole);
        return new Result(ResultCode.SUCCESS);
    }

    // 删除角色
    @GetMapping("/admin/delete/{id}")
    public Result delete (@PathVariable("id") String id ) {
        if (id == null || id == "") {
            throw new CommonException("删除失败");
        }
        // 查询角色
        Role role = this.roleService.roleDao().queryById(id);
        if (role == null) {
            throw new CommonException("删除的角色不存在");
        }
        this.roleService.roleDao().deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }

}

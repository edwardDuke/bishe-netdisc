package com.bishe.netdisc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户表
 *
 * @author third_e
 * @create 2020/4/10 0010-下午 4:38
 */
@Data
@Document(collection = "user")
public class User {
    @Id
    // 主键
    private String id;
    // 账号
    private String account;
    // 密码
    private String password;
    // 加密盐
    private String salt;
    // 用户名
    private String name;
    // 性别
    private String sex;
    // 角色id
    private String roleid;
    // 用户状态
    private String status;
    // 已用空间
    private Double usestoragesize;
    // 创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

}

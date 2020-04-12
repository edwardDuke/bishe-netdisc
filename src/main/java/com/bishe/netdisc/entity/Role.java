package com.bishe.netdisc.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 角色表
 *
 * @author third_e
 * @create 2020/4/11 0011-下午 4:26
 */
@Data
@Document(collection = "role")
public class Role {
    @Id
    // 角色主键
    private String id;
    // 角色名称
    private String name;
    // 角色描述
    private String description;
    // 上一级角色id
    private String pid;
    // 存储空间
    private Double storagesize;
    // 下载速度
    private Double downloadspeed;
    // 创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

}

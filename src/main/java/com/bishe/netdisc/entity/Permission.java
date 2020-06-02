package com.bishe.netdisc.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 权限表
 *
 * @author third_e
 * @create 2020/4/11 0011-下午 4:31
 */
@Data
@Document(collection = "permission")
public class Permission {
    @Id
    private String id;
    //权限名
    private String name;
    //权限代码
    private String percode;
    //上一级
    private String pid;
    // 创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
}

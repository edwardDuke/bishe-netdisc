package com.bishe.netdisc.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author third_e
 * @create 2020/4/11 0011-下午 4:38
 */
@Data
@Document(collection = "role_permission")
public class RolePermission {
    @Id
    private String id;
    // 角色id
    private String roleid;
    // 权限id
    private String permissionid;
    // 创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
}

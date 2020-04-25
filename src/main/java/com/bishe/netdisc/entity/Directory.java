package com.bishe.netdisc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 目录表
 *
 * @author third_e
 * @create 2020/4/17 0017-下午 5:21
 */
@Data
@Document(collection = "directory")
public class Directory {
    @Id
    private String id;
    // 目录名
    private String directoryname;
    // 父级目录
    private String pid;
    // 用户id
    private String userid;
    // 创建时间
    private Date createtime;
    // 最后修改时间
    private Date lastmodifytime;
}

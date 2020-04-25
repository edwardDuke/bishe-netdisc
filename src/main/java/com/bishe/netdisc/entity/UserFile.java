package com.bishe.netdisc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 文件表
 *
 * @author third_e
 * @create 2020/4/17 0017-下午 11:55
 */
@Data
@Document(collection = "userfile")
public class UserFile {
    @Id
    private String id;

    // 文件名
    private String filename;
    // 文件路径
    private String filepath;
    // 文件所在目录id
    private String directoryid;
    // 文件上传的时间
    private Date createtime;
    // 文件大小
    private Double filesize;
    // 文件状态
    private String filestatus;
    // hash值
    private String hash;
    // 文件类型
    private String type;
    // 用户id
    private String userid;
    // 最后一次修改时间
    private Date lastmodifytime;


}

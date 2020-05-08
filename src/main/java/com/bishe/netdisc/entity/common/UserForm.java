package com.bishe.netdisc.entity.common;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author third_e
 * @create 2020/4/17 0017-上午 11:29
 */
@Data
@Document(collection = "user")
public class UserForm {
    private String id;
    private String account;
    private String password;
    private String name;
    private String sex;
    private String roleid;
}

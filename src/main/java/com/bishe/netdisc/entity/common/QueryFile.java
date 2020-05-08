package com.bishe.netdisc.entity.common;

import lombok.Data;

/**
 * @author third_e
 * @create 2020/4/26 0026-上午 12:00
 */
@Data
public class QueryFile {
    private String userid;
    private String filetype;
    private String query;
    private Integer pagenum;
    private Integer pagesize;

}

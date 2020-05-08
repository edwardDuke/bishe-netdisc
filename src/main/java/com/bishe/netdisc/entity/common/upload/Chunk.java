package com.bishe.netdisc.entity.common.upload;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author third_e
 * @create 2020/5/4 0004-下午 2:25
 */
@Data
@Entity
public class Chunk implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    /**
     * 当前文件块，从1开始
     */
    @Column(nullable = false)
    private String chunkNumber;
    /**
     * 分块大小
     */
    @Column(nullable = false)
    private Long chunkSize;
    /**
     * 当前分块大小
     */
    @Column(nullable = false)
    private Long currentChunkSize;
    /**
     * 总大小
     */
    @Column(nullable = false)
    private Long totalSize;
    /**
     * 文件标识
     */
    @Column(nullable = false)
    private String identifier;
    /**
     * 文件名
     */
    @Column(nullable = false)
    private String filename;
    /**
     * 相对路径
     */
    @Column(nullable = false)
    private String relativePath;
    /**
     * 总块数
     */
    @Column(nullable = false)
    private Integer totalChunks;
    /**
     * 文件类型
     */
    @Column
    private String type;

    /**
     * 存储的目录id
     */
    private String dirId;

    @Transient
    private MultipartFile file;
}

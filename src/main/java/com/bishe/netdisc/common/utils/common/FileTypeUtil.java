package com.bishe.netdisc.common.utils.common;

import java.util.Arrays;

/**
 * @author third_e
 * @create 2020/5/5 0005-下午 9:44
 */
public class FileTypeUtil {
    // 图片类型
    public static final String[] PICTURE = {"bmp","jpg","png","tif","gif","pcx","tga","exif","fpx","svg","psd","cdr","pcd","dxf","ufo","eps","ai","raw","WMF","webp"};
    // 文档类型
    public static final String[] DOCUMENT = {"doc","docx","xls","xlsx","ppt","pptx","pdf","txt","tif","tiff","csv"};
    // 视频类型
    public static final String[] VIDEO = {"avi","mpg","mpeg","mov","rm","ram","mp4","rmvb","mkv","wmv","flv","swf"};
    // 音乐类型
    public static final String[] MUSIC = {"cda","wav","mp3","wma","ra","rma","mid","flac"};
    // 其他类型
    public static final String OTHER = "";

    public static String getFileTypeName(String type) {
        if (type == null){
            return "other";
        }
        if (Arrays.asList(PICTURE).contains(type)){
            return "picture";
        }else if (Arrays.asList(DOCUMENT).contains(type)) {
            return "document";
        }else if (Arrays.asList(VIDEO).contains(type)) {
            return "video";
        }else if (Arrays.asList(MUSIC).contains(type)) {
            return "music";
        }else {
            return "other";
        }
    }
}

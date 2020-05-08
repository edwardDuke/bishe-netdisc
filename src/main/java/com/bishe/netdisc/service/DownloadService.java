package com.bishe.netdisc.service;

import com.bishe.netdisc.common.utils.common.DateUtil;
import com.bishe.netdisc.entity.Directory;
import com.bishe.netdisc.entity.UserFile;
import com.bishe.netdisc.hdfs.HDFSService;
import com.bishe.netdisc.hdfs.HDFSServiceImp;
import com.bishe.netdisc.mapper.DirectoryDao;
import com.bishe.netdisc.mapper.UserFileDao;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author third_e
 * @create 2020/5/7 0007-下午 1:51
 */
@Service
public class DownloadService {

    @Autowired
    private UserFileDao userFileDao;
    @Autowired
    private DirectoryDao directoryDao;

    @Autowired
    private HDFSServiceImp hdfsServiceImp;

    public void downloadMore(List<Directory> directories, List<UserFile> userFiles, ZipOutputStream zos, String path ) throws Exception{
        // 1 目录haha1 文件空
        // 2 目录重命名 文件 bbb

       for (Directory directory:directories){
           String filepath = path + directory.getDirectoryname() +"/" ;
           System.out.println("当前目录"+filepath);
           ZipEntry entry = new ZipEntry(filepath);
           zos.putNextEntry(entry);
           List<Directory> sonDirectories = this.directoryDao.getListDircByPid(directory.getId());
           List<UserFile> sonUserFiles = this.userFileDao.getListFileByPid(directory.getId());
           if (sonDirectories.size() !=0 || sonUserFiles.size() !=0){
               downloadMore(sonDirectories,sonUserFiles,zos,filepath);
           }
       }
        FileSystem fs = this.hdfsServiceImp.getFileSystem();
        for (UserFile userFile: userFiles) {
            System.out.println("当前文件名为："+userFile.getFilename());
            InputStream in = fs.open(new Path(userFile.getFilepath()));
            String name;
            if (userFile.getType() != null){
                name =path+userFile.getFilename()+"."+userFile.getType();
            }else {
                name =path+userFile.getFilename();
            }
            byte[] buffer = new byte[1024];
            int len = 0;
            //创建zip实体（一个文件对应一个ZipEntry）  
            ZipEntry entry = new ZipEntry(name);
            zos.putNextEntry(entry);
            //文件流循环写入ZipOutputStream  
            while ((len = in.read(buffer)) != -1){
                zos.write(buffer, 0, len);
            }
            in.close();
            zos.closeEntry();
            userFile.setDownloadnum(userFile.getDownloadnum()+1);
            this.userFileDao.save(userFile);
        }

//        FileSystem fs = this.hdfsServiceImp.getFileSystem();

//        for (UserFile userFile: userFiles) {
//            InputStream in = fs.open(new Path(userFile.getFilepath()));
//            String name;
//            if (userFile.getType() != null){
//                name =userFile.getFilename()+"."+userFile.getType();
//            }else {
//                name =userFile.getFilename();
//            }
//            byte[] buffer = new byte[1024];
//            int len = 0;
//            //创建zip实体（一个文件对应一个ZipEntry）  
//            ZipEntry entry = new ZipEntry(name);
//            zos.putNextEntry(entry);
//            //文件流循环写入ZipOutputStream  
//            while ((len = in.read(buffer)) != -1){
//                zos.write(buffer, 0, len);
//            }
//            in.close();
//            zos.closeEntry();
//        }

//        for (FileStatus st : fileStatuses) {
//            System.out.println(st);
//            InputStream in = fs.open(st.getPath());
//            String name =st.getPath().getName();
//            byte[] buffer = new byte[1024];
//            int len = 0;
//            //创建zip实体（一个文件对应一个ZipEntry）  
//            ZipEntry entry = new ZipEntry(name);
//            zos.putNextEntry(entry);
//            //文件流循环写入ZipOutputStream  
//            while ((len = in.read(buffer)) != -1){
//                zos.write(buffer, 0, len);
//            }
//            in.close();
//            zos.closeEntry();
//        }

    }
}

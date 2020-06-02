package com.bishe.netdisc.mapper;

import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.entity.UserFile;
import com.bishe.netdisc.mapper.baseDao.MongoDbDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.regex.Pattern;


/**
 * @author third_e
 * @create 2020/4/18 0018-下午 5:34
 */
@Repository
public class UserFileDao extends MongoDbDao<UserFile> {
    @Override
    protected Class<UserFile> getEntityClass() {
        return UserFile.class;
    }

    @Autowired
    private MongoTemplate mongoTemplate;


    public UserFile getFileByPid (String pid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("directoryid").is(pid).and("filestatus").is("enable"));
        return this.mongoTemplate.findOne(query,UserFile.class);
    }

    public UserFile getFileByPid (String pid, String userid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("directoryid").is(pid).and("userid").is(userid).and("filestatus").is("enable"));
        return this.mongoTemplate.findOne(query,UserFile.class);
    }

    public List<UserFile> getListFileByPid (String pid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("directoryid").is(pid).and("filestatus").is("enable"));
        return this.mongoTemplate.find(query,UserFile.class);
    }

    public List<UserFile> getListFileByPid (String pid, String userid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("directoryid").is(pid).and("userid").is(userid).and("filestatus").is("enable"));
        query.with(Sort.by(Sort.Order.desc("createtime")));
        return this.mongoTemplate.find(query,UserFile.class);
    }

    // 删除单个文件
    public void deleteFileByid(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = Update.update("filestatus","delete");
        this.mongoTemplate.upsert(query, update, UserFile.class);
    }
    // 删除多个文件
    public void deleteFileByallId(List id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(id));
        Update update = Update.update("filestatus","delete");
        this.mongoTemplate.upsert(query, update, UserFile.class);
    }

    public List<UserFile> getListFindByName (String name, String userid) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        /**
         * 这里使用的正则表达式的方式
         * 第二个参数Pattern.CASE_INSENSITIVE是对字符大小写不明感匹配
         */
        Pattern pattern = Pattern.compile("^.*" + name + ".*$",Pattern.CASE_INSENSITIVE);
        query.addCriteria(criteria.and("filename").regex(pattern));
        query.addCriteria(criteria.and("userid").is(userid));
        query.addCriteria(criteria.and("filestatus").is("enable"));

        return this.mongoTemplate.find(query, UserFile.class);
    }

    public List<UserFile> getListFindByType (String type, String userId ) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria.and("typename").is(type));
        query.addCriteria(criteria.and("userid").is(userId));
        query.addCriteria(criteria.and("filestatus").is("enable"));
        query.with(Sort.by(Sort.Order.desc("lastmodifytime")));
        return this.mongoTemplate.find(query,UserFile.class);
    }
    public List<UserFile> queryFiles (List alluserid, String filetype, String queryName, Integer pagenum, Integer pagesize) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        System.out.println("a");
        System.out.println(alluserid.isEmpty());
        if (!alluserid.isEmpty()){
            query.addCriteria(criteria.and("userid").in(alluserid));
        }
        System.out.println("b");
        if (filetype !=""){
            query.addCriteria(criteria.and("typename").is(filetype));
        }
        /**
         * 这里使用的正则表达式的方式
         * 第二个参数Pattern.CASE_INSENSITIVE是对字符大小写不明感匹配
         */
        System.out.println("c");
        if (queryName != ""){
            Pattern pattern = Pattern.compile("^.*" + queryName + ".*$",Pattern.CASE_INSENSITIVE);
            query.addCriteria(criteria.and("filename").regex(pattern));
        }
        query.skip((pagenum-1)*pagesize);
        query.limit(pagesize);
        query.with(Sort.by(Sort.Order.desc("createtime")));
        query.addCriteria(criteria.and("filestatus").ne("delete"));
        return this.mongoTemplate.find(query,UserFile.class);
    }

    public Long getTotal (List alluserid, String filetype, String queryName) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (!alluserid.isEmpty()){
            query.addCriteria(criteria.and("userid").in(alluserid));
        }
        System.out.println("b");
        if (filetype !=""){
            query.addCriteria(criteria.and("typename").is(filetype));
        }
        /**
         * 这里使用的正则表达式的方式
         * 第二个参数Pattern.CASE_INSENSITIVE是对字符大小写不明感匹配
         */
        if (queryName != ""){
            Pattern pattern = Pattern.compile("^.*" + queryName + ".*$",Pattern.CASE_INSENSITIVE);
            query.addCriteria(criteria.and("filename").regex(pattern));
        }
        query.with(Sort.by(Sort.Order.desc("lastmodifytime")));
        query.addCriteria(criteria.and("filestatus").ne("delete"));
        return this.mongoTemplate.count(query,UserFile.class);
    }
}

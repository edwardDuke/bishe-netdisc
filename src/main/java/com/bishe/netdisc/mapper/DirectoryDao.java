package com.bishe.netdisc.mapper;

import com.bishe.netdisc.entity.Directory;
import com.bishe.netdisc.mapper.baseDao.MongoDbDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author third_e
 * @create 2020/4/17 0017-下午 5:20
 */
@Repository
public class DirectoryDao extends MongoDbDao<Directory> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected Class<Directory> getEntityClass() {
        return Directory.class;
    }

    public Directory getDircByPid(String pid) {
        Query query = new Query(Criteria.where("pid").in(pid));
        return this.mongoTemplate.findOne(query,Directory.class);
    }

    public Directory getDircByPid(String pid,String userid) {
        Query query = new Query(Criteria.where("pid").in(pid).and("userid").is(userid));
        return this.mongoTemplate.findOne(query,Directory.class);
    }

    public List<Directory> getListDircByPid(String pid) {
        Query query = new Query(Criteria.where("pid").in(pid));
        return this.mongoTemplate.find(query,Directory.class);
    }

    public List<Directory> getListDircByPid(String pid,String userid){
        Query query = new Query();
        Criteria criteria = Criteria.where("pid").is(pid).and("userid").is(userid);
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Order.desc("lastmodifytime")));
        return this.mongoTemplate.find(query,Directory.class);
    }

    public Directory getListParDircByid(String id,String userid){
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(id).and("userid").is(userid);
        query.addCriteria(criteria);
        return this.mongoTemplate.findOne(query,Directory.class);
    }

    public Directory insert(Directory directory) {
        this.mongoTemplate.insert(directory);
        return directory;
    }

    public List<Directory> getListFindByName (String name, String userid) {

        Query query = new Query();
        Criteria criteria = new Criteria();
        /**
         * 这里使用的正则表达式的方式
         * 第二个参数Pattern.CASE_INSENSITIVE是对字符大小写不明感匹配
         */
        Pattern pattern = Pattern.compile("^.*" + name + ".*$",Pattern.CASE_INSENSITIVE);
        query.addCriteria(criteria.and("directoryname").regex(pattern));
        query.addCriteria(criteria.and("userid").is(userid));
        query.addCriteria(criteria.and("pid").ne("0"));

        return this.mongoTemplate.find(query, Directory.class);
    }
}

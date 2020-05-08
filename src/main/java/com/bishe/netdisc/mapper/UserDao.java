package com.bishe.netdisc.mapper;

import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.mapper.baseDao.MongoDbDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author third_e
 * @create 2020/4/10 0010-下午 4:55
 */
@Repository
public class UserDao extends MongoDbDao<User> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected Class<User> getEntityClass(){
        return User.class;
    }

    // 通过用户账号获取用户信息
    public User findByAccount(String account){
        Query query = new Query(Criteria.where("account").is(account).and("status").ne("delete"));
        return mongoTemplate.findOne(query, this.getEntityClass());
    }

    // 存储大小修改
    public void updateStorage(String id, Double storage){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = Update.update("usestoragesize",storage);
        this.mongoTemplate.upsert(query, update, User.class);

    }
    // 通过角色id获取用户信息
    public List<User> findByRoleid(String roleid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roleid").is(roleid));
        return this.mongoTemplate.find(query,User.class);
    }

    public List<User> queryUsers(String queryName, Integer pagenum, Integer pagesize) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        /**
         * 这里使用的正则表达式的方式
         * 第二个参数Pattern.CASE_INSENSITIVE是对字符大小写不明感匹配
         */
        System.out.println("c");
        if (queryName != ""){
            Pattern pattern = Pattern.compile("^.*" + queryName + ".*$",Pattern.CASE_INSENSITIVE);
            Criteria account = Criteria.where("account").regex(pattern);
            Criteria name = Criteria.where("name").regex(pattern);
            query.addCriteria(criteria.orOperator(account, name));
//            query.addCriteria(criteria.and("account").regex(pattern));
        }
        query.skip((pagenum-1)*pagesize);
        query.limit(pagesize);
        query.with(Sort.by(Sort.Order.desc("createtime")));
        query.addCriteria(criteria.and("status").ne("delete"));
        return this.mongoTemplate.find(query,User.class);
    }


    public Long getTotal(String queryName) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        /**
         * 这里使用的正则表达式的方式
         * 第二个参数Pattern.CASE_INSENSITIVE是对字符大小写不明感匹配
         */
        if (queryName != ""){
            Pattern pattern = Pattern.compile("^.*" + queryName + ".*$",Pattern.CASE_INSENSITIVE);
            Criteria account = Criteria.where("account").regex(pattern);
            Criteria name = Criteria.where("name").regex(pattern);
            query.addCriteria(criteria.orOperator(account, name));
        }
        query.with(Sort.by(Sort.Order.desc("createtime")));
        query.addCriteria(criteria.and("status").ne("delete"));
        return this.mongoTemplate.count(query,User.class);
    }

}

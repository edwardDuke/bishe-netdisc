package com.bishe.netdisc.mapper;

import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.mapper.baseDao.MongoDbDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

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
        Query query = new Query(Criteria.where("account").is(account));
        return mongoTemplate.findOne(query, this.getEntityClass());
    }

    // 存储大小修改
    public void updateStorage(String id, Double storage){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = Update.update("usestoragesize",storage);
        this.mongoTemplate.upsert(query, update, User.class);

    }

}
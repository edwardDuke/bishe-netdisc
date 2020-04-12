package com.bishe.netdisc.service;

import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.mapper.userDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author third_e
 * @create 2020/4/10 0010-下午 10:06
 */
@Service
public class UserService {

    @Autowired
    private userDao userDao;

    public User findByAccount(String account){
//        Query query = new Query(Criteria.where("account").is(account));
//        return mongoTemplate.findOne(query, User.class);
        User user = userDao.findByAccount(account);
        return user;
    }
}

package com.bishe.netdisc.mapper;

import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.mapper.baseDao.MongoDbDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


/**
 * @author third_e
 * @create 2020/4/11 0011-下午 4:41
 */
@Repository
public class RoleDao extends MongoDbDao<Role> {
    @Override
    protected Class<Role> getEntityClass(){
        return Role.class;
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    // 通过角色名获取角色信息
    public Role findByname(String rolename){
        Query query = new Query(Criteria.where("name").is(rolename));
        return mongoTemplate.findOne(query, this.getEntityClass());
    }
}

package com.bishe.netdisc.mapper;

import com.bishe.netdisc.entity.Permission;
import com.bishe.netdisc.entity.RolePermission;
import com.bishe.netdisc.mapper.baseDao.MongoDbDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author third_e
 * @create 2020/5/31 0031-下午 2:39
 */
@Repository
public class PermissionDao extends MongoDbDao<Permission> {
    @Override
    protected Class<Permission> getEntityClass(){
        return Permission.class;
    }
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Permission> getPermissionBypid (String pid) {
        Query query = new Query(Criteria.where("pid").is(pid));
        return mongoTemplate.find(query, this.getEntityClass());
    }
}

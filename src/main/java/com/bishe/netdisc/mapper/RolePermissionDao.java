package com.bishe.netdisc.mapper;


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
 * @create 2020/5/31 0031-下午 2:37
 */
@Repository
public class RolePermissionDao extends MongoDbDao<RolePermission> {
    @Override
    protected Class<RolePermission> getEntityClass(){
        return RolePermission.class;
    }
    @Autowired
    private MongoTemplate mongoTemplate;


    public List<RolePermission> getPermissionByRoleId (String roleId) {
        Query query = new Query(Criteria.where("roleid").is(roleId));
        return mongoTemplate.find(query, this.getEntityClass());
    }

    public void deleteByRoleId (String roleId) {
        logger.info("-------------->MongoDB deleteById start");
        Query query = new Query(Criteria.where("roleid").is(roleId));
        mongoTemplate.findAllAndRemove(query,RolePermission.class);
    }
}

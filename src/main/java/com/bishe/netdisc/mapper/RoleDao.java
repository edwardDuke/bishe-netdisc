package com.bishe.netdisc.mapper;

import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.mapper.baseDao.MongoDbDao;
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
}

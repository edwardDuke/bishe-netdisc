package com.bishe.netdisc.mapper;

import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.mapper.baseDao.MongoDbDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;


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

    // 通过父id获取角色信息
    public List<Role> findBypid(String pid){
        Query query = new Query(Criteria.where("pid").is(pid));
        return mongoTemplate.find(query, this.getEntityClass());
    }
    public List<Role> queryRoles(String queryName, Integer pagenum, Integer pagesize) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        /**
         * 这里使用的正则表达式的方式
         * 第二个参数Pattern.CASE_INSENSITIVE是对字符大小写不明感匹配
         */
        if (queryName != ""){
            Pattern pattern = Pattern.compile("^.*" + queryName + ".*$",Pattern.CASE_INSENSITIVE);
            Criteria description = Criteria.where("description").regex(pattern);
            Criteria name = Criteria.where("name").regex(pattern);
            query.addCriteria(criteria.orOperator(description, name));
        }
        query.skip((pagenum-1)*pagesize);
        query.limit(pagesize);
        query.with(Sort.by(Sort.Order.desc("createtime")));
        return this.mongoTemplate.find(query,Role.class);
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
            Criteria description = Criteria.where("description").regex(pattern);
            Criteria name = Criteria.where("name").regex(pattern);
            query.addCriteria(criteria.orOperator(description, name));
        }
        return this.mongoTemplate.count(query,Role.class);
    }
}

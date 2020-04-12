package com.bishe.netdisc.service;

import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.mapper.userDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author third_e
 * @create 2020/4/10 0010-下午 4:57
 */
@Service
public class mongodbService {

    @Autowired
    private userDao userDao;

    public String test() {
        User user = userDao.queryById("5e79b1c6e246000046007565");
        System.out.println(user);
        return user.toString();
    }

}

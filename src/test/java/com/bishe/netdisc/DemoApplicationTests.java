package com.bishe.netdisc;

import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.mapper.UserDao;
import com.bishe.netdisc.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {

//        String dateString = "2019-04-30 15:59:10";
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = null;
//        try {
//            date = format.parse(dateString);
//            System.out.println(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

    }

}

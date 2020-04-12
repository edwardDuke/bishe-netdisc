package com.bishe.netdisc.common.utils;

import java.util.Random;

/**
 * 生成盐和加盐工具
 *
 * @author third_e
 * @create 2020/4/12 0012-上午 12:12
 */
public class SaltUtil {

    /**
     * 获取密码盐,长度为5
     *
     */
    public static String getRandomSalt() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < 5; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }

        return sb.toString();
    }

    /**
     * md5加密，带盐值
     *
     * @author fengshuonan
     * @Date 2019/7/20 17:36
     */
    public static String md5Encrypt(String password, String salt) {
        if (password.isEmpty() || salt.isEmpty()) {
            throw new IllegalArgumentException("密码或盐为空！");
        } else {
            return MD5Util.encrypt(password + salt);
        }
    }
}

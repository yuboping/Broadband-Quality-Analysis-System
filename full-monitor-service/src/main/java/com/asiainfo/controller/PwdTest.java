package com.asiainfo.controller;

import com.asiainfo.lcims.lcbmi.password.PasswordException;
import com.asiainfo.lcims.lcbmi.password.PwdDES3;
import com.asiainfo.lcims.lcbmi.password.PwdMD5;

public class PwdTest {
    public static void main(String[] args) {
        String password = "123456";
        System.out.println(encryptPwd(password, 5));
    }

    /**
     * 密码加密
     * 
     * @param password
     * @param passwordtype
     * @return
     */
    public static String encryptPwd(String password, int passwordtype) {
        String encryptpwd = "";
        try {
            if (passwordtype == 0) {
                encryptpwd = password;
            } else if (passwordtype == 2) {
                encryptpwd = new PwdDES3().encryptPassword(password);
            } else if (passwordtype == 5) {
                encryptpwd = new PwdMD5().encryptPassword(password);
            }
        } catch (PasswordException e) {
            encryptpwd = password;
        }
        return encryptpwd;
    }
}

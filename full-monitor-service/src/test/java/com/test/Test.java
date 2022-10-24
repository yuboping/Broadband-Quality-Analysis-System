package com.test;

import com.asiainfo.lcims.lcbmi.password.PasswordException;
import com.asiainfo.lcims.lcbmi.password.PwdDES3;

public class Test {
    public static void main(String[] args) throws PasswordException {
        PwdDES3 desc = new PwdDES3();
        System.out.println(desc.encryptPassword("123456"));
    }
}

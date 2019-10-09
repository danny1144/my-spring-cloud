package com.siemens.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import java.io.IOException;
@Slf4j
public class MyTest {

    @Test
    public void test1() throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        String adminUid = "Y24lM0RNYW5hZ2VyJTJDZGMlM0RzaWVtZW5zJTJDZGMlM0Rjb20=";
        String adminPassword = "MTIzNDU2";
        String ldapusername = new String(decoder.decodeBuffer(adminUid));
        String ldapuserpwd = new String(decoder.decodeBuffer(adminPassword));
        log.info(ldapusername);
        log.info(ldapuserpwd);


    }
}

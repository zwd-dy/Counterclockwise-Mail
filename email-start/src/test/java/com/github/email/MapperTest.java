package com.github.email;

import com.github.email.entity.Mail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
public class MapperTest {

    @Autowired
    private MongoTemplate template;

    @Test
    public void testMapper(){
        Mail mail = new Mail();
        mail.setContent("testtest");
        mail.setFrom("发件人2");
        mail.setSubject("主题2");
        mail.setFileId("235325");
        mail.setRecipient("收件人2");
        System.out.println(template.insert(mail));

    }
}

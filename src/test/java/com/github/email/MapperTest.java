package com.github.email;

import com.github.email.dao.SysEmailPlatformDao;
import com.github.email.entity.SysEmailPlatform;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MapperTest {

    @Autowired
    private SysEmailPlatformDao sysEmailPlatformDao;

    @Test
    public void test() {
        SysEmailPlatform sysEmailPlatform = new SysEmailPlatform();
        sysEmailPlatform.setName("QQ邮箱");
//        sysEmailPlatformDao.addOne()
    }
}

package com.shadougao.email.receive.execute;

import cn.hutool.core.date.DateUtil;
import com.shadougao.email.dao.mongo.MailDao;
import com.shadougao.email.entity.*;
import com.shadougao.email.receive.config.GlobalConfig;
import com.shadougao.email.receive.config.RedisConfig;
import com.shadougao.email.receive.utils.GetBeanUtil;
import com.shadougao.email.receive.utils.RedisUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.util.*;

@Getter
@Setter
@Slf4j
public class MailListener implements Runnable {

    private int sleepTime;  // 秒级
    private SysEmailPlatform platform;
    private UserBindEmail bindEmail;
    private Store store;
    private IMAPFolder folder;
    private boolean isClose;
    private MailDao mailDao;
    private RedisUtil redisUtil;
    private RedisConfig redisConfig;
    private MailTask mailTask;
    private GlobalConfig globalConfig;

    // 检测到的uid集合
    private List<String> detectedUids = new ArrayList<>();

    public MailListener() {
        mailDao = GetBeanUtil.getApplicationContext().getBean(MailDao.class);
        redisUtil = GetBeanUtil.getApplicationContext().getBean(RedisUtil.class);
        redisConfig = GetBeanUtil.getApplicationContext().getBean(RedisConfig.class);
        globalConfig = GetBeanUtil.getApplicationContext().getBean(GlobalConfig.class);
        mailTask = GetBeanUtil.getApplicationContext().getBean(MailTask.class);
    }

    @Override
    public void run() {
        this.isClose = false;
        Thread.currentThread().setName(bindEmail.getEmailUser());
        synchronized (MailTask.class) {
            mailTask.getThreads().add(this);
        }
        try {
            // 准备连接服务器的会话信息
            this.store = connect();
            Date date = new Date();

            folder = (IMAPFolder) store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            SearchTerm comparisonTermGe = new ReceivedDateTerm(ComparisonTerm.GE, date);

//        int count = folder.getMessageCount();
            int count = folder.search(comparisonTermGe).length;
            folder.close();


            while (!this.isClose) {
                folder = (IMAPFolder) store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);

                Message[] messages = folder.search(comparisonTermGe);
                int newCount = messages.length;

                log.info("[{}] {}，时间范围：{}，邮件数：{}",globalConfig.nodeName,Thread.currentThread().getName(),DateUtil.format(date,"YYYY-MM-dd"),newCount);

                if (newCount != count) {
//                int i = newCount - count;
//                for (int j = count; j < newCount; j++) {
//                    Message message = messages[j];
//                    IMAPMessage msg = (IMAPMessage) message;
//                    String subject = MimeUtility.decodeText(msg.getSubject());
//
//                    System.out.println(Thread.currentThread().getName() + "，有新邮件" + subject);
//                }
                    // 从邮箱服务器获取所有邮箱uid
                    List<String> uids = new ArrayList<>();
                    for (Message message : messages) {
                        uids.add(String.valueOf(folder.getUID(message)));
                    }
                    // 根据这些uid从数据库获取邮箱
                    List<Mail> localMailList = mailDao.findByUidAndBindId(uids, bindEmail.getId());
                    // 创建存放新邮件UID的集合
                    List<String> newUids = new ArrayList<>();
                    // 对比邮箱，找出新邮箱
                    for (int i = 0; i < uids.size(); i++) {
                        boolean flag = false;
                        for (int j = 0; j < localMailList.size(); j++) {
                            if (uids.get(i).equals(localMailList.get(j).getUid())) {
                                flag = true;
                                break;
                            }
                        }
                        // 判断flag
                        if (!flag) {
                            // 新邮箱就是我！
                            String uid = uids.get(i);
                            // 向本地缓存检测该uid有没有被检测过
                            if (!detectedUids.contains(uid)) {
                                newUids.add(uid);
//                                System.out.println(uid);
                                // 向缓存集合加入检测到的uid
                                detectedUids.add(uid);
                            }
                        }
                    }
                    if (newUids.size() > 0) {
                        // 通知主程序有新邮件，传递uid，邮件内容让主程序解析
                        Map<String, Object> map = new HashMap<>();
                        map.put("bindEmail", bindEmail);
                        map.put("newUids", newUids);
                        redisUtil.publist(redisConfig.executeChannel, new RedisResult(RedisResultEnum.NEW_MAIL_UIDS, map));
                        log.info("[{}] - 检测到 {} 条新邮件，已转交给主程序解析内容",globalConfig.nodeName,newUids.size());
                    }
                    count = newCount;
                    date = new Date();
                }
                folder.close();
                // 周期性睡眠
                sleep();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.warn("[{}] - {}，发生异常，准备重新启动监听",globalConfig.nodeName,Thread.currentThread().getName());
            // 断线重连
            MailListener listener = new MailListener();
            // 设置监控邮箱
            listener.setBindEmail(bindEmail);
            // 设置对应邮箱平台
            listener.setPlatform(platform);
            // 设置轮询周期（秒级）
            listener.setSleepTime(sleepTime);
            // 执行
            mailTask.getPoolExecutor().execute(listener);
        } finally {
            close(store, folder);
            log.info("[{}] - 线程{}关闭",globalConfig.nodeName,Thread.currentThread().getName());
        }
    }


    /**
     * 建立imap连接，返回store
     *
     * @return
     */
    public Store connect() throws MessagingException {
        SysEmailPlatform.Connect imap = platform.getConnect().get("imap");

        String host = imap.getHost();
        String port = imap.getPort();
        String username = bindEmail.getEmailUser();
        String password = bindEmail.getEmailAuth();
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", host); // imap主机名
        props.setProperty("mail.imap.port", port); // 主机端口号

        // 连接信息附加
        List<Map<String, String>> smtpProps = imap.getProps();
        if (smtpProps != null) {
            for (Map<String, String> smtpProp : smtpProps) {
                props.setProperty(smtpProp.get("name"), smtpProp.get("value"));
            }
        }

        // 创建Session实例对象
        Session session = Session.getInstance(props);
        // 创建IMAP协议的Store对象
        IMAPStore store = (IMAPStore) session.getStore("imap");
        // 连接邮件服务器
        store.connect(username, password);
        // 客户端附加信息（例如网易这事b需要）
        List<Map<String, String>> clientParams = imap.getClientParams();
        if (clientParams != null) {
            HashMap IAM = new HashMap();
            for (Map<String, String> clientParam : clientParams) {
                IAM.put(clientParam.get("name"), clientParam.get("value").replace("{account}", bindEmail.getEmailUser()));
            }
            store.id(IAM);
        }


        return store;
    }

    void sleep() throws InterruptedException {
        int count = 0;
        while (count < sleepTime) {
            if (isClose) {
                break;
            }
            count++;
            Thread.sleep(1000);
        }
    }

    /**
     * 关闭连接
     *
     * @param store
     * @param folder
     */
    void close(Store store, Folder folder) {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close();
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
        if (store != null) {
            try {
                store.close();
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void closeTask() {
        setClose(true);
        close(store,folder);
    }

}



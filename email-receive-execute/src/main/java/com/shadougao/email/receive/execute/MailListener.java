package com.shadougao.email.receive.execute;

import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.util.*;

@Getter
@Setter
public class MailListener implements Runnable {

    private int sleepTime;  // 秒级
    private SysEmailPlatform platform;
    private UserBindEmail bindEmail;
    private Store store;

    @SneakyThrows
    @Override
    public void run() {
        // 准备连接服务器的会话信息
        this.store = connect();

        Date date = new Date();

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        SearchTerm comparisonTermGe = new ReceivedDateTerm(ComparisonTerm.GE, date);

//        int count = folder.getMessageCount();
        int count = folder.search(comparisonTermGe).length;
        folder.close();

        while (true) {
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.search(comparisonTermGe);
//            int newCount = folder.getMessageCount();
            int newCount = messages.length;
            System.out.println(Thread.currentThread().getName() + "，" + newCount);


            if (newCount > count) {
                int i = newCount - count;
                for (int j = count; j < newCount; j++) {
                    Message message = messages[j];
                    IMAPMessage msg = (IMAPMessage) message;
                    String subject = MimeUtility.decodeText(msg.getSubject());

                    System.out.println(Thread.currentThread().getName() + "，有新邮件" + subject);
                }
//                Message message = folder.getMessage(newCount);
//                IMAPMessage msg = (IMAPMessage) message;
//                String subject = MimeUtility.decodeText(msg.getSubject());
//                System.out.println(Thread.currentThread().getName() + "，有新邮件" + subject);
                count = newCount;
            }

            folder.close();


            sleep();
        }
    }


    /**
     * 建立imap连接，返回store
     *
     * @return
     */
    public Store connect() {
        SysEmailPlatform.Connect imap = platform.getConnect().get("imap");

        String host = imap.getHost();
        String port = imap.getPort();
        String username = bindEmail.getEmailUser();
        String password = bindEmail.getEmailAuth();
        // 连接到SMTP服务器587端口:
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

        IMAPStore store = null;
        try {
            // 创建Session实例对象
            Session session = Session.getInstance(props);
            // 创建IMAP协议的Store对象
            store = (IMAPStore) session.getStore("imap");
            // 连接邮件服务器
            store.connect(bindEmail.getEmailUser(), bindEmail.getEmailAuth());
            // 客户端附加信息（例如网易这事b需要）
            List<Map<String, String>> clientParams = imap.getClientParams();
            if (clientParams != null) {
                HashMap IAM = new HashMap();
                for (Map<String, String> clientParam : clientParams) {
                    IAM.put(clientParam.get("name"), clientParam.get("value").replace("{account}", bindEmail.getEmailUser()));
                }
                store.id(IAM);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return store;
    }

    void sleep() throws InterruptedException {
        int count = 0;
        while (count < sleepTime) {
            count++;
            Thread.sleep(1000);
        }
    }

}



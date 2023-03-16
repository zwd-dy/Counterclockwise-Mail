package com.shadougao.email.common.utils;

import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SendMailUtil {

    public static Session connectSmtp(SysEmailPlatform platform, UserBindEmail bindEmail) {
        SysEmailPlatform.Connect smtp = platform.getConnect().get("smtp");

        String host = smtp.getHost();
        String port = smtp.getPort();
        String username = bindEmail.getEmailUser();
        String password = bindEmail.getEmailAuth();
        // 连接到SMTP服务器587端口:
        Properties props = new Properties();
        props.put("mail.smtp.host", host); // SMTP主机名
        props.put("mail.smtp.port", port); // 主机端口号

        // 连接信息附加
        List<Map<String, String>> smtpProps = smtp.getProps();
        for (Map<String, String> smtpProp : smtpProps) {
            props.put(smtpProp.get("name"), smtpProp.get("value"));
        }
        // 获取Session实例:
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        return session;
    }

    public static boolean check(SysEmailPlatform platform, UserBindEmail bindEmail) {
        Session session = connectSmtp(platform, bindEmail);
        Transport transport = null;
        boolean success = true;
        try {
            transport = session.getTransport();
            transport.connect();
        } catch (Exception e) {
            success = false;
        } finally {
            try {
                if (transport != null) {
                    transport.close();
                }
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
        return success;
    }


    public static void sendEmail(SysEmailPlatform platform, UserBindEmail bindEmail, Mail mail) {

        Session session = connectSmtp(platform, bindEmail);

        Transport transport = null;

        try {
            transport = session.getTransport();
            MimeMessage message = new MimeMessage(session);
            // 设置发送方地址:
            message.setFrom(new InternetAddress(mail.getFrom()));
            // 设置接收方地址:
            String[] recipients = mail.getRecipients();
            for (String recipient : recipients) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }
            // 设置邮件主题:
            message.setSubject(mail.getSubject(), "UTF-8");


            // 创建消息部分
            BodyPart messageBodyPart = new MimeBodyPart();
            // 消息
            messageBodyPart.setContent(mail.getContent(), "text/html;charset=gbk");
            // 创建多重消息
            Multipart multipart = new MimeMultipart();
            // 设置文本消息部分
            multipart.addBodyPart(messageBodyPart);
            // 附件部分

//            messageBodyPart = new MimeBodyPart();
//            String filename = "C:\\Users\\dd\\Desktop\\朱文迪.pdf";
//            DataSource source = new FileDataSource(filename);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName("文件");
//            multipart.addBodyPart(messageBodyPart);

            // 发送完整消息
            message.setContent(multipart);
            // 发送:
            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());

        } catch (Exception e) {

            throw new RuntimeException(e);

        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

}

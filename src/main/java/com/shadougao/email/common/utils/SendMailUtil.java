package com.shadougao.email.common.utils;

import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
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


            // 构建混合邮件块，附件+邮件
            MimeMultipart mixed = new MimeMultipart("mixed");

            // 附件部分
            MimeBodyPart file_body = new MimeBodyPart();
            DataHandler dhFile = new DataHandler(new FileDataSource("C:\\Users\\admin\\Desktop\\朱文迪.pdf"));
            file_body.setDataHandler(dhFile); //设置dhFile附件处理
//            file_body.setContentID("fileA");  //设置资源附件名称ID
            file_body.setFileName(MimeUtility.encodeText("朱文迪.pdf"));   //设置中文附件名称
            // 先把附件资源混合到 mixed多资源邮件模块里
            mixed.addBodyPart(file_body);


            // 5.3：创建主体内容资源存储对象
            MimeBodyPart content = new MimeBodyPart();
            // 把主体内容混合到mixed资源存储对象里
            mixed.addBodyPart(content);
            // 构建一个多资源的邮件块 用来把 文本内容资源 和 图片资源合并
            MimeMultipart text_img_related = new MimeMultipart("related");
            content.setContent(text_img_related);

            // 图片部分
            MimeBodyPart img_body = new MimeBodyPart();
            DataHandler dhImg = new DataHandler(new FileDataSource("C:\\Users\\admin\\Pictures\\screenshots\\1.png"));
            img_body.setDataHandler(dhImg); //设置dhImg图片处理
            img_body.setContentID("imgA");  //设置资源图片名称ID




            // 创建文本部分
            MimeBodyPart text_body = new MimeBodyPart();
            text_body.setContent(mail.getContent(),"text/html;charset=UTF-8");

            // 合并资源
            text_img_related.addBodyPart(text_body);
            text_img_related.addBodyPart(img_body);

            // 附件部分
//            messageBodyPart = new MimeBodyPart();
//            String filename = "C:\\Users\\dd\\Desktop\\朱文迪.pdf";
//            DataSource source = new FileDataSource(filename);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName("文件");
//            multipart.addBodyPart(messageBodyPart);

            // 发送完整消息
            message.setContent(mixed);
            // 保存上面设置的邮件内容
            message.saveChanges();
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

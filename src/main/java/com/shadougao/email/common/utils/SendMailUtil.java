package com.shadougao.email.common.utils;

import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.MailFile;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.execute.SendMailExecute;
import com.shadougao.email.service.MailFileService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class SendMailUtil {






    public static void sendEmailTest(SysEmailPlatform platform, UserBindEmail bindEmail, Mail mail) {

        Session session = SendMailExecute.connectSmtp(platform, bindEmail);

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


            // 创建主体内容资源存储对象
            MimeBodyPart content = new MimeBodyPart();

            // 构建一个多资源的邮件块 用来把 文本内容资源 和 图片资源合并
            MimeMultipart text_img_related = new MimeMultipart("related");


            // 图片部分
            MimeBodyPart img_body = new MimeBodyPart();
            DataHandler dhImg = new DataHandler(new FileDataSource("C:\\Users\\admin\\Pictures\\screenshots\\1.png"));
            img_body.setDataHandler(dhImg); //设置dhImg图片处理
            img_body.setContentID("imgA");  //设置资源图片名称ID


            // 创建文本部分
            MimeBodyPart text_body = new MimeBodyPart();
            text_body.setContent(mail.getContent(), "text/html;charset=UTF-8");

            // 合并资源
            text_img_related.addBodyPart(text_body);
            text_img_related.addBodyPart(img_body);
            content.setContent(text_img_related);
            // 把主体内容混合到mixed资源存储对象里
            mixed.addBodyPart(content);
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


    public static void sendEmail(MailFileService fileService, SysEmailPlatform platform, UserBindEmail bindEmail, Mail mail) {

        Session session = SendMailExecute.connectSmtp(platform, bindEmail);

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
            String[] fileIds = mail.getFileId();
            // 获取文件存储在数据库的详细信息
            List<MailFile> files = fileService.getByIds(Arrays.asList(fileIds));
            for (MailFile file : files) {
                MimeBodyPart file_body = new MimeBodyPart();
                DataHandler dhFile = new DataHandler(new FileDataSource(file.getAbsolutePath()));
                //设置dhFile附件处理
                file_body.setDataHandler(dhFile);
                // 设置附件ID
                file_body.setContentID(file.getId());
                //设置附件名称
                file_body.setFileName(MimeUtility.encodeText(file.getName()));
                // 把附件资源混合到 mixed多资源邮件模块里
                mixed.addBodyPart(file_body);
            }


            // 创建主体内容资源存储对象
            MimeBodyPart content = new MimeBodyPart();


            // 图片部分
            List<String> imgIds = new ArrayList<>();

            Document document = Jsoup.parse(mail.getContent());
            // 获取每一个  <img alt=''>标签
            Elements elements = document.select("img[alt]");

            for (int i = 0; i < elements.size(); i++) {
                // 获取单个 <img> 标签
                Element element = elements.get(i);
                // 获取 alt 内容，因为我将图片id存到了alt属性中
                String fileId = element.attr("alt");
                // 判断值是否为id，暂且用长度判断
                if (fileId.length() == 19) {
                    // 将id保存下来
                    imgIds.add(fileId);
                    // 更新src属性值
                    element.attr("src", "cid:" + fileId);
                }
            }
            // 从数据库获取所有image信息
            List<MailFile> images = fileService.getByIds(imgIds);



            // 构建一个多资源的邮件块 用来把 文本内容资源 和 图片资源合并
            MimeMultipart text_img_related = new MimeMultipart("related");

            for (MailFile image : images) {
                MimeBodyPart img_body = new MimeBodyPart();
                DataHandler dhImg = new DataHandler(new FileDataSource(image.getAbsolutePath()));
                img_body.setDataHandler(dhImg); //设置dhImg图片处理
                img_body.setContentID(image.getId());  //设置资源图片名称ID
                text_img_related.addBodyPart(img_body);
            }

            // 创建文本部分
            MimeBodyPart text_body = new MimeBodyPart();
            text_body.setContent(document.html(), "text/html;charset=UTF-8");

            // 合并资源
            text_img_related.addBodyPart(text_body);

            content.setContent(text_img_related);
            // 把主体内容混合到mixed资源存储对象里
            mixed.addBodyPart(content);

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

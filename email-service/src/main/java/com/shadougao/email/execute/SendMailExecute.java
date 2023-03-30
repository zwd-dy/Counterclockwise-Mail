package com.shadougao.email.execute;

import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.MailFile;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.service.MailFileService;
import com.shadougao.email.service.MailService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 发邮件核心类
 */

public class SendMailExecute implements Runnable {

    private MailFileService fileService;
    private MailService mailService;
    private UserBindEmail bindEmail;
    private SysEmailPlatform platform;
    private Mail mail;

    public SendMailExecute(UserBindEmail bindEmail, SysEmailPlatform platform, Mail mail) {
        this.bindEmail = bindEmail;
        this.platform = platform;
        this.mail = mail;

        this.fileService = GetBeanUtil.getApplicationContext().getBean(MailFileService.class);
        this.mailService = GetBeanUtil.getApplicationContext().getBean(MailService.class);
    }


    @Override
    public void run() {
        Session session = connectSmtp(platform, bindEmail);
        Transport transport = null;

        try {
            transport = session.getTransport();

            // 构建邮件收发方信息
            MimeMessage message = constructMessage(session, mail);
            // 构建混合邮件块，附件+邮件
            MimeMultipart mixed = new MimeMultipart("mixed");
            // 附件部分
            List<BodyPart> fileBodyList = constructFileBody(mail.getFileId(), fileService);
            // 创建主体内容资源存储对象
            MimeBodyPart content = constructContentBody(mail.getContent(), fileService);

            // 把附件部分与主体内容部分合并到 mixed 中
            addBodyPart(mixed, fileBodyList);
            mixed.addBodyPart(content);
            // 发送邮件
            sendMessage(message, mixed, transport);

            // 更新状态
            mail.setType(MailEnum.SEND_SUCCESS);
            mail.setBindId(bindEmail.getId());
            mail.setSendTime(System.currentTimeMillis());
            mail.setSendState(MailEnum.SEND_SUCCESS);

        } catch (Exception e) {
            mail.setSendExceptionLog(e.getMessage());
            mail.setSendState(MailEnum.SEND_ERROR);
            throw new RuntimeException(e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }

            }
            // 更新状态
            mailService.updateOne(mail);
        }
    }

    /**
     * 发送邮件
     *
     * @param message
     * @param mixed
     * @param transport
     * @throws MessagingException
     */
    public void sendMessage(MimeMessage message, MimeMultipart mixed, Transport transport) throws MessagingException {
        // 发送完整消息
        message.setContent(mixed);
        // 保存上面设置的邮件内容
        message.saveChanges();
        // 发送
        transport.connect();
        transport.sendMessage(message, message.getAllRecipients());
    }

    /**
     * 构建邮件收发方信息
     *
     * @throws MessagingException
     */
    public MimeMessage constructMessage(Session session, Mail mail) throws MessagingException {
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
        return message;
    }

    /**
     * 构建邮箱图片主体
     *
     * @throws MessagingException
     */
    public List<BodyPart> constructImgBody(List<String> ids, MailFileService fileService) throws MessagingException {
        List<BodyPart> list = new ArrayList<>();
        List<MailFile> images = fileService.getByIds(ids);
        for (MailFile image : images) {
            MimeBodyPart imgBody = new MimeBodyPart();
            DataHandler dhImg = new DataHandler(new FileDataSource(image.getAbsolutePath()));
            imgBody.setDataHandler(dhImg); //设置dhImg图片处理
            imgBody.setContentID(image.getId());  //设置资源图片名称ID
            list.add(imgBody);
        }
        return list;
    }

    /**
     * 构建邮箱文本主体（html）
     *
     * @throws MessagingException
     */
    public BodyPart constructTextBody(String mailContent) throws MessagingException {
        // 创建文本部分
        MimeBodyPart textBody = new MimeBodyPart();
        textBody.setContent(mailContent, "text/html;charset=UTF-8");
        return textBody;
    }

    /**
     * 构建多资源邮件块，包含文本与图片主体
     *
     * @throws MessagingException
     */
    public MimeMultipart constructRelatedMultipart(String mailContent, MailFileService fileService) throws MessagingException {
        Map<String, Object> map = replaceAllContentByTagSrc(mailContent);
        List<String> ids = (List<String>) map.get("ids");
        String content = (String) map.get("content");

        // 构建一个多资源的邮件块 用来把 文本内容资源 和 图片资源合并
        MimeMultipart related = new MimeMultipart("related");
        // 构建图片主体
        List<BodyPart> bodyList = constructImgBody(ids, fileService);
        // 构建文本主体并加入到集合
        bodyList.add(constructTextBody(content));
        // 合并主体到related
        addBodyPart(related, bodyList);
        return related;
    }

    /**
     * 构建邮箱附件主体
     *
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public List<BodyPart> constructFileBody(String[] fileIds, MailFileService fileService) throws MessagingException, UnsupportedEncodingException {
        List<BodyPart> fileBodyList = new ArrayList<>();
        // 获取文件存储在数据库的详细信息
        List<MailFile> files = fileService.getByIds(Arrays.asList(fileIds));
        for (MailFile file : files) {
            MimeBodyPart fileBody = new MimeBodyPart();
            DataHandler dhFile = new DataHandler(new FileDataSource(file.getAbsolutePath()));

            fileBody.setDataHandler(dhFile);       //设置dhFile附件处理
            fileBody.setContentID(file.getId());   // 设置附件ID
            fileBody.setFileName(MimeUtility.encodeText(file.getName()));  //设置附件名称
            fileBodyList.add(fileBody);
        }
        return fileBodyList;
    }

    /**
     * 对邮箱文本内容进行html解析，
     * 将前端上传的图片转换成邮箱能识别的图片
     *
     * @param mailContent
     * @return
     */
    public Map<String, Object> replaceAllContentByTagSrc(String mailContent) {
        List<String> imgIds = new ArrayList<>();
        Document document = Jsoup.parse(mailContent);
        // 获取每一个  <img alt=''>标签
        Elements elements = document.select("img[alt]");

        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);  // 获取单个 <img> 标签
            String fileId = element.attr("alt");    // 获取 alt 内容，因为我将图片id存到了alt属性中
            if (fileId.length() == 19) {     // 判断值是否为id，暂且用长度判断
                // 将id保存下来
                imgIds.add(fileId);
                // 更新src属性值
                element.attr("src", "cid:" + fileId);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("content", document.html());
        map.put("ids", imgIds);
        return map;
    }

    /**
     * 构建邮箱主体
     *
     * @throws MessagingException
     */
    public MimeBodyPart constructContentBody(String mailContent, MailFileService fileService) throws MessagingException {
        MimeBodyPart content = new MimeBodyPart();
        MimeMultipart related = constructRelatedMultipart(mailContent, fileService);
        content.setContent(related);
        return content;
    }

    public void addBodyPart(MimeMultipart multipart, List<BodyPart> bodyPart) {
        try {
            for (BodyPart part : bodyPart) {
                multipart.addBodyPart(part);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

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

    /**
     * 验证邮箱账号
     *
     * @param platform
     * @param bindEmail
     * @return
     */
    public static boolean checkAuth(SysEmailPlatform platform, UserBindEmail bindEmail) {
        Session session = SendMailExecute.connectSmtp(platform, bindEmail);
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

}

package com.shadougao.email.execute;

import com.alibaba.fastjson.JSON;
import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.MailFile;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.service.MailFileService;
import com.shadougao.email.service.MailService;
import com.shadougao.email.service.UserBindEmailService;
import com.shadougao.email.utils.EmailUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bson.Document;
import org.jsoup.helper.StringUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.util.*;

/**
 * 有一封邮件就需要建立一个ReciveMail对象
 */
@Getter
@Setter
public class MailParseExecute implements Runnable {
    private long uid;
    private UserBindEmail bindEmail;
    private SysEmailPlatform platform;
    private MailService mailService;
    private MailFileService fileService;
    private UserBindEmailService bindEmailService;
    private MimeMessage mimeMessage = null;
    private String saveAttachPath = "D:\\file\\static"; //附件下载后的存放目录
    private StringBuffer bodytext = new StringBuffer();//存放邮件内容
    private boolean isBatch;    // true.拉取所有邮件  false.根据uid拉取邮件

    public MailParseExecute() {
        this.fileService = GetBeanUtil.getApplicationContext().getBean(MailFileService.class);
        this.mailService = GetBeanUtil.getApplicationContext().getBean(MailService.class);
        this.bindEmailService = GetBeanUtil.getApplicationContext().getBean(UserBindEmailService.class);
    }


    /**
     * 获得发件人的地址和姓名
     */
    public String getFrom() throws Exception {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String from = address[0].getAddress();
        if (from == null)
            from = "";
        String personal = address[0].getPersonal();
        if (personal == null)
            personal = "";
        String fromaddr = personal + "<" + from + ">";
        return fromaddr;
    }

    /**
     * 获取发件人邮箱
     *
     * @return
     * @throws Exception
     */
    public String getFromMail() throws Exception {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String from = address[0].getAddress();
        if (from == null)
            from = "";
        return from;
    }

    /**
     * 获取发件人姓名
     *
     * @return
     * @throws Exception
     */
    public String getFromName() throws Exception {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String personal = address[0].getPersonal();
        if (personal == null)
            personal = "";
        return personal;
    }

    /**
     * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
     */
    public String[] getMailAddress(String type) throws Exception {
        List<String> addressList = new ArrayList<>();
//        String mailaddr = "";
        String addtype = type.toUpperCase();
        InternetAddress[] address = null;
        if (addtype.equals("TO") || addtype.equals("CC") || addtype.equals("BCC")) {
            if (addtype.equals("TO")) {
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
            } else if (addtype.equals("CC")) {
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
            } else {
                address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
            }

            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    String email = address[i].getAddress();
                    if (email == null)
                        email = "";
                    else {
                        email = MimeUtility.decodeText(email);
                    }
//                    String personal = address[i].getPersonal();
//                    if (personal == null)
//                        personal = "";
//                    else {
//                        personal = MimeUtility.decodeText(personal);
//                    }
//                    String compositeto = personal + "<" + email + ">";
//                    mailaddr += "," + compositeto;
                    addressList.add(email);
                }
//                mailaddr = mailaddr.substring(1);
            }
        } else {
            throw new Exception("Error emailaddr type!");
        }

        return addressList.toArray(new String[addressList.size()]);
    }

    /**
     * 获得邮件主题
     */
    public String getSubject() throws MessagingException {
        String subject = "";
        try {
            subject = MimeUtility.decodeText(mimeMessage.getSubject());
            if (subject == null)
                subject = "";
        } catch (Exception exce) {
        }
        return subject;
    }


    public Long getLongSentDate() throws MessagingException {
        Date sentdate = mimeMessage.getSentDate();
        if (sentdate == null) {
            return 0L;
        }
        return sentdate.getTime();
    }

    public Long getLongReceivedDate() throws MessagingException {
        Date sentdate = mimeMessage.getReceivedDate();
        return sentdate.getTime();
    }

    /**
     * 获得邮件正文内容
     */
    public String getBodyText() {
        return bodytext.toString();
    }

    /**
     * 解析邮件，根据MimeType类型的不同依次执行不同的操作，用StringBuffer
     */
    public void getMailContent(Part part) throws Exception {
        String contenttype = part.getContentType();
        int nameindex = contenttype.indexOf("name");
        boolean conname = false;
        if (nameindex != -1)
            conname = true;
//        System.out.println("CONTENTTYPE: " + contenttype);
        if (part.isMimeType("text/plain") && !conname) {
            bodytext.append((String) part.getContent());
        } else if (part.isMimeType("text/html") && !conname) {
            bodytext.append((String) part.getContent());
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i = 0; i < counts; i++) {
                getMailContent(multipart.getBodyPart(i));
            }
        } else if (part.isMimeType("message/rfc822")) {
            getMailContent((Part) part.getContent());
        } else {
        }
    }


    /**
     * 判断此邮件是否包含附件
     */
    public boolean isContainAttach(Part part) throws Exception {
        boolean attachflag = false;
        String contentType = part.getContentType();
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                        .equals(Part.INLINE))))
                    attachflag = true;
                else if (mpart.isMimeType("multipart/*")) {
                    attachflag = isContainAttach((Part) mpart);
                } else {
                    String contype = mpart.getContentType();
                    if (contype.toLowerCase().indexOf("application") != -1)
                        attachflag = true;
                    if (contype.toLowerCase().indexOf("name") != -1)
                        attachflag = true;
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            attachflag = isContainAttach((Part) part.getContent());
        }
        return attachflag;
    }

    /**
     * 保存附件
     */
    public void saveAttachMent(Part part) throws Exception {
        String fileName = "";
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                        .equals(Part.INLINE)))) {
                    fileName = mpart.getFileName();
                    if (fileName.toLowerCase().indexOf("gb2312") != -1) {
                        fileName = MimeUtility.decodeText(fileName);
                    }
                    saveFile(fileName, mpart.getInputStream());
                } else if (mpart.isMimeType("multipart/*")) {
                    saveAttachMent(mpart);
                } else {
                    fileName = mpart.getFileName();
                    if ((fileName != null)
                            && (fileName.toLowerCase().indexOf("GB2312") != -1)) {
                        fileName = MimeUtility.decodeText(fileName);
                        saveFile(fileName, mpart.getInputStream());
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachMent((Part) part.getContent());
        }
    }


    /**
     * 获得附件存放路径
     */
    public String getAttachPath() {
        return saveAttachPath;
    }

    /**
     * 保存附件到指定目录
     */
    private void saveFile(String fileName, InputStream in) throws Exception {
        String osName = System.getProperty("os.name");
        String storedir = getAttachPath();
        String separator = "";
        if (osName == null)
            osName = "";
        if (osName.toLowerCase().indexOf("win") != -1) {
            separator = "\\";
            if (storedir == null || storedir.equals(""))
                storedir = "D:\\file\\static";
        } else {
            separator = "/";
            storedir = "/tmp";
        }
        File storefile = new File(storedir + separator + MimeUtility.decodeText(fileName));
        System.out.println("storefile's path: " + storefile.toString());

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(storefile));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("文件保存失败!");
        } finally {
            bos.close();
            bis.close();
        }
    }


    public static void main(String[] args) throws Exception {
        MailParseExecute execute = new MailParseExecute();

        Mail mail = new Mail();
        mail.setUid("2341");

        String p = "{\"_id\":\"3453464363463\",\"name\":\"QQ邮箱\",\"connect\":{\"smtp\":{\"host\":\"smtp.qq.com\",\"port\":587,\"props\":[{\"name\":\"mail.smtp.auth\",\"value\":\"true\"},{\"name\":\"mail.smtp.starttls.enable\",\"value\":\"true\"}]},\"pop\":{\"host\":\"pop.qq.com\",\"port\":110},\"imap\":{\"host\":\"imap.qq.com\",\"port\":143}},\"create_time\":\"2023-03-13 15:53:20\"}";
        SysEmailPlatform platform = JSON.parseObject(p, SysEmailPlatform.class);
        String b = "{\"_id\":\"1636366520646971392\",\"platformId\":\"3453464363463\",\"userId\":1,\"emailUser\":\"370907944@qq.com\",\"emailAuth\":\"vprzdsslmcnbbgfb\",\"create_time\":\"2023-03-16 21:59:39\"}";
        UserBindEmail bindEmail = JSON.parseObject(b, UserBindEmail.class);

        execute.setPlatform(platform);
        execute.setBindEmail(bindEmail);
        execute.setUid(2341);

//        execute.setBatch(false);
        new Thread(execute).start();
    }

    public long[] longToArray(long l) {
        long[] arr = new long[1];
        arr[0] = l;
        return arr;
    }

    @Override
    public void run() {
        Store store = null;
        IMAPFolder folder = null;
        List<Mail> mailList = new ArrayList<>();
//        List<Document> documents = new ArrayList<>();

        try {
            // 准备连接服务器的会话信息
            store = EmailUtil.connectImap(platform,bindEmail);
            folder = (IMAPFolder) store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            // Fetch 一下
            FetchProfile profile = new FetchProfile();
            profile.add(UIDFolder.FetchProfileItem.UID);
            profile.add(UIDFolder.FetchProfileItem.ENVELOPE);
            profile.add(UIDFolder.FetchProfileItem.FLAGS);
            profile.add(UIDFolder.FetchProfileItem.SIZE);
            profile.add(UIDFolder.FetchProfileItem.CONTENT_INFO);

            Message[] messages = null;
            if (isBatch) {
                // 设置邮箱账号为同步中
                bindEmail.setSynchronizing(1);
                bindEmailService.updateOne(bindEmail);
                messages = folder.getMessages();
            } else {
                messages = folder.getMessagesByUID(longToArray(uid));
            }
            folder.fetch(messages, profile);
            for (int i = 0; i < messages.length; i++) {
                Mail mail = new Mail();
                mail.setUserId(bindEmail.getUserId());
                mail.setBindId(bindEmail.getId());
                try {
                    this.mimeMessage = (MimeMessage) messages[i];
                    // 设置uid
                    mail.setUid(String.valueOf(folder.getUID(messages[i])));
                    // 设置邮件类型为收件箱
                    mail.setType(MailEnum.TYPE_RECEIVE);
                    // 设置收件的绑定邮箱
                    mail.setBindId(bindEmail.getId());
                    // 获取主题
                    mail.setSubject(getSubject());
                    // 获取发件日期
                    mail.setSendTime(getLongSentDate());
                    // 获取收件日期
                    mail.setReceiveTime(getLongReceivedDate());
                    // 获取发件人
                    mail.setFrom(getFromMail());
                    // 获取发件人姓名
                    mail.setFormName(getFromName());
                    // 获取收件人
                    mail.setRecipients(getMailAddress("to"));
                    // 获取邮件内容
                    bodytext.setLength(0);
                    getMailContent((Part) messages[i]);
                    mail.setContent(getBodyText());
                    // TODO 判断附件是否存在
//                    if (isContainAttach((Part) messages[i])) {
//                        saveAttachMent((Part) messages[i]);
//                    }
                    System.out.println(i + ", subject：" + mail.getSubject());
                } catch (Exception e) {
                    mail.setReceiveExceptionLog(e.getMessage());
                } finally {
//                    mailService.addOne(mail);
                    mailList.add(mail);
//                    documents.add(Document.parse(JSON.toJSONString(mail)));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (folder != null) {
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

//            mailService.getBaseMapper().getMongoTemplate().getCollection("t_mail").insertMany(documents);
            mailService.getBaseMapper().getMongoTemplate().insert(mailList,Mail.class);
            if (isBatch) {
                // 设置邮箱账号未同步
                bindEmail.setSynchronizing(0);
                bindEmailService.updateOne(bindEmail);
            }
            System.out.println("完成");
        }

    }
}

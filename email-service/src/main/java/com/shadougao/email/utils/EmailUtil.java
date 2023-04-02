package com.shadougao.email.utils;

import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import javax.mail.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class EmailUtil {

    public static Store connectImap(SysEmailPlatform platform, UserBindEmail bindEmail) throws Exception {
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

    public static void delMail(SysEmailPlatform platform, UserBindEmail bindEmail, List<Long> uids) {
        if (uids.size() <= 0) {
            return;
        }

        Store store = null;
        IMAPFolder folder = null;
        try {
            store = connectImap(platform, bindEmail);
            folder = (IMAPFolder) store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            // 删除
            for (int i = 0; i < uids.size(); i++) {
                Message message = folder.getMessageByUID(uids.get(i));
                if(message!=null) {
                    message.setFlag(Flags.Flag.DELETED, true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        } finally {
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    throw new BadRequestException(e.getMessage());
                }
            }
            if (folder != null && folder.isOpen()) {
                try {
                    folder.close();
                } catch (MessagingException e) {
                    throw new BadRequestException(e.getMessage());
                }
            }
        }
    }
}

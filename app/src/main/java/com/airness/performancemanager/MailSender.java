package com.airness.performancemanager;

import java.util.Properties;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by ${AirNess} on 2018/2/23.
 */

public class MailSender {
    /**
     * 以文本格式发送邮件
     * @param mailInfo 待发送的邮件的信息
     */
    public boolean sendTextMail(EmailInfo mailInfo)
    {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate())
        {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro,authenticator);
        try
        {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMessage.setRecipient(Message.RecipientType.TO,to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());


            // 邮件文本/HTML内容
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(mailInfo.getContent(), "text/html");
            multipart.addBodyPart(messageBodyPart);


            MimeBodyPart attachPart=new MimeBodyPart();
            FileDataSource source=new FileDataSource(MyAccessibilityService.FilePath);
            attachPart.setDataHandler(new DataHandler(source));
            attachPart.setFileName(MyAccessibilityService.FilePath);

            // 发送邮件
            Transport.send(mailMessage);
            return true;
        }
        catch (MessagingException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}

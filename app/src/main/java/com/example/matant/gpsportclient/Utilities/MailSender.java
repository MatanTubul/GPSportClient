package com.example.matant.gpsportclient.Utilities;

import android.util.Log;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by nir b on 17/08/2015.
 */
public class MailSender {

    private Session session = null;
    private String mailAcountAuthenticationAddress;
    private String mailSubject;
    private String mailBody;
    private Properties props;
    private String mailAcountAuthenticationPassword;

    public MailSender()
    {
        setMailAcountAuthenticationAddress("GPSport.braude@gmail.com");
        setMailAcountAuthenticationPassword("123qweasdzxc123qwe");
        this.mailEnginePrepare();
        this.setMailSubject("GPSport: Forgotten Password");
    }

    private void mailEnginePrepare()
    {
        props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailAcountAuthenticationAddress, mailAcountAuthenticationPassword);
            }
        });
        Log.d("sendMail", "connection was created");

    }

    public void sendMailTo(String Recipient, String recoveredPassword)
    {
        try {
            this.setMailBody("Dear "+Recipient+",Your password for GPSport application is: "+ recoveredPassword+"." +
                    "Thank you, GPSport Team");
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailAcountAuthenticationAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(Recipient));
            message.setSubject(this.getMailSubject());
            message.setContent(this.getMailBody(), "text/html; charset=utf-8");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{ Transport.send(message);}
                    catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("sendMail", "email was sent");

    }

    //getters and setters
    public String getMailAcountAuthenticationPassword() {
        return mailAcountAuthenticationPassword;
    }

    public void setMailAcountAuthenticationPassword(String mailAcountAuthenticationPassword) {
        this.mailAcountAuthenticationPassword = mailAcountAuthenticationPassword;
    }

    public String getMailAcountAuthenticationAddress() {
        return mailAcountAuthenticationAddress;
    }

    public void setMailAcountAuthenticationAddress(String mailAcountAuthenticationAddress) {
        this.mailAcountAuthenticationAddress = mailAcountAuthenticationAddress;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }





}

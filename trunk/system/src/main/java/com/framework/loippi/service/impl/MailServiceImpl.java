package com.framework.loippi.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.mysql.jdbc.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.framework.loippi.service.MailService;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.TemplateService;
import com.framework.loippi.support.Setting;
import com.framework.loippi.utils.web.SpringUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Service - 邮件
 *
 * @author Mounate Yan。
 * @version 1.0
 */
@Service("mailServiceImpl")
public class MailServiceImpl implements MailService {

    @Resource(name = "freeMarkerConfigurer")
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Resource(name = "sysMailSender")
    private JavaMailSenderImpl javaMailSender;
    @Resource(name = "sysTaskExecutor")
    private TaskExecutor taskExecutor;
    @Resource(name = "templateServiceImpl")
    private TemplateService templateService;
    @Resource
    private TUserSettingService tUserSettingService;

    /**
     * 添加邮件发送任务
     *
     * @param mimeMessage MimeMessage
     */
    private void addSendTask(final MimeMessage mimeMessage) {
        try {
            taskExecutor.execute(new Runnable() {
                public void run() {
                    javaMailSender.send(mimeMessage);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 群发
     */

    public void send(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword, String[] toMail, String subject, String templatePath, Map<String, Object> model, boolean async) {
        Assert.hasText(smtpFromMail);
        Assert.hasText(smtpHost);
        Assert.notNull(smtpPort);
        Assert.hasText(smtpUsername);
        Assert.hasText(smtpPassword);
        Assert.notNull(toMail);
        Assert.hasText(subject);
        Assert.hasText(templatePath);
        try {
            Setting setting = tUserSettingService.get();
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate(templatePath);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            javaMailSender.setHost(smtpHost);
            javaMailSender.setPort(smtpPort);
            javaMailSender.setUsername(smtpUsername);
            javaMailSender.setPassword(smtpPassword);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            mimeMessageHelper.setFrom(MimeUtility.encodeWord(setting.getSiteName()) + " <" + smtpFromMail + ">");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setText(text, true);
            if (async) {
                addSendTask(mimeMessage);
            } else {
                javaMailSender.send(mimeMessage);
            }
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public void send(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword, String toMail, String subject, String templatePath, Map<String, Object> model, boolean async) {
        Assert.hasText(smtpFromMail);
        Assert.hasText(smtpHost);
        Assert.notNull(smtpPort);
        Assert.hasText(smtpUsername);
        Assert.hasText(smtpPassword);
        Assert.hasText(toMail);
        Assert.hasText(subject);
        Assert.hasText(templatePath);
        try {
            Setting setting = tUserSettingService.get();
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate(templatePath);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            javaMailSender.setHost(smtpHost);
            javaMailSender.setPort(smtpPort);
            javaMailSender.setUsername(smtpUsername);
            javaMailSender.setPassword(smtpPassword);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            mimeMessageHelper.setFrom(MimeUtility.encodeWord(setting.getSiteName()) + " <" + smtpFromMail + ">");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setText(text, true);
            if (async) {
                addSendTask(mimeMessage);
            } else {
                javaMailSender.send(mimeMessage);
            }
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void send(String toMail, String subject, String templatePath, Map<String, Object> model, boolean async) {
        Setting setting = tUserSettingService.get();
        send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(), setting.getSmtpPassword(), toMail, subject, templatePath, model, async);
    }

    public void send(String toMail, String subject, String templatePath, Map<String, Object> model) {
        Setting setting = tUserSettingService.get();
        send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(), setting.getSmtpPassword(), toMail, subject, templatePath, model, true);
    }

    public void send(String toMail, String subject, String templatePath) {
        Setting setting = tUserSettingService.get();
        send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(), setting.getSmtpPassword(), toMail, subject, templatePath, null, true);
    }

    public void sendTestMail(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword, String toMail) {
        Setting setting = tUserSettingService.get();
        String subject = SpringUtils.getMessage("admin.setting.testMailSubject", setting.getSiteName());
        com.framework.loippi.entity.Template testMailTemplate = templateService.get("testMail");
        send(smtpFromMail, smtpHost, smtpPort, smtpUsername, smtpPassword, toMail, subject, testMailTemplate.getTemplatePath(), null, false);
    }

    public void send(String subject, String content, String[] toMail, boolean async) {
        try {
            Setting setting = tUserSettingService.get();
            javaMailSender.setHost(setting.getSmtpHost());
            javaMailSender.setPort(setting.getSmtpPort());
            javaMailSender.setUsername(setting.getSmtpUsername());
            javaMailSender.setPassword(setting.getSmtpPassword());
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            mimeMessageHelper.setFrom(MimeUtility.encodeWord(setting.getSiteName()) + " <" + setting.getSmtpFromMail() + ">");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setText(content, true);
            if (async) {
                addSendTask(mimeMessage);
            } else {
                javaMailSender.send(mimeMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void send(String subject, String content, String[] toMail, ClassPathResource attachment, String attachmentName, boolean async) {
        try {
            Setting setting = tUserSettingService.get();
            javaMailSender.setHost(setting.getSmtpHost());
            javaMailSender.setPort(setting.getSmtpPort());
            javaMailSender.setUsername(setting.getSmtpUsername());
            javaMailSender.setPassword(setting.getSmtpPassword());
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            mimeMessageHelper.setFrom(MimeUtility.encodeWord(setting.getSiteName()) + " <" + setting.getSmtpFromMail() + ">");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setText(content, true);
            if (attachment != null) {
                mimeMessageHelper.addAttachment(StringUtils.isNullOrEmpty(attachmentName) ? attachment.getFilename() : attachmentName, attachment);
            }
            if (async) {
                addSendTask(mimeMessage);
            } else {
                javaMailSender.send(mimeMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void send(String subject, String content, String[] toMail, File attachment, String attachmentName, boolean async) {
        try {
            Setting setting = tUserSettingService.get();
            javaMailSender.setHost(setting.getSmtpHost());
            javaMailSender.setPort(setting.getSmtpPort());
            javaMailSender.setUsername(setting.getSmtpUsername());
            javaMailSender.setPassword(setting.getSmtpPassword());
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            mimeMessageHelper.setFrom(MimeUtility.encodeWord(setting.getSiteName()) + " <" + setting.getSmtpFromMail() + ">");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setTo(toMail);
            mimeMessageHelper.setText(content, true);
            if (attachment != null) {
                mimeMessageHelper.addAttachment((StringUtils.isNullOrEmpty(attachmentName) ? attachment.getName() : attachmentName), attachment);
            }
            if (async) {
                addSendTask(mimeMessage);
            } else {
                javaMailSender.send(mimeMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }


}
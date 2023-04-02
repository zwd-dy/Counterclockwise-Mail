package com.shadougao.email.web.controller;

import com.alibaba.fastjson.JSON;
import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.dto.MailTagDTO;
import com.shadougao.email.entity.dto.PageData;
import com.shadougao.email.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final MailService mailService;


    @GetMapping("/pageList")
    public Result<?> mailPageList(PageData<Mail> pageData, Mail mail) {
        mail.setUserId(SecurityUtils.getCurrentUser().getId());
//        mail.setType(MailEnum.TYPE_RECEIVE);
        return Result.success(mailService.pageList(pageData, mail));
    }

    @PostMapping("/send")
    public Result<?> sendMail(@RequestBody Mail mail) {
        mailService.sendMail(mail);
        return Result.success();
    }

    @PostMapping("/schedule")
    public Result<?> schedule(@RequestBody Mail mail) {
        return mailService.schedule(mail);
    }

    @PostMapping("/save")
    public Result<?> saveDraft(@RequestBody Mail mail) {
        return mailService.saveDraft(mail);
    }

    @DeleteMapping("/delete")
    public Result<?> delMail(@RequestBody List<Mail> mailList) {
        mailService.delMail(mailList);
        return Result.success();
    }

    @DeleteMapping("/schedule/delete")
    public Result<?> delScheduleMail(@RequestBody List<Mail> mailList) {
        mailService.delScheduleMail(mailList);
        return Result.success();
    }

    @PostMapping("/schedule/update")
    public Result<?> updateSchedule(@RequestBody Mail mail) {
        mailService.updateSchedule(mail);
        return Result.success();
    }

    @PostMapping("/addToTag")
    public Result<?> addToTag(@RequestBody MailTagDTO mailTagDTO) {
        mailService.addToTag(mailTagDTO.getMailList(), mailTagDTO.getTagIds());
        return Result.success();
    }

    @DeleteMapping("/delToTag")
    public Result<?> delToTag(@RequestBody List<Mail> mailList) {
        mailService.delToTag(mailList);
        return Result.success();
    }

    @PostMapping("/addStar")
    public Result<?> addStar(@RequestBody List<Mail> mailList) {
        mailService.updateStar(mailList,MailEnum.STAR_YES);
        return Result.success();
    }

    @PostMapping("/delStar")
    public Result<?> delStar(@RequestBody List<Mail> mailList) {
        mailService.updateStar(mailList,MailEnum.STAR_NO);
        return Result.success();
    }

}

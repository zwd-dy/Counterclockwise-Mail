package com.shadougao.email.web.controller;

import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.dto.PageData;
import com.shadougao.email.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final MailService mailService;


    @GetMapping("/pageList")
    public Result<?> mailPageList(PageData<Mail> pageData,Mail mail) {
        mail.setUserId(SecurityUtils.getCurrentUser().getId());
//        mail.setType(MailEnum.TYPE_RECEIVE);
        return Result.success(mailService.pageList(pageData,mail));
    }

    @PostMapping("/send")
    public Result<?> sendMail(@RequestBody Mail mail) {
        mailService.sendMail(mail);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result<?> delMail(@RequestBody List<Mail> mailList) {
        mailService.delMail(mailList);
        return Result.success();
    }
}

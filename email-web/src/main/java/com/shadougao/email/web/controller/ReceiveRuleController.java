package com.shadougao.email.web.controller;

import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.ReceiveRule;
import com.shadougao.email.entity.dto.PageData;
import com.shadougao.email.service.ReceiveRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
public class ReceiveRuleController {
    private final ReceiveRuleService ruleService;

    @GetMapping("/pageList")
    public Result<?> rulePageList(PageData<Mail> pageData, ReceiveRule rule) {
        rule.setUserId(SecurityUtils.getCurrentUser().getId());
        return Result.success(ruleService.pageList(pageData, rule));
    }

    @PostMapping("/add")
    public Result<?> addRule(@RequestBody ReceiveRule rule) {
        if (rule.getConditions().size() <= 0) {
            throw new BadRequestException("条件不可为空");
        }
        if (rule.getExecutes().size() <= 0) {
            throw new BadRequestException("执行操作不可为空");
        }
        if (rule.getConditions().size() > 10) {
            throw new BadRequestException("条件不可超过10个");
        }
        rule.setIsOpen(0);
        rule.setUserId(SecurityUtils.getCurrentUser().getId());
        return Result.success(ruleService.addOne(rule));
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody ReceiveRule rule) {
        ruleService.updateOne(rule);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result<?> deleteRule(@RequestBody List<ReceiveRule> receiveRules) {
        List<String> ids = new ArrayList<>();
        receiveRules.forEach(item -> ids.add(item.getId()));
        ruleService.batchDel(ids);
        return Result.success();
    }

    /**
     * 开启规则
     * @param receiveRules
     * @return
     */
    @PostMapping("/open")
    public Result<?> openRule(@RequestBody List<ReceiveRule> receiveRules){
        List<String> ids = new ArrayList<>();
        receiveRules.forEach(item -> ids.add(item.getId()));
        ruleService.getBaseMapper().updateMulti(
                new Query().addCriteria(Criteria.where("_id").in(ids)),
                new Update().set("isOpen", 1)
        );
        return Result.success();
    }

    /**
     * 关闭规则
     * @param receiveRules
     * @return
     */
    @PostMapping("/close")
    public Result<?> closeRule(@RequestBody List<ReceiveRule> receiveRules){
        List<String> ids = new ArrayList<>();
        receiveRules.forEach(item -> ids.add(item.getId()));
        ruleService.getBaseMapper().updateMulti(
                new Query().addCriteria(Criteria.where("_id").in(ids)),
                new Update().set("isOpen", 0)
        );
        return Result.success();
    }
}

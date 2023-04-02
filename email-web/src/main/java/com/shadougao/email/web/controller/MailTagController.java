package com.shadougao.email.web.controller;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.MailTag;
import com.shadougao.email.service.MailTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class MailTagController {
    private final MailTagService tagService;

    @GetMapping("/list")
    public Result<?> getTag(){
        return Result.success(tagService.getTag());
    }

    @PostMapping("/add")
    public Result<?> addTag(@RequestBody MailTag mailTag) {
        return Result.success(tagService.addTag(mailTag));
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> addTag(@PathVariable("id") String tagId) {
        return Result.success(tagService.delTag(tagId));
    }

    @PostMapping("/update")
    public Result<?> updateTag(@RequestBody MailTag mailTag){
        return Result.success(tagService.updateOne(mailTag));
    }
}

package com.shadougao.email.web.controller;

import com.shadougao.email.service.QuartzJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private QuartzJobService quartzJobService;

    @GetMapping("/add")
    public void add() {
        quartzJobService.addJob("testJob2","default"
                ,"0 10 1 2 4 ? 2023-2023",new HashMap<>());
    }

}

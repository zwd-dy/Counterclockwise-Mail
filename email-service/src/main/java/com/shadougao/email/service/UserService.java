package com.shadougao.email.service;


import com.shadougao.email.entity.AddUserDto;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserService {

    void addUser(AddUserDto resource);

    void getValidCode(@RequestParam String username, String email);
}

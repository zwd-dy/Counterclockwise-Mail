package com.shadougao.email.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户通讯录
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("t_address_book")
public class AddressBook extends BaseEntity {
    /**
     * 邮箱账号
     */
    private String emailAddress;
    /**
     * 通讯录名字
     */
    private String name;
    /**
     * 用户id
     */
    private String userId;
}
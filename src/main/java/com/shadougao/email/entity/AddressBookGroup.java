package com.shadougao.email.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 通讯录分组
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("t_address_book_group")
public class AddressBookGroup extends BaseEntity {
    /**
     * 分组名
     */
    private String name;
    /**
     * 用户id
     */
    private Integer userId;
}

package com.shadougao.email.entity;

import com.shadougao.email.annotation.MongoLikeQuery;
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
public class AddressBook extends MongoBaseEntity {
    /**
     * 邮箱账号
     */
    private String emailAddress;
    /**
     * 通讯录名字
     */
    @MongoLikeQuery
    private String name;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 分组Id
     */
    private String groupId;
}

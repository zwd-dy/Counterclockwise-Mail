package com.shadougao.email.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("t_mail_tag")
public class MailTag extends MongoBaseEntity{
    private String label;
    private Long userId;
    private String parentId;
    private String color;
    private List<MailTag> children;

    @Override
    public String toString() {
        return "MailTag{" +
                "label='" + label + '\'' +
                ", userId=" + userId +
                ", children=" + children +
                '}';
    }
}

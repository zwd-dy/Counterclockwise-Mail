package com.shadougao.email.entity.dto;

import com.shadougao.email.entity.Mail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MailTagDTO {
        private List<Mail> mailList;
        private List<String> tagIds;
    }

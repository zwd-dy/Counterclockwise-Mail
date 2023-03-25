package com.shadougao.email.receive.execute;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Getter
@Setter
public class MailTask {
    List<MailListener> threads;
    ThreadPoolExecutor poolExecutor;
}

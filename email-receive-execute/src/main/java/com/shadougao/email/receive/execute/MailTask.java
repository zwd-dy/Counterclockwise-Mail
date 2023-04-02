package com.shadougao.email.receive.execute;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

@Getter
@Setter
public class MailTask {
    List<MailListener> threads;
    ThreadPoolExecutor poolExecutor;
}

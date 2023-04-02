package com.shadougao.email.execute;

import java.util.concurrent.ExecutorService;

public class MailExecutor {
    private ExecutorService sendExecutorService;
    private ExecutorService parseExecutorService;

    public ExecutorService getParseExecutorService() {
        return parseExecutorService;
    }

    public void setParseExecutorService(ExecutorService parseExecutorService) {
        this.parseExecutorService = parseExecutorService;
    }

    public ExecutorService getSendExecutorService() {
        return sendExecutorService;
    }

    public void setSendExecutorService(ExecutorService executorService) {
        this.sendExecutorService = executorService;
    }

    public void executeSend(Runnable task){
        this.sendExecutorService.execute(task);
    }

    public void executeParse(Runnable task){
        this.parseExecutorService.execute(task);
    }
}

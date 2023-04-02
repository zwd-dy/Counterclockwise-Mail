package com.shadougao.email.execute;

import java.util.concurrent.ExecutorService;

public class SendMailExecutor {
    private ExecutorService executorService;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void execute(Runnable task) {
        this.executorService.execute(task);
    }
}

# 多邮件收发系统

## 项目选型

- 项目整体采用单体 **springboot + vue** 为基础框架 
- 邮箱功能采用 **JavaMail** 来驱动
- 数据库
  - 系统表采用 **MySql**（例如用户表）
  - 业务表采用 **MongoDB**
- 使用**SpringBoot Security**实现用户系统
- 使用**quartz**任务调度框架实现邮件定时发送
- 邮件监听采用集群方式，利用 **redis**的发布订阅来当中间件，redis缓存当邮件任务池，实现cluster与后端程序的交互
- 使用**websocket**实现对前端新邮件的推送
- 前端采用 **quasar** 框架，<a href='https://github.com/zwd-dy/email-ui'>前端地址</a>

## 项目结构

- project
  - email-entity
  - email-dao
  - email-service
  - email-common
  - email-web
  - email-receive-execute (外部程序，监听集群)

## 部分流程

### 监听流程

<img src="http://rs4h5afyg.hn-bkt.clouddn.com/view/%E9%82%AE%E4%BB%B6%E7%9B%91%E5%90%AC%E6%B5%81%E7%A8%8B.png" alt="image-20230406172433985" style="zoom:67%;" />

### 节点 加入/退出 流程

<img src="http://rs4h5afyg.hn-bkt.clouddn.com/view/cluster%E5%8A%A0%E5%85%A5%E4%B8%8E%E9%80%80%E5%87%BA%E6%B5%81%E7%A8%8B.png" alt="image-20230406172433985" style="zoom:67%;" />

## 项目示例



<img src="http://rs4h5afyg.hn-bkt.clouddn.com/view/image-20230406172433985.png" alt="image-20230406172433985" />

<img src="http://rs4h5afyg.hn-bkt.clouddn.com/view/Snipaste_2023-04-06_18-37-15.png" alt="image-20230406172433985" />

<img src="http://rs4h5afyg.hn-bkt.clouddn.com/view/QQ%E5%9B%BE%E7%89%8720230406183626.png" alt="image-20230406172433985" />

<img src="http://rs4h5afyg.hn-bkt.clouddn.com/view/%E7%9B%91%E5%90%AC%E7%A4%BA%E4%BE%8B.png" alt="image-20230406172433985" />
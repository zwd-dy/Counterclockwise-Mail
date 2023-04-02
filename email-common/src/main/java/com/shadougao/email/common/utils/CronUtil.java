package com.shadougao.email.common.utils;

import cn.hutool.core.date.DateUtil;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * cron表达式转换
 */
public class CronUtil {

    public static String unixToCron(Long time){
        // 秒 分 时 日 月 ? 月-月
        // YYYY/MM/dd HH:mm:ss
        return DateUtil.format(new Date(time),"0 m HH d M ? YYYY-YYYY");
    }


}

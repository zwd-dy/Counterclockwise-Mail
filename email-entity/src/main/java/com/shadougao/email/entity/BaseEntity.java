package com.shadougao.email.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class BaseEntity implements Serializable {
    /*
    id
     */
    @Id
    @JsonProperty("_id")
    private String id = IdUtil.getSnowflakeNextIdStr();

    /*
    创建人
     */
    @JsonProperty("createBy")
    private String createBy;

    /*
    最后更新人
     */
    @JsonProperty("updateBy")
    private String updateBy;

    /*
    创建时间
     */
    @JsonProperty("create_time")
    private String create_time = DateUtil.formatDateTime(new Date());

    /*
    更新时间
     */
    @JsonProperty("update_time")
    private String update_time;


}

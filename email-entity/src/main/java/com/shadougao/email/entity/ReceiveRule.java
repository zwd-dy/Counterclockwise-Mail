package com.shadougao.email.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 收信规则类
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("t_receive_rule")
public class ReceiveRule extends MongoBaseEntity{
    /**
     * 规则描述
     */
    private String describe;
    /**
     * 条件
     */
    private List<Condition> conditions;
    /**
     * 当所有condition成立执行的操作类
     */
    private List<Execute> executes;
    /**
     * 开关   0、关    1、开
     */
    private Integer isOpen;
    /**
     * 用户id
     */
    private Long userId;

    @Getter
    @Setter
    public class Condition extends MongoBaseEntity{
        public static final Integer LOGIC_INCLUDE = 0; // 包含
        public static final Integer LOGIC_NO_INCLUDE = 1; // 不包含
        public static final Integer LOGIC_GT = 2; // 大于等于
        public static final Integer LOGIC_LT = 3; // 小于

        /**
         * 判断字段
         */
        private String field;
        /**
         * 判断逻辑，0、包含    1、不包含   2、大于等于  3、小于
         */
        private Integer judgeLogic;
        /**
         * 对比值
         */
        private String value;
    }

    @Getter
    @Setter
    public class Execute extends MongoBaseEntity{
        /**
         * 操作类型
         */
        private String type;
        /**
         * 执行参数
         */
        private Object param;
    }
}

package com.shadougao.email.rule;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.ReceiveRule;
import com.shadougao.email.rule.execute.RuleExecute;
import com.shadougao.email.rule.execute.annotation.RuleExecuteType;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * 收件规则执行Service
 */
public class ReceiveRuleExecutor {
    private Mail mail;
    private ExecuteMap executeMap;



    public ReceiveRuleExecutor() {
        executeMap = GetBeanUtil.getApplicationContext().getBean(ExecuteMap.class);
    }

    public void execute(List<ReceiveRule.Condition> conditions, List<ReceiveRule.Execute> executes) {
        if(!conditionFilter(conditions)){
            return;
        }
        // 执行
        for (int i = 0; i < executes.size(); i++) {
            ReceiveRule.Execute execute = executes.get(i);
            RuleExecute ruleExecute = executeMap.get(execute.getType());
            ruleExecute.execute(mail,execute);
        }
    }


    /**
     * 条件判断
     * @param conditions
     * @return
     */
    private boolean conditionFilter(List<ReceiveRule.Condition> conditions) {

        for (int i = 0; i < conditions.size(); i++) {
            ReceiveRule.Condition condition = conditions.get(i);
            String fieldValue;
            try {
                Field field = this.mail.getClass().getDeclaredField(condition.getField());
                field.setAccessible(true);
                Object o = field.get(this.mail);
                fieldValue = String.valueOf(o);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Integer logic = condition.getJudgeLogic();

            if (logic == ReceiveRule.Condition.LOGIC_INCLUDE) {             // 包含
                return fieldValue.indexOf(condition.getValue()) != -1;
            } else if (logic == ReceiveRule.Condition.LOGIC_NO_INCLUDE) {   // 不包含
                return fieldValue.indexOf(condition.getValue()) == -1;
            } else if (logic == ReceiveRule.Condition.LOGIC_GT) {           // 大于等于
                return Double.parseDouble(fieldValue) >= Double.parseDouble(condition.getValue());
            } else if (logic == ReceiveRule.Condition.LOGIC_LT) {           // 小于
                return Double.parseDouble(fieldValue) < Double.parseDouble(condition.getValue());
            }
        }

        return false;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }
}

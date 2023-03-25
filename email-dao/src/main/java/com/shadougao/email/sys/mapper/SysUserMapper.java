package com.shadougao.email.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shadougao.email.entity.SysUser;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser findByUsername(@Param("username") String username);
}

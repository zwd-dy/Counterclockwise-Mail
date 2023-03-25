package com.shadougao.email.dao.mysql;

import com.shadougao.email.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    SysUser findByUsername(String username);

    SysUser findByUsernameOrEmail(String username, String email);
}

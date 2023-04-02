package com.shadougao.email.service.impl;

import cn.hutool.core.lang.Validator;
import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.dao.mongo.AddressBookDao;
import com.shadougao.email.dao.mongo.AddressBookGroupDao;
import com.shadougao.email.entity.AddressBook;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService {

    @Autowired
    private AddressBookDao addressBookDao;
    @Autowired
    private AddressBookGroupDao groupDao;

    /**
     * 添加联系人到通讯录
     *
     * @param addressBook
     * @return
     */
    @Override
    public Result addContact(AddressBook addressBook) {
        SysUser user = SecurityUtils.getCurrentUser();

        // 验证邮箱格式
        if (!Validator.isEmail(addressBook.getEmailAddress())) {
            throw new BadRequestException("邮箱格式有误，请检查！");
        }
        // 判断是否重复添加
        if (!Objects.isNull(addressBookDao.getOneByEmailUser(user.getId(), addressBook.getEmailAddress()))) {
            throw new BadRequestException("该联系人已在通讯录中，请勿重复添加");
        }

        // 完成添加
        addressBook.setUserId(user.getId());
        if (Objects.isNull(addressBookDao.addOne(addressBook))) {
            throw new BadRequestException("系统出错，请重新添加！");
        }
        return Result.success(addressBook);
    }

    @Override
    public Result delContact(String id) {
        SysUser user = SecurityUtils.getCurrentUser();

        // 判断联系人是否存在
        if (Objects.isNull(addressBookDao.getOneById(id))) {
            throw new BadRequestException("联系人不存在");
        }

        // 移除联系人
        addressBookDao.delOne(id);
        return Result.success();
    }

    @Override
    public Result addToGroup(String id, String groupId) {
        AddressBook contact = addressBookDao.getOneById(id);
        // 查看联系人是否存在
        if (Objects.isNull(contact)) {
            throw new BadRequestException("联系人不存在");
        }
        // 查看分组是否存在
        if (Objects.isNull(groupDao.getOneById(groupId))) {
            throw new BadRequestException("该分组不存在");
        }
        // 添加到分组
        contact.setGroupId(groupId);
        if (addressBookDao.updateOne(contact) != 1) {
            throw new BadRequestException("添加到分组失败，请重新尝试");
        }
        return Result.success();
    }

    @Override
    public Result delToGroup(String id) {
        AddressBook contact = addressBookDao.getOneById(id);
        // 查看联系人是否存在
        if (Objects.isNull(contact)) {
            throw new BadRequestException("联系人不存在");
        }

        // groupId为0代表没分组
        contact.setGroupId("0");
        if (addressBookDao.updateOne(contact) != 1) {
            throw new BadRequestException("操作异常，请重新尝试");
        }
        return Result.success();
    }
}

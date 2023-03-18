package com.shadougao.email.web.controller;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.entity.AddressBook;
import com.shadougao.email.entity.AddressBookGroup;
import com.shadougao.email.entity.dto.PageData;
import com.shadougao.email.service.AddressBookGroupService;
import com.shadougao.email.service.AddressBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor

public class AddressBookController {

    private final AddressBookService addressBookService;
    private final AddressBookGroupService groupService;

    /**
     * 分页查询联系人
     *
     * @param pageData
     * @param addressBook
     * @return
     */
    @GetMapping("/pageList")
    public Result pageList(PageData pageData, AddressBook addressBook) {
        return Result.success(addressBookService.pageList(pageData, addressBook));
    }

    /**
     * 添加联系人到通讯录
     *
     * @param addressBook
     * @return
     */
    @PostMapping("/addContact")
    public Result addContact(@RequestBody AddressBook addressBook) {
        return addressBookService.addContact(addressBook);
    }

    /**
     * 将联系人从通讯录中删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delContact(@PathVariable("id") String id) {
        return addressBookService.delContact(id);
    }

    /**
     * 分组列表
     * @return
     */
    @GetMapping("/group/list")
    public Result listGroup() {
        return Result.success(groupService.getBaseMapper().find(
                new Query()
                        .addCriteria(Criteria
                                .where("userId")
                                .is(SecurityUtils.getCurrentUser().getId()
                                ))));
    }

    /**
     * 添加分组
     *
     * @param group
     * @return
     */
    @PostMapping("/addGroup")
    public Result addGroup(@RequestBody AddressBookGroup group) {
        return groupService.addGroup(group);
    }

    /**
     * 删除分组
     *
     * @param id
     * @return
     */
    @DeleteMapping("/group/delete/{id}")
    public Result deleteGroup(@PathVariable String id) {
        return groupService.delGroup(id);
    }

    /**
     * 添加联系人到分组
     *
     * @param id
     * @param groupId
     * @return
     */
    @PostMapping("/addToGroup/{id}/{groupId}")
    public Result addToGroup(@PathVariable("id") String id, @PathVariable("groupId") String groupId) {
        return addressBookService.addToGroup(id, groupId);
    }

    /**
     * 将联系人从分组中删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delToGroup/{id}")
    public Result delToGroup(@PathVariable("id") String id) {
        return addressBookService.delToGroup(id);
    }

}

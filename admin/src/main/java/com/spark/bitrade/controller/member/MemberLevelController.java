package com.spark.bitrade.controller.member;

import com.spark.bitrade.annotation.AccessLog;
import com.spark.bitrade.constant.AdminModule;
import com.spark.bitrade.controller.common.BaseAdminController;
import com.spark.bitrade.entity.MemberLevel;
import com.spark.bitrade.service.MemberLevelService;
import com.spark.bitrade.util.BindingResultUtil;
import com.spark.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author rongyu
 * @description 会员等级管理类
 * @date 2017/12/27 10:16
 */
@RestController
@Slf4j
@RequestMapping("member/member-level")
public class MemberLevelController extends BaseAdminController {

    @Autowired
    private MemberLevelService memberLevelService;

    @RequiresPermissions("member:member-level:all")
    @PostMapping("all")
    @AccessLog(module = AdminModule.MEMBER, operation = "所有会员等级MemberLevel")
    public MessageResult findAll() {
        List<MemberLevel> memberLevels = memberLevelService.findAll();
        MessageResult messageResult = success();
        messageResult.setData(memberLevels);
        return messageResult;
    }

    @RequiresPermissions("member:member-level:update")
    @PostMapping("update")
    @AccessLog(module = AdminModule.MEMBER, operation = "更新会员等级MemberLevel")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult update(@Valid MemberLevel memberLevel, BindingResult bindingResult) throws Exception {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null)
            return result;
        if (memberLevel.getId() == null)
            return error("主键不得为空");
        MemberLevel one = memberLevelService.findOne(memberLevel.getId());
        if (one == null)
            return error("修改对象不存在");
        if (memberLevel.getIsDefault() && !one.getIsDefault())
            //修改对象为默认 原本为false 则 修改默认的等级的isDefault为false
            memberLevelService.updateDefault();
        MemberLevel save = memberLevelService.save(memberLevel);
        return success(save);
    }

}

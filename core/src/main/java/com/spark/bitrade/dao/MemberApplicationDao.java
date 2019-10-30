package com.spark.bitrade.dao;

import com.spark.bitrade.constant.AuditStatus;
import com.spark.bitrade.dao.base.BaseDao;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberApplication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author rongyu
 * @description
 * @date 2017/12/26 15:12
 */
public interface MemberApplicationDao extends BaseDao<MemberApplication> {
    List<MemberApplication> findMemberApplicationByMemberAndAuditStatusOrderByIdDesc(Member var1, AuditStatus var2);

    long countAllByAuditStatus(AuditStatus auditStatus);

    @Query("select a from MemberApplication a where a.idCard=:idCard and (a.auditStatus=:sta1 or a.auditStatus=:sta2) ")
    List<MemberApplication> findSuccessMemberApplicationsByIdCard(@Param("idCard") String idCard, @Param("sta1") AuditStatus sta1, @Param("sta2") AuditStatus sta2);

    List<MemberApplication> findByMemberAndAuditStatus(Member member,AuditStatus auditStatus);
}

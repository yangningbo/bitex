package com.spark.bitrade.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.CertifiedBusinessStatus;
import com.spark.bitrade.constant.CommonStatus;
import com.spark.bitrade.constant.PageModel;
import com.spark.bitrade.dao.MemberDao;
import com.spark.bitrade.dao.MemberSignRecordDao;
import com.spark.bitrade.dao.MemberTransactionDao;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.exception.AuthenticationException;
import com.spark.bitrade.pagination.Criteria;
import com.spark.bitrade.pagination.PageResult;
import com.spark.bitrade.pagination.Restrictions;
import com.spark.bitrade.service.Base.BaseService;
import com.spark.bitrade.util.BigDecimalUtils;
import com.spark.bitrade.util.Md5;
import com.spark.bitrade.vo.ChannelVO;
import com.sparkframework.sql.DB;
import com.sparkframework.sql.DataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.spark.bitrade.constant.TransactionType.ACTIVITY_AWARD;

@Slf4j
@Service
public class MemberService extends BaseService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private MemberSignRecordDao signRecordDao;

    @Autowired
    private MemberTransactionDao transactionDao;

    /**
     * 条件查询对象 pageNo pageSize 同时传时分页
     *
     * @param booleanExpressionList
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult<Member> queryWhereOrPage(List<BooleanExpression> booleanExpressionList, Integer pageNo, Integer pageSize) {
        List<Member> list;
        JPAQuery<Member> jpaQuery = queryFactory.selectFrom(QMember.member)
                .where(booleanExpressionList.toArray(new BooleanExpression[booleanExpressionList.size()]));
        jpaQuery.orderBy(QMember.member.id.desc());
        if (pageNo != null && pageSize != null) {
            list = jpaQuery.offset((pageNo - 1) * pageSize).limit(pageSize).fetch();
        } else {
            list = jpaQuery.fetch();
        }
        return new PageResult<>(list, jpaQuery.fetchCount());
    }

    public Member save(Member member) {
        return memberDao.saveAndFlush(member);
    }

    public List<Member> findAll(Predicate predicate){
        //return Collections.;
        Iterable<Member> iterable = memberDao.findAll(predicate);
        List<Member> list = IteratorUtils.toList(iterable.iterator());
        return list ;
    }

    public Member saveAndFlush(Member member) {
        return memberDao.saveAndFlush(member);
    }

    @Transactional(rollbackFor = Exception.class)
    public Member loginWithToken(String token, String ip, String device) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        //Member mr = memberDao.findMemberByTokenAndTokenExpireTimeAfter(token,new Date());
        Member mr = memberDao.findMemberByToken(token);
        return mr;
    }

    public Member login(String username, String password) throws Exception {
        Member member = memberDao.findMemberByMobilePhoneOrEmail(username, username);
        if (member == null) {
            throw new AuthenticationException("账号或密码错误");
        } else if (member.getLoginLock()!=null&&member.getLoginLock().equals(BooleanEnum.IS_TRUE)){
            throw new AuthenticationException("多次输入错误的密码，账号已锁定，请联系客服解锁或找回密码解锁");
        } else if (!Md5.md5Digest(password + member.getSalt()).toLowerCase().equals(member.getPassword())) {
            log.info("账号或密码错误");
            return null;
        } else if (member.getStatus().equals(CommonStatus.ILLEGAL)) {
            throw new AuthenticationException("该帐号已经被禁用，请联系客服");
        }
        return member;
    }

    /**
     * @author rongyu
     * @description
     * @date 2017/12/25 18:42
     */
    public Member findOne(Long id) {
        return memberDao.findOne(id);
    }

    /**
     * @author rongyu
     * @description 查询所有会员
     * @date 2017/12/25 18:43
     */
    public List<Member> findAll() {
        return memberDao.findAll();
    }



    /**
     * 查询会员总数
     * @return
     */
    public long count(){
        return memberDao.count();
    }

    public Page<Member> findAll(Predicate predicate, PageModel pageModel){
        return memberDao.findAll(predicate,pageModel.getPageable());
    }

    public List<Member> findPromotionMember(Long id) {
        return memberDao.findAllByInviterId(id);
    }

    /**
     * @author rongyu
     * @description 分页
     * @date 2018/1/12 15:35
     */
    public Page<Member> page(Integer pageNo, Integer pageSize, CommonStatus status) {
        //排序方式 (需要倒序 这样    Criteria.sort("id","createTime.desc") ) //参数实体类为字段名
        Sort orders = Criteria.sortStatic("id");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        //查询条件
        Criteria<Member> specification = new Criteria<Member>();
        specification.add(Restrictions.eq("status", status, false));
        return memberDao.findAll(specification, pageRequest);
    }

    public Page<Member> page(Integer pageNo, Integer pageSize) {
        //排序方式 (需要倒序 这样    Criteria.sort("id","createTime.desc") ) //参数实体类为字段名
        Sort orders = Criteria.sortStatic("id");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        return memberDao.findAll(pageRequest);
    }

    public List<Map<String, String>> getEmptyAddressMember(String coinName) throws DataException, SQLException {
        String sql = "select a.id as memberId,b.id as walletId,b.address from member a \n" +
                "left join member_wallet b on a.id=b.member_id \n" +
                "where b.address = '' and b.coin_id='"+coinName+"'";
        List<Map<String, String>> list = DB.query(sql);
        return list;
    }

    public boolean emailIsExist(String email) {
        List<Member> list = memberDao.getAllByEmailEquals(email);
        return list.size() > 0 ? true : false;
    }

    public boolean usernameIsExist(String username) {
        return memberDao.getAllByUsernameEquals(username).size() > 0 ? true : false;
    }

    public boolean phoneIsExist(String phone) {
        return memberDao.getAllByMobilePhoneEquals(phone).size() > 0 ? true : false;
    }

    public Member findByUsername(String username) {
        return memberDao.findByUsername(username);
    }

    public Member findByEmail(String email) {
        return memberDao.findMemberByEmail(email);
    }

    public Member findByPhone(String phone) {
        return memberDao.findMemberByMobilePhone(phone);
    }

    public Page<Member> findAll(Predicate predicate, Pageable pageable) {
        return memberDao.findAll(predicate, pageable);
    }

    public String findUserNameById(long id) {
        return memberDao.findUserNameById(id);
    }

    //签到事件
    @Transactional(rollbackFor = Exception.class)
    public void signInIncident(Member member, MemberWallet memberWallet, Sign sign) {
        member.setSignInAbility(false);//失去签到能力
        memberWallet.setBalance(BigDecimalUtils.add(memberWallet.getBalance(), sign.getAmount()));//签到收益

        // 签到记录
        signRecordDao.save(new MemberSignRecord(member, sign));
        //账单明细
        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setMemberId(member.getId());
        memberTransaction.setAmount(sign.getAmount());
        memberTransaction.setType(ACTIVITY_AWARD);
        memberTransaction.setSymbol(sign.getCoin().getUnit());
        transactionDao.save(memberTransaction);
    }

    //重置会员签到
    public void resetSignIn() {
        memberDao.resetSignIn();
    }

    public void updateCertifiedBusinessStatusByIdList(List<Long> idList) {
        memberDao.updateCertifiedBusinessStatusByIdList(idList, CertifiedBusinessStatus.DEPOSIT_LESS);
    }

    public List<ChannelVO> getChannelCount(List<Long> memberIds){
        List<Object[]> list=memberDao.getChannelCount(memberIds);
        List<ChannelVO> channelVOList=new ArrayList<>();
        if(list!=null&&list.size()>0){
            for(Object[] objs:list){
                Number memberId=(Number)objs[0];
                Number channelCount=(Number)objs[1];
                Number channelReward=(Number)objs[2];
                ChannelVO channelVO=new ChannelVO(memberId.longValue(),channelCount.intValue(),new BigDecimal(channelReward.doubleValue()));
                channelVOList.add(channelVO);
            }
        }
        return channelVOList;
    }

    public void lock(String username){
        memberDao.updateLoginLock(username,BooleanEnum.IS_TRUE);
    }

    public void unLock(String username){
        memberDao.updateLoginLock(username,BooleanEnum.IS_FALSE);
    }
}

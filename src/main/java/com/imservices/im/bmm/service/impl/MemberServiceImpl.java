package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.*;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.WebConfigService;
import com.imservices.im.bmm.utils.NumberUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Administrator
 *
 */
@Service
@SuppressWarnings("all")
@AllArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
	
	private BaseService baseService;
	private WebConfigService configService;
	

	
	
	@Transactional(readOnly = true)
	public Member get(String id) throws Exception {
		return (Member) baseService.get(Member.class, id);
	}

	@Transactional(readOnly = true)
	public Member get(String propertyName, Object value) throws Exception {
		return (Member) baseService.get(Member.class,propertyName ,value);
	}

	@Override
	public List<Member> getLike(String propertyName, Object value) throws Exception {
		return baseService.getLike(Member.class, propertyName, value);
	}

	@Transactional(readOnly = true)
	public List<Member> getList(String[] properties, Object[] vals)
			throws Exception {
		return baseService.getList(Member.class, properties, vals);
	}

	@Transactional
	public void update(Member member) throws Exception  {
		baseService.update(member);
	}

	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(Member.class, properties, vals, Pager);
	}

	
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(Member.class, id);
	}
 
	
	@Transactional(readOnly = true)
	public Long count(String[] properties, Object[] values) throws Exception {
		return baseService.getTotalCount(Member.class, properties, values);
	}
 

	
	@Transactional
	public void save(Member member) throws Exception {
		baseService.save(member);
	}

	
	@Transactional
	public void delete(Member member) throws Exception {
		baseService.delete(member);
	}


	public synchronized String generateMemberId() throws Exception {
		WebConfig wc = configService.get();
		return NumberUtils.generateRandomNumber(wc.getMember_id_len()); 
	}

	
	@Transactional(readOnly = true)
	public List<Friends> getFriendsList(String[] ps, Object[] vs)
			throws Exception {
		return baseService.getList(Friends.class, ps, vs);
	}

	
	@Transactional(readOnly = true)
	public List<FriendsAdd> getFriendsAddList(String[] ps, Object[] vs)
			throws Exception {
		return baseService.getList(FriendsAdd.class, ps, vs);
	}

	
	@Transactional
	public void saveFriendAdd(FriendsAdd friend_add) throws Exception {
		baseService.save(friend_add);
	}
  
	
	@Transactional(readOnly = true)
	public Long friendsAddCount(String[] ps, Object[] vs) throws Exception {
		return baseService.getTotalCount(FriendsAdd.class, ps, vs);
	}

	
	@Transactional(readOnly = true)
	public Long friendsCount(String[] ps, Object[] vs) throws Exception {
		return baseService.getTotalCount(Friends.class, ps, vs); 
	}
  
	
	@Transactional(readOnly = true)
	public FriendsAdd getFriendsAdd(String id) throws Exception {
		return (FriendsAdd) baseService.get(FriendsAdd.class, id);
	}

	
	@Transactional
	public void updateFriendsAdd(FriendsAdd fa) throws Exception {
		baseService.update(fa);
	}

	
	@Transactional
	public void saveFriends(Friends firends) throws Exception {
		baseService.save(firends);
	}


	@Transactional
	public void deleteByHql(String hql) throws Exception {
		baseService.delete(hql);
	}

	
	@Transactional
	public void saveLoginLog(MemberLoginLog log) throws Exception {
		baseService.save(log); 
	}

	
	@Transactional
	public void deleteFriends(Friends o) throws Exception {
		baseService.delete(o);
	}

	
	@Transactional
	public void deleteFriendsAdd(FriendsAdd o) throws Exception {
		baseService.delete(o);
	}

	
	@Transactional(readOnly = true)
	public Long count(Class cls, String[] ps, Object[] vs) throws Exception {
		return baseService.getTotalCount(cls, ps, vs);
	}


	
	@Transactional
	public void saveBlackList(Blacklist o) throws Exception {
		baseService.save(o);
	}

	
	@Transactional
	public void updateBlackList(Blacklist o) throws Exception {
		baseService.update(o);
	}
	
	@Transactional
	
	public List<Blacklist> getBlackList(String[] ps, Object[] vs) throws Exception {
		return baseService.getList(Blacklist.class, ps, vs);
	}


	@Transactional(readOnly = true)
	public MemberLoginLog getIP(String propertyName, Object value) throws Exception {
		return (MemberLoginLog) baseService.getIP(MemberLoginLog.class,propertyName ,value);
	}

	@Override
	public void clearMember() {
		baseService.callProcedure("clearMember");
	}

}

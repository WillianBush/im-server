package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.*;

import java.util.List;

public interface MemberService {
	

	public Member get(String id) throws Exception;
    public Member get(String propertyName, Object value) throws Exception;
	public List<Member> getLike(String propertyName, Object value) throws Exception;
    public List<Member> getList( String[] properties, Object[] vals) throws Exception;
    public void update(Member member) throws Exception ;
	public Pager findByPager(String[] properties, Object[] vals, Pager pager) throws Exception ;
	public void delete(String id)  throws Exception;
	public Long count(String[] properties, Object[] values) throws Exception;
	public void save(Member member) throws Exception;
	public void delete(Member member) throws Exception;

	public  String generateMemberId() throws Exception;
	public List<Friends> getFriendsList(String[] ps, Object[] vs) throws Exception;
	public List<FriendsAdd> getFriendsAddList(String[] ps, Object[] vs) throws Exception;
	public void saveFriendAdd(FriendsAdd friend_add) throws Exception;
	public Long friendsAddCount(String[] ps, Object[] vs)  throws Exception;
	public Long friendsCount(String[] ps, Object[] vs) throws Exception;
	public FriendsAdd getFriendsAdd(String id) throws Exception;
	public void updateFriendsAdd(FriendsAdd fa) throws Exception;
	public void saveFriends(Friends firends) throws Exception;
	public void deleteByHql(String hql) throws Exception;
	public void saveLoginLog(MemberLoginLog log) throws Exception;
	public void deleteFriends(Friends o) throws Exception;
	public void deleteFriendsAdd(FriendsAdd o) throws Exception;
	public Long count(Class cls, String[] ps,Object[] vs) throws Exception;

	public void saveBlackList(Blacklist o) throws Exception;
	public void updateBlackList(Blacklist o) throws Exception;
	public List<Blacklist> getBlackList(String[] ps, Object[] vs)  throws Exception;
	public MemberLoginLog getIP(String propertyName, Object value) throws Exception;

	/***
	 * 清理垃圾数据
	 */
    public void clearMember();
}
 
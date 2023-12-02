package com.imservices.im.bmm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.bean.AccessRecordBean;
import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.bean.store.ChatStore;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.entity.AccessRecord;
import com.imservices.im.bmm.service.AccessRecordService;
import com.imservices.im.bmm.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AccessRecordServiceImpl implements AccessRecordService {

	@Resource
	private BaseService<AccessRecord, String> baseService;

	@Resource
	private StoreComponent storeComponent;

	
	@Override
	@Transactional(readOnly = true)
	public List<AccessRecord> getList(String[] properties, Object[] vals)
			throws Exception {
		return baseService.getList(AccessRecord.class, properties, vals);
	}

	@Override
	@Transactional
	public void update(AccessRecord ar) throws Exception {
		ar.setModifyDate(new Date());
		if (ar.getCreateDate() != null){
			ar.setCreateDate(new Date());
		}
		baseService.update(ar);
	}

	@Override
	@Transactional(readOnly = true)
	public Pager<AccessRecord> findByPager(String[] properties, Object[] vals, Pager<AccessRecord> pager)
			throws Exception {
		return baseService.findByPager(AccessRecord.class, properties, vals, pager);
	}

	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Long count(String[] properties, Object[] vals) throws Exception {
		return baseService.getTotalCount(AccessRecord.class, properties, vals);
	}

	@Override
	@Transactional
	public void save(AccessRecord ar) throws Exception {
		ar.setModifyDate(new Date());
		ar.setCreateDate(new Date());
		baseService.save(ar);
	}

	@Override
	@Transactional
	public void delete(AccessRecord ar) throws Exception {
		baseService.delete(ar);
	}

	@Override
	@Transactional
	public void updateHeadpic(String eid, String headpic) throws Exception {
		baseService.update("update AccessRecord as u set u.img='"+headpic+"' where u.entityid='"+eid+"'");
	}

	@Override
	@Transactional
	public void updateByEid(String eid, String[] ps, String[] vs)
			throws Exception {
		if(ArrayUtils.isEmpty(ps)||ArrayUtils.isEmpty(vs)) return;

		for (String v: vs) {
			if (v.contains("createDate") || v.contains("modifyDate") ){
				throw new Exception("内容异常");
				// TODO ip拉黑
			}
		}

		String str = "";
		for(int i=0;i<ps.length;i++) {
			str+=("u."+ps[i]+"='"+vs[i].toString()+"'");
			if(i<ps.length-1) str +=",";
		}
		baseService.update("update AccessRecord as u set "+str+" where u.entityid='"+eid+"'");
	}

	@Override
	public AccessRecord get(String id) throws Exception {
		return baseService.get(AccessRecord.class, id);
	}

	@Override
	@Transactional
	public void deleteByHql(String hql) throws Exception {
		baseService.delete(hql);
	}

	@Override
	public AccessRecordBean getAccessRecordBean(AccessRecord e) {

		SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yy/M/dd");
		Date now = new Date();
		Date yesterday = new Date();
		yesterday.setDate(yesterday.getDate()-1);

		if("1".equals(e.getTypeid())) {
			//群
			RoomBean roomBean = ChatStore.ROOMB_BEAN_MAP.get(e.getEntityid());

		}
		try {
			AccessRecordBean o = new AccessRecordBean();
			if ("2".equals(e.getTypeid())) {
				//检查用户是否在线
				if (storeComponent.isMemberOnline(e.getEntityid())) {
					o.setOnline(1);
				} else {
					MemberBean mb = storeComponent.getMemberBeanFromMapDB(e.getEntityid());
					if (null == mb) {
						log.error("AccessRecord:{}", JSONObject.toJSONString(e));
						this.delete(e);
						return null;
					}
					String str = "";
					if (null != mb.getLastLoginDate()) {
						Long l = new Date().getTime() - mb.getLastLoginDate().getTime();
						long day = l / (24 * 60 * 60 * 1000);
						long hour = (l / (60 * 60 * 1000) - day * 24);
						long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
						long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
//				        //
						if (day > 0) {
							str = day + "天前";
						} else if (hour > 0) {
							str = hour + "小时前";
						} else if (min > 0) {
							str = min + "分钟前";
						} else if (s > 0) {
							str = s + "秒前";
						}
					}

					//如果不在线则需要显示上次在线时间
					o.setLastLoginDate(str);
				}
			}
			o.setArid(e.getId());
			o.setContent("");
			o.setId(e.getEntityid());
			o.setTypeid(e.getTypeid());
			o.setImg(e.getImg());
			o.setTitle(e.getTitle());
			Date d = e.getCreateDate();
			if (d != null) {
				if (d.getYear() == now.getYear() && d.getMonth() == now.getMonth() && d.getDate() == now.getDate()) {
					o.setCreateDate(sdf1.format(d));
				} else if (d.getDate() == yesterday.getDate()) {
					o.setCreateDate("昨天");
				} else {
					o.setCreateDate(sdf2.format(d));
				}
				o.setCreateDateTime(e.getCreateDate().getTime());
			}

			o.setContent("");
			o.setSubname(e.getSubname());
			return o;
		}catch (Exception ex) {
			log.error("getAccessRecord :{}",e,ex);
			return null;
		}
	}


}

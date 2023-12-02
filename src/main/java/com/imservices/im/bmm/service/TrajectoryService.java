package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Trajectory;

import java.util.List;

public interface TrajectoryService {  

	public abstract Pager findByPager(String[] properties, Object[] vals, Pager Pager ) throws Exception;
	public abstract void generate(MemberBean mb,String descri) throws Exception;
	public abstract List<Trajectory> getList(String[] ps, Object[] vs) throws Exception;
}

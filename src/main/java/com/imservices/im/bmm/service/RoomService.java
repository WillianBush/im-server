package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Room;
import com.imservices.im.bmm.entity.RoomAdd;

import java.util.List;

public interface RoomService {

	public Room get(String id) throws Exception;
    public Room get(String propertyName, Object value) throws Exception;
    public List<Room> getList( String[] properties, Object[] vals) throws Exception;
    public void update(Room room) throws Exception ;
	public Pager findByPager(String[] properties, Object[] vals, Pager pager) throws Exception ;
	public void delete(String id)  throws Exception;
	public Long count(String[] properties, Object[] values) throws Exception;
	public void save(Room room) throws Exception;
	public void delete(Room room) throws Exception;
	public List<Room> getAll() throws Exception;
	public Long getTotalCount(String[] properties, Object[] vals) throws Exception;
	public Long roomAddCount(String[] ps, Object[] vs) throws Exception;
	public void saveRoomAdd(RoomAdd room_add) throws Exception;
	public List<RoomAdd> roomAddList(String[] ps, Object[] vs) throws Exception;
	public RoomAdd getRoomAdd(String raid) throws Exception;
	public void deleteRoomAdd(RoomAdd ra) throws Exception;

	/**
	 * 发送移除消息到被删除用户ws
	 * @param roomid
	 * @param mid
	 */
	void sendRemoveMsg(String roomid,String mid);
    
}
 
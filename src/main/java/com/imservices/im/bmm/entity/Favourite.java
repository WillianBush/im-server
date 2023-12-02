package com.imservices.im.bmm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name="favourite")
public class Favourite  extends BaseEntity {

	public enum FavouriteType {
		TxtBean
	}
	
	private FavouriteType ctype = FavouriteType.TxtBean;
	private String jsonstr;
	private String uid;
	
	
	
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	@Enumerated
	public FavouriteType getCtype() {
		return ctype;
	}
	public void setCtype(FavouriteType ctype) {
		this.ctype = ctype;
	}
	@Column(length=20000)
	public String getJsonstr() {
		return jsonstr;
	}
	public void setJsonstr(String jsonstr) {
		this.jsonstr = jsonstr;
	}
	
	
	
}

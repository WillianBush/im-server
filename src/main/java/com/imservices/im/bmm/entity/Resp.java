package com.imservices.im.bmm.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 页面响应entity
 * @author wang<fangyuan.co@outlook.com>
 */
@Data
@ApiModel
@AllArgsConstructor
public class Resp<T> {

	private static final long serialVersionUID = 1L;

	public static final int ok = 0;
	public static final int error = 500;

	@ApiModelProperty(value = "成功:0;失败:500")
	private Integer code;

	@ApiModelProperty(value = "message")
	private String msg;

	@ApiModelProperty(value = "实体类")
	private T data;

	@ApiModelProperty(value = "返回集合")
	private List<T> rows;


	public Resp() {

	}


	
	public static Resp error() {
		return error(error, "未知异常，请联系管理员");
	}

	public static Resp ok() {
		return  Resp.ok(ok,"成功");
	}

	public static <E> Resp<E> ok(E data) {
		return  Resp.ok(ok,"成功",data);
	}

	public static <E> Resp<E> ok(List<E> dataRows) {
		return  Resp.ok(ok,"成功",dataRows);
	}

	public static <E> Resp<E> ok(E data,List<E> dataRows) {
		return  Resp.ok(ok,"成功",data,dataRows);
	}

	public static <E> Resp<E> ok(String msg,List<E> dataRows) {
		return  Resp.ok(ok,msg,dataRows);
	}

	public static <E> Resp<E> ok(String msg,E data,List<E> dataRows) {
		return  Resp.ok(ok,msg,data,dataRows);
	}

	public static <E> Resp<E> ok(String msg,E data) {
		return  Resp.ok(ok,msg,data);
	}
	
	public static Resp error(String msg) {
		return error(error, msg);
	}

	public static <E> Resp<E> error(E data) {
		return  Resp.error(error,"失败",data);
	}
	
	public static Resp error(int code, String msg) {
		Resp r = new Resp();
		r.code = code;
		r.msg =msg;
		return r;
	}

	public static Resp ok(int code, String msg) {
		Resp r = new Resp();
		r.code = code;
		r.msg =msg;
		return r;
	}


	public static <E> Resp ok(int code, String msg, E data) {
		Resp r = new Resp();
		r.code = code;
		r.msg =msg;
		r.data =data;
		return r;
	}


	public static <E> Resp error(int code, String msg, E data) {
		Resp r = new Resp();
		r.code = code;
		r.msg =msg;
		r.data =data;
		return r;
	}

	public static <E> Resp ok(int code, String msg,List<E> dataRows) {
		Resp r = new Resp();
		r.code = code;
		r.msg =msg;
		r.rows = dataRows;
		return r;
	}

	public static <E> Resp ok(int code, String msg, E data,List<E> dataRows) {
		Resp r = new Resp();
		r.code = code;
		r.msg =msg;
		r.data =data;
		r.rows = dataRows;
		return r;
	}


	public static Resp ok(String msg) {
		Resp r = new Resp();
		r.code = ok;
		r.msg =msg;
		return r;
	}

	


}
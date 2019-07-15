package com.ewhale.common.pageutil;

import java.io.Serializable;

/**
 * 查询条件分页对象封装
 *
 * @author Tanjb
 */
@SuppressWarnings("serial")
public class Querys<T> implements Serializable{

	private T object;// 类实体对象

	private int page;// 当前页码

	private int size;// 分页大小

	private String startTime;// 开始时间

	private String endTime;// 结束时间
	
	private int skip;
	
	private int type ; //1:跨境电商  2:一般进口
	
	private int asc ; // 升序
	
	private int desc; // 降序

	public Querys() {
		super();
	}

	/**
	 * 公共查询
	 * @param object
	 * @param page
	 * @param startTime
	 * @param endTime
	 */
	public Querys(T object, int page, String startTime, String endTime) {
		this.object = object;
		this.page = page;
		this.startTime = startTime;
		this.endTime = endTime;

	}

	/**
	 * 用码统计特殊查询
	 * @param object
	 * @param page
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @param asc
	 * @param desc
	 */
	public Querys(T object, int page, String startTime, String endTime,int type,int asc,int desc){
		this.object = object;
		this.page = page;
		this.startTime = startTime;
		this.endTime = endTime;
		this.type = type ;
		this.asc = asc ;
		this.desc = desc ;
	}
	
	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAsc() {
		return asc;
	}

	public void setAsc(int asc) {
		this.asc = asc;
	}

	public int getDesc() {
		return desc;
	}

	public void setDesc(int desc) {
		this.desc = desc;
	}
	
	

}

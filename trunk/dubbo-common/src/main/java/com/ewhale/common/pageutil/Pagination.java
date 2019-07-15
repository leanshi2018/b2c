package com.ewhale.common.pageutil;

import java.io.Serializable;
import java.util.Collection;

/**
 * 分页对象
 *
 * @author linwh
 */
@SuppressWarnings("serial")
public class Pagination<T> implements Serializable  {
	
	private int count;//总数
	
	private int page;//当前页码
	
	private int size;//分页大小
	
	private int pageCount;//页码总数
	
	private Collection<T> items;//数据列表
	
	private int itemCount;//当前页总数
	
	public Pagination(){
		super();
	}
	
	public Pagination(int count, int page, int size, Collection<T> items){
		
		this.count = count;
		this.page = page;
		this.size = size;
		this.pageCount = count % size != 0 ? count / size + 1 : count / size;
		this.items = items;
		
		if(items != null){
			itemCount = items.size();
		}else{
			itemCount = 0;
		}
	}


	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public Collection<T> getItems() {
		return items;
	}

	public void setItems(Collection<T> items) {
		this.items = items;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

}

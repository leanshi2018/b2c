package com.ewhale.common.pageutil;

import java.io.Serializable;

/**
 * Mysql文本导入路径对象
 *
 * @author Tanjb
 */
@SuppressWarnings("serial")
public class PathSql implements Serializable{

	private String path; //文本路径
	
	public PathSql(){
		super();
	}
	
	public PathSql(String path){
		this.path = path ;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}

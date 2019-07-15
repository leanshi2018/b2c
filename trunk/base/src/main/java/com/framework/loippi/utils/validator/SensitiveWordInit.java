package com.framework.loippi.utils.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @Description: 初始化敏感词库，将敏感词加入到HashMap中，构建DFA算法模型
 * @Author : KVIUFF
 * @Date : 2015年9月29日 上午11:05:06
 * @version 1.0
 */
public class SensitiveWordInit {
	private String ENCODING = "UTF-8";    //字符编码
	@SuppressWarnings("rawtypes")
	public HashMap sensitiveWordMap;
	
	public SensitiveWordInit(){
		super();
	}
	
	/**
	 * @author KVIUFF 
	 * @date 2015年9月29日 上午11:05:06
	 * @version 1.0
	 */
	@SuppressWarnings("rawtypes")
	public Map initKeyWord(String filePath){
		try {
			//读取敏感词库
			File file = new File(filePath);
			if(file.isDirectory()){
				File[] tempList = file.listFiles();
				if(tempList.length > 0){
					for (File file2 : tempList) {
						Set<String> keyWordSet = readSensitiveWordFile(file2.getPath());
						//将敏感词库加入到HashMap中
						addSensitiveWordToHashMap(keyWordSet);
					}
				}else{
					sensitiveWordMap = new HashMap();
				}
			}
			//spring获取application，然后application.setAttribute("sensitiveWordMap",sensitiveWordMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sensitiveWordMap;
	}
	
	
	/**
	 * @author chenming 
	 * @date 2014年4月20日 下午2:28:32
	 * @version 1.0
	 */
	@SuppressWarnings("rawtypes")
	public Map initKeyWord(Map keyWordSet){
		try {
			//读取敏感词库

			//将敏感词库加入到HashMap中
			addSensitiveWordToHashMap(keyWordSet);
			//spring获取application，然后application.setAttribute("sensitiveWordMap",sensitiveWordMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sensitiveWordMap;
	}

	/**
	 * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：<br>
	 * 中 = {
	 *      isEnd = 0
	 *      国 = {<br>
	 *      	 isEnd = 1
	 *           人 = {isEnd = 0
	 *                民 = {isEnd = 1}
	 *                }
	 *           男  = {
	 *           	   isEnd = 0
	 *           		人 = {
	 *           			 isEnd = 1
	 *           			}
	 *           	}
	 *           }
	 *      }
	 *  五 = {
	 *      isEnd = 0
	 *      星 = {
	 *      	isEnd = 0
	 *      	红 = {
	 *              isEnd = 0
	 *              旗 = {
	 *                   isEnd = 1
	 *                  }
	 *              }
	 *      	}
	 *      }
	 * @author KVIUFF 
	 * @date 2015年9月29日 上午11:05:06
	 * @param keyWordSet  敏感词库
	 * @version 1.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
		sensitiveWordMap = new HashMap(keyWordSet.size());     //初始化敏感词容器，减少扩容操作
		String key = null;  
		Map nowMap = null;
		Map<String, String> newWorMap = null;
		//迭代keyWordSet
		Iterator<String> iterator = keyWordSet.iterator();
		while(iterator.hasNext()){
			key = iterator.next();    //关键字
			nowMap = sensitiveWordMap;
			for(int i = 0 ; i < key.length() ; i++){
				char keyChar = key.charAt(i);       //转换成char型
				Object wordMap = nowMap.get(keyChar);       //获取
				
				if(wordMap != null){        //如果存在该key，直接赋值
					nowMap = (Map) wordMap;
				}
				else{     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
					newWorMap = new HashMap<String,String>();
					newWorMap.put("isEnd", "0");     //不是最后一个
					nowMap.put(keyChar, newWorMap);
					nowMap = newWorMap;
				}
				
				if(i == key.length() - 1){
					nowMap.put("isEnd", "1");    //最后一个
				}
			}
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addSensitiveWordToHashMap(Map keyWordSet) {
		sensitiveWordMap = new HashMap(keyWordSet.size());     //初始化敏感词容器，减少扩容操作
		String key = null;  
		Map nowMap = null;
		Map<String, String> newWorMap = null;
		//迭代keyWordSet
	     Iterator<Map.Entry<String, String>> it = keyWordSet.entrySet().iterator();
	        while (it.hasNext()) {
	            //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
	        Map.Entry<String, String> entry = it.next();
			key =  entry.getValue();    //关键字
			nowMap = sensitiveWordMap;
			for(int i = 0 ; i < key.length() ; i++){
				char keyChar = key.charAt(i);       //转换成char型
				Object wordMap = nowMap.get(keyChar);       //获取
				
				if(wordMap != null){        //如果存在该key，直接赋值
					nowMap = (Map) wordMap;
				}
				else{     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
					newWorMap = new HashMap<String,String>();
					newWorMap.put("isEnd", "0");     //不是最后一个
					nowMap.put(keyChar, newWorMap);
					nowMap = newWorMap;
				}
				
				if(i == key.length() - 1){
					nowMap.put("isEnd", "1");    //最后一个
				}
			}
		}
	}


	/**
	 * 读取敏感词库中的内容，将内容添加到set集合中
	 * @author KVIUFF 
	 * @date 2015年9月29日 上午11:05:06
	 * @return
	 * @version 1.0
	 * @throws Exception 
	 */
	@SuppressWarnings("resource")
	private Set<String> readSensitiveWordFile(String filePath) throws Exception{
		Set<String> set = null;
		File file = new File(filePath);    //读取文件
		InputStreamReader read = new InputStreamReader(new FileInputStream(file),ENCODING);
		try {
			if(file.isFile() && file.exists()){      //文件流是否存在
				set = new HashSet<String>();
				BufferedReader bufferedReader = new BufferedReader(read);
				String txt = null;
				while((txt = bufferedReader.readLine()) != null){    //读取文件，将文件内容放入到set中
					set.add(txt);
			    }
			}
			else{         //不存在抛出异常信息
				throw new Exception("敏感词库文件不存在");
			}
		} catch (Exception e) {
			throw e;
		}finally{
			read.close();     //关闭文件流
		}
		return set;
	}
}

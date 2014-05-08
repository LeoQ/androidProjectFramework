package com.autotiming.csck.bean;

/**
 * 服务器接口返回的基础信息
 * @author qiuli
 */
public class BaseBean<T> {
	/** 请求 ID，每次被接受的接口请求均被记录*/
	public int request_id;
	/**消息代码, 固定由五位数字组成*/
	public int msgcode;
	/**针对该消息的文本说明*/
	public String message;
	/**服务器接口版本*/
	public String version;
	/**服务器 Unix 时间戳*/
	public long servertime;
	/**扩展资源数组, 某些需要返回数据均会存入这里，默认为空*/
	public T response;
}

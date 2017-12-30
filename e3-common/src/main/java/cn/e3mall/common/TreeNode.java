package cn.e3mall.common;

import java.io.Serializable;

/**
 * 根据easyUI中的tree组件需要的json数据格式建立的实体类
 * @author 64566
 *
 */
public class TreeNode implements Serializable{
	private long id;
	private String text;
	private String state;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
}

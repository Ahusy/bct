package cn.antke.bct.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzhichao on 2016/12/18.
 * 活动
 */
public class ActivityEntity {

	@SerializedName("adposition_no")
	private String adNo;//首页广告位置
	@SerializedName("id")
	private String id;
	@SerializedName("pic_url")
	private String picUrl;
	@SerializedName("name")
	private String name;
	@SerializedName("descriptions")
	private String title;
	@SerializedName("link_url")
	private String linkUrl;
	@SerializedName("link_type")
	private int type;//1-网页,2-店铺,3-商品

	public ActivityEntity(String id, String picUrl, String name, String linkUrl) {
		this.id = id;
		this.picUrl = picUrl;
		this.name = name;
		this.linkUrl = linkUrl;
	}

	public String getAdNo() {
		return adNo;
	}

	public void setAdNo(String adNo) {
		this.adNo = adNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}

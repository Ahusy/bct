package cn.antke.bct.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzhichao on 2017/1/6.
 * 品牌
 */
public class BrandEntity extends Entity {

	@SerializedName("brand_id")
	private String id;
	@SerializedName("brand_name")
	private String name;
	@SerializedName("brand_pic")
	private String logo;

	public BrandEntity(String id, String name, String logo) {
		this.id = id;
		this.name = name;
		this.logo = logo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
}

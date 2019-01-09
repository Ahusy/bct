package cn.antke.bct.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuzhichao on 2017/2/10.
 * 品牌详情
 */
public class BrandDetailEntity extends Entity{

	@SerializedName("brand_name")
	private String name;
	@SerializedName("brand_logo")
	private String logo;
	@SerializedName("describe")
	private String desc;
	@SerializedName("sale_count")
	private String num;
	@SerializedName("goods_list")
	private List<ProductEntity> productEntities;

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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getNum() {
		int result = 0;
		try {
			result = Integer.parseInt(num);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public List<ProductEntity> getProductEntities() {
		return productEntities;
	}

	public void setProductEntities(List<ProductEntity> productEntities) {
		this.productEntities = productEntities;
	}
}

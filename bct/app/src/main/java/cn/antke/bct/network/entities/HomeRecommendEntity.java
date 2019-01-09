package cn.antke.bct.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zhaoweiwei on 2016/12/19.
 * 首页下半部分
 */
public class HomeRecommendEntity {

	@SerializedName("floor_id")
	private String id;
	@SerializedName("floor_name")
	private String name;
	@SerializedName("goods_list")
	private List<ProductEntity> productEntities;
	@SerializedName("category_id")
	private String categoryId;

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
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

	public List<ProductEntity> getProductEntities() {
		return productEntities;
	}

	public void setProductEntities(List<ProductEntity> productEntities) {
		this.productEntities = productEntities;
	}
}

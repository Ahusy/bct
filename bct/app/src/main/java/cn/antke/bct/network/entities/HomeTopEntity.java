package cn.antke.bct.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuzhichao on 2017/2/14.
 * 首页上半部分
 */
public class HomeTopEntity extends Entity {

	@SerializedName("ad_list")
	private ADEntity adEntity;
	@SerializedName("brand_list")
	private List<BrandEntity> brandEntities;
	@SerializedName("category_list")
	private List<CategoryTwoEntity> categoryEntities;
	@SerializedName("pic_list")
	private List<ActivityEntity> activityEntities;
	@SerializedName("channel_list")
	private List<PlateEntity> plateEntities;

	public List<PlateEntity> getPlateEntities() {
		return plateEntities;
	}

	public void setPlateEntities(List<PlateEntity> plateEntities) {
		this.plateEntities = plateEntities;
	}

	public ADEntity getAdEntity() {
		return adEntity;
	}

	public void setAdEntity(ADEntity adEntity) {
		this.adEntity = adEntity;
	}

	public List<BrandEntity> getBrandEntities() {
		return brandEntities;
	}

	public void setBrandEntities(List<BrandEntity> brandEntities) {
		this.brandEntities = brandEntities;
	}

	public List<CategoryTwoEntity> getCategoryEntities() {
		return categoryEntities;
	}

	public void setCategoryEntities(List<CategoryTwoEntity> categoryEntities) {
		this.categoryEntities = categoryEntities;
	}

	public List<ActivityEntity> getActivityEntities() {
		return activityEntities;
	}

	public void setActivityEntities(List<ActivityEntity> activityEntities) {
		this.activityEntities = activityEntities;
	}
}

package cn.antke.bct.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuzhichao on 2017/3/21.
 * 首页广告
 */
public class ADEntity {

	@SerializedName("template_id")
	private String type;
	@SerializedName("pic_list")
	private List<ActivityEntity> activityEntities;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ActivityEntity> getActivityEntities() {
		return activityEntities;
	}

	public void setActivityEntities(List<ActivityEntity> activityEntities) {
		this.activityEntities = activityEntities;
	}
}

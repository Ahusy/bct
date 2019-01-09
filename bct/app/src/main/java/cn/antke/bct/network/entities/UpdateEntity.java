package cn.antke.bct.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzhichao on 2017/2/18.
 * 版本更新实体
 */
public class UpdateEntity extends Entity {

	@SerializedName("version")
	private String newVersion;
	@SerializedName("version_desc")
	private String updateDesc;
	@SerializedName("update_url")
	private String downloadUrl;
	@SerializedName("type")
	private int type;//1为强制升级,2为普通升级

	public String getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}

	public String getUpdateDesc() {
		return updateDesc;
	}

	public void setUpdateDesc(String updateDesc) {
		this.updateDesc = updateDesc;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}

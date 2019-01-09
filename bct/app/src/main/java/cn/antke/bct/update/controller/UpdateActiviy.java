package cn.antke.bct.update.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.common.utils.AnalysisUtil;
import com.common.utils.NetWorkUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;

import cn.antke.bct.R;
import cn.antke.bct.update.service.UpdateService;
import cn.antke.bct.utils.CommonTools;
import cn.antke.bct.utils.PermissionUtils;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.base.BaseActivity.REQUEST_PERMISSION_CODE;

/**
 * 版本更新升级
 */
public class UpdateActiviy extends Activity implements View.OnClickListener {

	public final static String KEY_UPDATE_TYPE = "force";
	public final static String KEY_UPDATE_URL = "url";
	public final static String KEY_UPDATE_VERSION_NAME = "version_name";
	public final static String KEY_UPDATE_VERSION_DESC = "version_desc";

	@ViewInject(R.id.txt_update_version)
	private TextView mTxtUpdateVersion;
	@ViewInject(R.id.btn_ignore)
	private TextView mBtnIgnore;
	@ViewInject(R.id.btn_update)
	private TextView mBtnUpdate;
	@ViewInject(R.id.btn_force_update)
	private TextView mBtnForceUpdate;

	private int type;
	private String ApkUrl;
	private String VersionName;

	public static void startUpdateActiviy(Context context, String newVersion, String downloadUrl, int type) {
		Intent intent = new Intent(context, UpdateActiviy.class);
		intent.putExtra(KEY_UPDATE_VERSION_NAME, newVersion);
		intent.putExtra(KEY_UPDATE_URL, downloadUrl);
		intent.putExtra(KEY_UPDATE_TYPE, type);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_update);
		this.getWindow().addFlags(Window.FEATURE_NO_TITLE);
		initStyle();
//		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		ViewInjectUtils.inject(this);

		Intent startIntent = getIntent();
		if (startIntent != null) {
			type = startIntent.getIntExtra(KEY_UPDATE_TYPE, 2);
			ApkUrl = startIntent.getStringExtra(KEY_UPDATE_URL);
			VersionName = startIntent.getStringExtra(KEY_UPDATE_VERSION_NAME);
		}
//		setFinishOnTouchOutside(type != 1);
		mTxtUpdateVersion.setText("V" + VersionName);
		if (type == 1) {//1为强制升级
			mBtnIgnore.setVisibility(View.GONE);
			mBtnUpdate.setVisibility(View.GONE);
			mBtnForceUpdate.setVisibility(View.VISIBLE);
//			ConfigUtils.setIgnoreDate(UpdateActiviy.this, 0);
		} else {
			mBtnIgnore.setVisibility(View.VISIBLE);
			mBtnUpdate.setVisibility(View.VISIBLE);
			mBtnForceUpdate.setVisibility(View.GONE);
		}

		mBtnIgnore.setOnClickListener(this);
		mBtnUpdate.setOnClickListener(this);
		mBtnForceUpdate.setOnClickListener(this);
	}

	private void initStyle() {
		Window window = getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams wl = window.getAttributes();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = CommonTools.dp2px(this, 284);
		this.onWindowAttributesChanged(wl);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AnalysisUtil.onPageStart(getClass().getSimpleName());
		AnalysisUtil.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AnalysisUtil.onPageEnd(getClass().getSimpleName());
		AnalysisUtil.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return KeyEvent.KEYCODE_BACK == keyCode || super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_ignore:
//				ConfigUtils.setIgnoreDate(UpdateActiviy.this, new Date().getTime());
				finish();
				break;
			case R.id.btn_update:
			case R.id.btn_force_update:
				if (StringUtil.isEmpty(ApkUrl)) return;
				if (!NetWorkUtil.isConnect(UpdateActiviy.this)) {
					Toast.makeText(UpdateActiviy.this, R.string.no_available_net, Toast.LENGTH_LONG).show();
					return;
				}

				if (PermissionUtils.isGetPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					download();
				} else {
					PermissionUtils.secondRequest(this, REQUEST_PERMISSION_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
				}
				break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (REQUEST_PERMISSION_CODE == requestCode) {
			if (grantResults.length > 0 && Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[0]) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				download();
			} else {
				ToastUtil.shortShow(this, "获取必要权限失败，请到应用设置中授予权限后再使用！");
			}
		}
	}

	private void download() {
		String packageName = "com.android.providers.downloads";
		int state = UpdateActiviy.this.getPackageManager().getApplicationEnabledSetting(packageName);
		if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
				state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER ||
				state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
			//不能使用系统下载管理器
			startActivity(CommonTools.getIntent(UpdateActiviy.this));
			 /*try {
			  //Open the specific App Info page:
			  Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			  i.setData(Uri.parse("package:" + packageName));
			  startActivity(i);
			 } catch ( ActivityNotFoundException e ) {
			  //Open the generic Apps page:
			  Intent i = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
			  startActivity(i);
			 }*/
		} else {
			mBtnUpdate.setEnabled(false);
			startService(new Intent().setClass(getApplicationContext(), UpdateService.class).putExtra(KEY_UPDATE_URL, ApkUrl));
		}
	}
}

package cn.antke.bct.main.controller;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;

import java.util.IdentityHashMap;

import cn.antke.bct.R;
import cn.antke.bct.base.BaseTabsDrawerActivity;
import cn.antke.bct.category.controller.CategoryFragment;
import cn.antke.bct.category.controller.CategoryTwoFragment;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.deal.controller.DealHallFragment;
import cn.antke.bct.deal.controller.DealRechargeActivity;
import cn.antke.bct.home.controller.HomeFragment;
import cn.antke.bct.home.controller.HomeTwoFragment;
import cn.antke.bct.login.controller.LoginActivity;
import cn.antke.bct.login.utils.UserCenter;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.DealConditionEntity;
import cn.antke.bct.network.entities.Entity;
import cn.antke.bct.network.entities.UpdateEntity;
import cn.antke.bct.person.controller.PersonFragment;
import cn.antke.bct.person.controller.ProtocalActivity;
import cn.antke.bct.special.controller.H5TwoFragment;
import cn.antke.bct.special.controller.SpecialFragment;
import cn.antke.bct.update.controller.UpdateActiviy;
import cn.antke.bct.utils.ExitManager;
import cn.antke.bct.utils.PermissionUtils;

import static cn.antke.bct.common.CommonConstant.FROM_ACT_FIVE;
import static cn.antke.bct.common.CommonConstant.REQUEST_NET_SUCCESS;

public class MainActivity extends BaseTabsDrawerActivity {
	public static final String EXTRA_WHICH_TAB = "extra_which_tab";
	public static final long DIFF_DEFAULT_BACK_TIME = 2000;

	private long mBackTime = -1;
	private boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isHasFragment = true;
		checkUpdateVersion();
		PermissionUtils.requestPermissions(this, REQUEST_PERMISSION_CODE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_PERMISSION_CODE) {
			//如果取消了，结果数组将会为0，结果数组数量对应请求权限的个数
			if (grantResults.length < 1) {
				ToastUtil.shortShow(this, getString(R.string.get_permission_failed));
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		int whichTab = intent.getIntExtra(EXTRA_WHICH_TAB, 0);
		setCurrentTab(whichTab);
	}

	@Override
	protected void addTabs() {
		addTab(initTabView(R.drawable.navigation_ic_home_selector, R.string.main_tab_home), HomeTwoFragment.class, null);
		addTab(initTabView(R.drawable.navigation_ic_category_selector, R.string.main_tab_category), CategoryTwoFragment.class, null);
		addTab(initTabView(R.drawable.navigation_ic_special_selector, R.string.main_tab_faxian), H5TwoFragment.class, null);
		addTab(initTabView(R.drawable.navigation_ic_deal_selector, R.string.main_tab_deal), DealHallFragment.class, null);
		addTab(initTabView(R.drawable.navigation_ic_person_selector, R.string.main_tab_person), PersonFragment.class, null);
	}

	private View initTabView(int tabIcon, int tabText) {
		ViewGroup tab = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.main_tab_item, null);
		ImageView imageView = (ImageView) tab.findViewById(R.id.navigation);
		imageView.setImageResource(tabIcon);

		TextView textView = (TextView) tab.findViewById(R.id.txt_navigation);
		textView.setText(tabText);
		return tab;
	}

	@Override
	public void onTabChanged(String tabId) {
		super.onTabChanged(tabId);
		switch (currentIndex) {
			case 3:
				if (!UserCenter.isLogin(this)) {
					setCurrentTab(preIndex);
					startActivity(new Intent(this, LoginActivity.class));
				} else {
					if (!flag) {
						setCurrentTab(preIndex);
						showProgressDialog();
						IdentityHashMap<String, String> params = new IdentityHashMap<>();
						params.put("tradeHall_site_id", UserCenter.getUserSiteId(this));
						requestHttpData(Constants.Urls.URL_POST_DEAL_INFO, CommonConstant.REQUEST_NET_ONE, FProtocol.HttpMethod.POST, params);
					}
				}
				break;
			case 4:
				if (UserCenter.personFirst(this)){
					setCurrentTab(preIndex);
					ProtocalActivity.startProtocalActivity(this,FROM_ACT_FIVE);
				}
				break;
		}
	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode,data);
		closeProgressDialog();
		if (REQUEST_UPDATE_VERSION_CODE == requestCode) {
			UpdateEntity update = Parsers.getUpdate(data);
			if (update != null) {
				if (REQUEST_NET_SUCCESS.equals(update.getResultCode())) {
					String version = update.getNewVersion() == null ? "" : update.getNewVersion();
					String url = update.getDownloadUrl() == null ? "" : update.getDownloadUrl();
					update.setType(update.getType());
					UpdateActiviy.startUpdateActiviy(this, version, url, update.getType());
				}
			} else {
				ToastUtil.shortShow(this, "检查更新失败");
			}
		} else{
			Entity result = Parsers.getResult(data);
			if (CommonConstant.REQUEST_NET_SUCCESS.equals(result.getResultCode())) {
				switch (requestCode) {
					case CommonConstant.REQUEST_NET_ONE:
						DealConditionEntity dealCondition = Parsers.getDealCondition(data);
						if (dealCondition.getIsPay() == 1) {
							flag = true;
							setCurrentTab(2);
						} else {
							DealRechargeActivity.startDealRechargeActivity(this);
						}
						break;
				}
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		closeProgressDialog();
		super.mistake(requestCode, status, errorMessage);
	}

	@Override
	public void onBackPressed() {
		long nowTime = System.currentTimeMillis();
		long diff = nowTime - mBackTime;
		if (diff >= DIFF_DEFAULT_BACK_TIME) {
			mBackTime = nowTime;
			Toast.makeText(getApplicationContext(), R.string.toast_back_again_exit, Toast.LENGTH_SHORT).show();
		} else {
			ExitManager.instance.exit();
		}
	}
}

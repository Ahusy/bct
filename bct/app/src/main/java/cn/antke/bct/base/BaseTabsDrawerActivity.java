package cn.antke.bct.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.antke.bct.R;


public abstract class BaseTabsDrawerActivity extends BaseActivity implements OnTabChangeListener {
	/**
	 * Tab面板
	 */
	protected FragmentTabHost mTabHost;
	/**
	 * Tab控件
	 */
	protected TabWidget mTabWidget;
	/**
	 * 当前Tab页下标
	 */
	protected int currentIndex;
	protected TextView titleTextView;
	protected ImageView right_button;
	protected int preIndex;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_tabs_drawerlayout);
		useTransparentActionBar();
		initChenJinStatusBar();
		setStatusBarDarkMode(true, this);
		importantInit();
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.fcontainer);
		addTabs();
		mTabHost.setOnTabChangedListener(this);
		mTabWidget = mTabHost.getTabWidget();
		mTabWidget.setDividerDrawable(null);
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
			currentIndex = mTabHost.getCurrentTab();
		}
	}

	protected void useTransparentActionBar() {
		fitSystemWindows(false);
		setActionBarResource(R.color.transparent);
	}
	protected void fitSystemWindows(boolean isFit) {
		fitSystemWindows = isFit;
	}
	protected void setActionBarResource(int actionBarResourceId) {
		this.actionBarResourceId = actionBarResourceId;
	}
	private boolean mChenJinStatusBarTag = true;
	private boolean fitSystemWindows = true;
	private int actionBarResourceId = R.color.color_diglog_background;
	protected void initChenJinStatusBar() {
		if (isUseChenJinStatusBar() && mChenJinStatusBarTag) {
			setTranslucentStatus(true);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(actionBarResourceId);
		}
	}

	protected void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	public void setStatusBarDarkMode(boolean darkmode, Activity activity) {
		Class<? extends Window> clazz = activity.getWindow().getClass();
		try {
			Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
			Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
			int darkModeFlag = field.getInt(layoutParams);
			Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
			extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isUseChenJinStatusBar() {
		if (android.os.Build.BRAND.equalsIgnoreCase("Meizu")) {
			return false;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			return true;
		}
		return false;
	}

	/**
	 * 加载页面之前需要完成的事情
	 */
	protected void importantInit() {

	}
	/**
	 * 设置Tab切换
	 *
	 * @param tabIndex 切换的Tab下标
	 */
	public void setCurrentTab(int tabIndex) {
		mTabHost.setCurrentTab(tabIndex);
	}

	protected abstract void addTabs();

	protected void addTab(View tabView, Class<?> cls, Bundle bundle) {
		mTabHost.addTab(mTabHost.newTabSpec(cls.getSimpleName()).setIndicator(tabView), cls, bundle);
	}

	protected void addTab(View tabView, Class<?> cls, String tabName, Bundle bundle) {
		mTabHost.addTab(mTabHost.newTabSpec(tabName).setIndicator(tabView), cls, bundle);
	}

	public int getTabPosition() {
		return currentIndex;
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	public void onTabChanged(String tabId) {
		preIndex = currentIndex;
		currentIndex = mTabHost.getCurrentTab();
	}
	public void showTabHost(boolean show) {
		if (show) {
			mTabWidget.setVisibility(View.VISIBLE);
		} else {
			mTabWidget.setVisibility(View.GONE);
		}
	}

	public boolean tabHostIsShow() {
		return mTabWidget.getVisibility() == View.VISIBLE;
	}
}

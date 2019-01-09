package cn.antke.bct.main.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.view.SimpleDraweeView;

import cn.antke.bct.R;
import cn.antke.bct.base.BaseActivity;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.utils.ImageUtils;
import cn.antke.bct.utils.ViewInjectUtils;

/**
 * Created by liuzhichao on 2017/5/15.
 * 活动界面
 */
public class NewActivityActivity extends BaseActivity implements View.OnClickListener {

	@ViewInject(R.id.sdv_activity_pic)
	private SimpleDraweeView sdvActivityPic;
	@ViewInject(R.id.iv_activity_close)
	private View ivActivityClose;

	public static void startNewActivityActivity(Context context, String url, String picUrl) {
		Intent intent = new Intent(context, NewActivityActivity.class);
		intent.putExtra(CommonConstant.EXTRA_URL, url);
		intent.putExtra(CommonConstant.EXTRA_PIC_URL, picUrl);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_new_activity);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		ImageUtils.setSmallImg(sdvActivityPic, getIntent().getStringExtra(CommonConstant.EXTRA_PIC_URL));

		ivActivityClose.setOnClickListener(this);
		sdvActivityPic.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_activity_close:
				finish();
				break;
			case R.id.sdv_activity_pic:
				break;
		}
	}
}

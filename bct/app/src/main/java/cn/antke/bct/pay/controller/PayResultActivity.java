package cn.antke.bct.pay.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.common.viewinject.annotation.ViewInject;

import cn.antke.bct.R;
import cn.antke.bct.base.ToolBarActivity;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.main.controller.MainActivity;
import cn.antke.bct.person.controller.OrderListActivity;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.common.CommonConstant.EXTRA_TYPE;
import static cn.antke.bct.common.CommonConstant.ORDERSTATE_DELIVING;

/**
 * Created by liuzhichao on 2017/5/21.
 * 支付结果页
 */
public class PayResultActivity extends ToolBarActivity implements View.OnClickListener {

	@ViewInject(R.id.tv_pay_result_integral)
	private TextView tvPayResultIntegral;
	@ViewInject(R.id.tv_pay_result_way)
	private TextView tvPayResultWay;
	@ViewInject(R.id.tv_pay_result_lbtn)
	private View tvPayResultLbtn;
	@ViewInject(R.id.tv_pay_result_rbtn)
	private View tvPayResultRbtn;

	public static void startPayResultActivity(Context context, String payAmount, String payWay, int payType) {
		Intent intent = new Intent(context, PayResultActivity.class);
		intent.putExtra(CommonConstant.EXTRA_TYPE, payType);
		intent.putExtra("payAmount", payAmount);
		intent.putExtra("payWay", payWay);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_pay_result);
		ViewInjectUtils.inject(this);
		initView();
	}

	private void initView() {
		setLeftTitle(getString(R.string.pay_result));

		String payAmount = getIntent().getStringExtra("payAmount");
		String payWay = getIntent().getStringExtra("payWay");
		int payType = getIntent().getIntExtra(CommonConstant.EXTRA_TYPE, 0);
		if (CommonConstant.TYPE_WXPAY == payType) {
			payWay += "+微信";
		} else if (CommonConstant.TYPE_ALIPAY == payType) {
			payWay += "+支付宝";
		}
		tvPayResultIntegral.setText(payAmount);
		tvPayResultWay.setText(payWay);

		tvPayResultLbtn.setOnClickListener(this);
		tvPayResultRbtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_pay_result_lbtn:
				startActivity(new Intent(this, OrderListActivity.class).putExtra(EXTRA_TYPE, ORDERSTATE_DELIVING));
				finish();
				break;
			case R.id.tv_pay_result_rbtn:
				startActivity(new Intent(this, MainActivity.class));
				break;
		}
	}
}

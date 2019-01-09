package cn.antke.bct.person.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;

import java.util.IdentityHashMap;
import java.util.Map;

import cn.antke.bct.R;
import cn.antke.bct.base.ToolBarActivity;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.login.utils.UserCenter;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.PayEntity;
import cn.antke.bct.pay.controller.PayResultActivity;
import cn.antke.bct.pay.util.PayUtils;
import cn.antke.bct.utils.ExitManager;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.common.CommonConstant.REQUEST_NET_ONE;

/**
 * Created by zhaoweiwei on 2017/5/15.
 * 充值中心
 */

public class ChargeCenterActivity extends ToolBarActivity implements View.OnClickListener {
	@ViewInject(R.id.charge_center_share_integral)
	private TextView shareIntegral;
	@ViewInject(R.id.charge_center_useable_integral)
	private TextView useableIntegral;
	@ViewInject(R.id.charge_center_usernumber)
	private TextView userNumber;
	@ViewInject(R.id.charge_center_realname)
	private TextView realName;
	@ViewInject(R.id.charge_center_phone)
	private TextView userPhone;
	@ViewInject(R.id.charge_center_money)
	private EditText amountMoney;
	@ViewInject(R.id.charge_center_wx)
	private TextView chargeByWx;
	@ViewInject(R.id.charge_center_zfb)
	private TextView chargeByZfb;
	@ViewInject(R.id.charge_center_pay)
	private TextView chargePay;

	private String chargeType ="3";
	private String payType = "2";

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Map<String, String> result = (Map<String, String>) msg.obj;
			String resultStatus = "";
			for (String key : result.keySet()) {
				if (TextUtils.equals(key, "resultStatus")) {
					resultStatus = result.get(key);
				}
			}
           /*
			 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
			 */
			// 判断resultStatus 为9000则代表支付成功
			Intent intent = new Intent(CommonConstant.ACTION_BROADCAST_PAY_RESULT);
			intent.putExtra(CommonConstant.EXTRA_TYPE, CommonConstant.TYPE_ALIPAY);
			if (TextUtils.equals(resultStatus, "9000")) {
				// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
				intent.putExtra(CommonConstant.EXTRA_CODE, 0);
			} else {
				// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
				intent.putExtra(CommonConstant.EXTRA_CODE, -1);
			}
			LocalBroadcastManager.getInstance(ChargeCenterActivity.this).sendBroadcast(intent);

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_charge_center);
		ViewInjectUtils.inject(this);
		setLeftTitle(getString(R.string.charge_center));
		userNumber.setText(UserCenter.getUserCode(this));
		realName.setText(UserCenter.getUserName(this));
		userPhone.setText(UserCenter.getPhone(this));
		shareIntegral.setOnClickListener(this);
		useableIntegral.setOnClickListener(this);
		chargeByWx.setOnClickListener(this);
		chargeByZfb.setOnClickListener(this);
		chargePay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.charge_center_share_integral:
				shareIntegral.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.language_area_selected, 0);
				useableIntegral.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.language_area_normal, 0);
				chargeType = "4";
				break;
			case R.id.charge_center_useable_integral:
				useableIntegral.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.language_area_selected, 0);
				shareIntegral.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.language_area_normal, 0);
				chargeType = "1";
				break;
			case R.id.charge_center_wx:
				chargeByWx.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.language_area_selected, 0);
				chargeByZfb.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.language_area_normal, 0);
				payType = "2";
				break;
			case R.id.charge_center_zfb:
				chargeByZfb.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.language_area_selected, 0);
				chargeByWx.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.language_area_normal, 0);
				payType = "1";
				break;
			case R.id.charge_center_pay:
				String amount = amountMoney.getText().toString();
				if (!StringUtil.isEmpty(amount)) {
					int amountInt = Integer.parseInt(amount);
					if (amountInt>0) {
						showProgressDialog();
						IdentityHashMap<String, String> params = new IdentityHashMap<>();
						params.put("type", chargeType);
						params.put("pay_type", payType);
						params.put("amount", amount);
						requestHttpData(Constants.Urls.URL_POST_INTEGRAL_CHARGE, REQUEST_NET_ONE, FProtocol.HttpMethod.POST, params);
					} else {
						ToastUtil.shortShow(this,getString(R.string.consumer_charge_number));
					}
				} else {
					ToastUtil.shortShow(this,getString(R.string.consumer_charge_number));
				}

				BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						//BroadcastReceiver是否需要变成全局变量
						LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
						int intExtra = intent.getIntExtra(CommonConstant.EXTRA_CODE, -1);
						switch (intExtra) {
							case 0:
								ToastUtil.shortShow(context, "支付成功");
								PayResultActivity.startPayResultActivity(ChargeCenterActivity.this, amount, payType, intent.getIntExtra(CommonConstant.EXTRA_TYPE, 0));
								ExitManager.instance.closeBuyNowActivity();
								break;
							case -1:
								ToastUtil.shortShow(context, "支付失败");
								break;
							case -2:
								ToastUtil.shortShow(context, "取消支付");
								break;
						}
					}
				};

				IntentFilter intentFilter = new IntentFilter(CommonConstant.ACTION_BROADCAST_PAY_RESULT);
				LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
				break;
		}
	}

	@Override
	protected void parseData(int requestCode, String data) {
		super.parseData(requestCode, data);
		PayEntity payEntity = Parsers.getPay(data);
		if (payEntity != null) {
			String prePayId = payEntity.getPayId();
			if ("2".equals(payType)) {//微信支付
				PayUtils.startWXPay(this, prePayId);
			} else {//支付宝支付
				PayUtils.startAliPay(this, handler, prePayId);
			}
		}
	}
}

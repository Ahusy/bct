package cn.antke.bct.product.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import cn.antke.bct.R;
import cn.antke.bct.base.BaseActivity;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.ProductDetailEntity;
import cn.antke.bct.network.entities.StockEntity;
import cn.antke.bct.product.adapter.NormsAdapter;
import cn.antke.bct.utils.ViewInjectUtils;
import cn.antke.bct.widget.AddMinuLayout;
import cn.antke.bct.widget.flowlayout.FlowTagLayout;
import cn.antke.bct.widget.flowlayout.OnTagSelectListener;

/**
 * Created by liuzhichao on 2017/6/14.
 * 尺码规格
 */
public class NormsActivity extends BaseActivity implements View.OnClickListener, OnTagSelectListener {

	@ViewInject(R.id.iv_norms_close)
	private View ivNormsClose;
	@ViewInject(R.id.aml_norms_num)
	private AddMinuLayout amlNormsNum;
	@ViewInject(R.id.tv_norms_first)
	private TextView tvNormsFirst;
	@ViewInject(R.id.ftl_norms_first)
	private FlowTagLayout ftlNormsFirst;
	@ViewInject(R.id.tv_norms_second)
	private TextView tvNormsSecond;
	@ViewInject(R.id.ftl_norms_second)
	private FlowTagLayout ftlNormsSecond;
	@ViewInject(R.id.tv_norms_buy)
	private TextView tvNormsBuy;
	@ViewInject(R.id.ll_norms_first)
	private View llNormsFirst;
	@ViewInject(R.id.ll_norms_second)
	private View llNormsSecond;
	@ViewInject(R.id.tv_stock)
	private TextView tvStock;

	private List<ProductDetailEntity.NormEntity> normEntities;
	private String firstId;
	private String secondId;
	private int from;
	private int parentPosition;
	private int position;
	public static final String GOOD_ID="goodid";
	public static final String NUM="num";
	private String goodId;
	private String num="1";

	public static void startNormsActivity(Activity activity, int requestCode, int from,ArrayList<ProductDetailEntity.NormEntity> normEntities, int parentPosition, int position) {
		Intent intent = new Intent(activity, NormsActivity.class);
		intent.putExtra(CommonConstant.EXTRA_FROM, from);
		intent.putExtra(CommonConstant.EXTRA_ENTITY, normEntities);
		if (from == CommonConstant.FROM_SHOP_CAR_LIST) {
			intent.putExtra("parentPosition", parentPosition);
			intent.putExtra("position", position);
		}
		activity.startActivityForResult(intent, requestCode);
	}
	public static void startNormsActivity(Activity activity, int requestCode, int from,ArrayList<ProductDetailEntity.NormEntity> normEntities, int parentPosition, int position,String goodId) {
		Intent intent = new Intent(activity, NormsActivity.class);
		intent.putExtra(CommonConstant.EXTRA_FROM, from);
		intent.putExtra(CommonConstant.EXTRA_ENTITY, normEntities);
		intent.putExtra(GOOD_ID,goodId);
		if (from == CommonConstant.FROM_SHOP_CAR_LIST) {
			intent.putExtra("parentPosition", parentPosition);
			intent.putExtra("position", position);
		}
		activity.startActivityForResult(intent, requestCode);
	}
	public static void startNormsActivity(Activity activity, int requestCode, int from,ArrayList<ProductDetailEntity.NormEntity> normEntities, int parentPosition, int position,String goodId,String num) {
		Intent intent = new Intent(activity, NormsActivity.class);
		intent.putExtra(CommonConstant.EXTRA_FROM, from);
		intent.putExtra(CommonConstant.EXTRA_ENTITY, normEntities);
		intent.putExtra(GOOD_ID,goodId);
		intent.putExtra(NUM,num);
		if (from == CommonConstant.FROM_SHOP_CAR_LIST) {
			intent.putExtra("parentPosition", parentPosition);
			intent.putExtra("position", position);
		}
		activity.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_norms);
		ViewInjectUtils.inject(this);
		initStyle();
		initView();
		loadData();
	}

	private void initStyle() {
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams wl = window.getAttributes();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		this.onWindowAttributesChanged(wl);
	}

	private void initView() {
		from = getIntent().getIntExtra(CommonConstant.EXTRA_FROM, CommonConstant.FROM_SHOP_CAR_LIST);
		goodId = getIntent().getStringExtra(GOOD_ID);
		normEntities = getIntent().getParcelableArrayListExtra(CommonConstant.EXTRA_ENTITY);
		if (from == CommonConstant.FROM_SHOP_CAR_LIST) {
			parentPosition = getIntent().getIntExtra("parentPosition", -1);
			position = getIntent().getIntExtra("position", -1);
		}

		num=getIntent().getStringExtra(NUM);
		if(TextUtils.isEmpty(num)||"0".equals(num)){
			num="1";
		}

		findViewById(R.id.v_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

//		if (from == CommonConstant.FROM_PRODUCT_DETAIL) {
//			tvNormsBuy.setText(getString(R.string.buy_now));
//		} else if (from == CommonConstant.FROM_DRAW_DETAIL) {
//			llNormsFirst.setVisibility(View.GONE);
//			llNormsSecond.setVisibility(View.GONE);
//			tvNormsBuy.setText(getString(R.string.now_draw));
//		} else {
//			tvNormsBuy.setText(getString(R.string.finished));
//		}
		tvNormsBuy.setText(getString(R.string.button_ok));
		amlNormsNum.setLimitNum(1, 999);
		amlNormsNum.setResult(num);

		ivNormsClose.setOnClickListener(this);
		tvNormsBuy.setOnClickListener(this);
	}

	private void loadData() {
		if (normEntities != null && normEntities.size() > 0) {
			if (normEntities.size() == 1) {
				ProductDetailEntity.NormEntity normEntity = normEntities.get(0);
				tvNormsFirst.setText(normEntity.getAttrName());
				NormsAdapter firstAdapter = new NormsAdapter(this);
				ftlNormsFirst.setAdapter(firstAdapter);
				ftlNormsFirst.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
				List<String> attrDatas = new ArrayList<>();
				for (ProductDetailEntity.NormEntity normEntity1 : normEntity.getNormEntities()) {
					attrDatas.add(normEntity1.getAttrValue());
				}
				firstAdapter.onlyAddAll(attrDatas);
				ftlNormsFirst.setOnTagSelectListener(this);

				tvNormsSecond.setVisibility(View.GONE);
				ftlNormsSecond.setVisibility(View.GONE);
			} else {
				ProductDetailEntity.NormEntity normEntity11 = normEntities.get(0);
				tvNormsFirst.setText(normEntity11.getAttrName());
				NormsAdapter firstAdapter = new NormsAdapter(this);
				ftlNormsFirst.setAdapter(firstAdapter);
				ftlNormsFirst.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
				List<String> attrDatas1 = new ArrayList<>();
				for (ProductDetailEntity.NormEntity normEntity1 : normEntity11.getNormEntities()) {
					attrDatas1.add(normEntity1.getAttrValue());
				}
				if (attrDatas1.size()>0) {
					firstAdapter.onlyAddAll(attrDatas1);
					ftlNormsFirst.setOnTagSelectListener(this);
				} else {
					tvNormsFirst.setVisibility(View.GONE);
					ftlNormsFirst.setVisibility(View.GONE);
				}

				ProductDetailEntity.NormEntity normEntity22 = normEntities.get(1);
				tvNormsSecond.setText(normEntity22.getAttrName());
				NormsAdapter secondAdapter = new NormsAdapter(this);
				ftlNormsSecond.setAdapter(secondAdapter);
				ftlNormsSecond.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
				List<String> attrDatas2 = new ArrayList<>();
				for (ProductDetailEntity.NormEntity normEntity1 : normEntity22.getNormEntities()) {
					attrDatas2.add(normEntity1.getAttrValue());
				}

				if (attrDatas2.size()>0) {
					secondAdapter.onlyAddAll(attrDatas2);
					ftlNormsSecond.setOnTagSelectListener(this);
				} else {
					tvNormsSecond.setVisibility(View.GONE);
					ftlNormsSecond.setVisibility(View.GONE);
				}
			}
			ProductDetailEntity.NormEntity normEntity1 = normEntities.get(0);
			firstId = normEntity1.getNormEntities().get(0).getRelationId();
			ProductDetailEntity.NormEntity normEntity2 = normEntities.get(1);
			secondId = normEntity2.getNormEntities().get(0).getRelationId();
			ftlNormsFirst.setDefaultChecked(0);
			ftlNormsFirst.reloadData();
			ftlNormsSecond.setDefaultChecked(0);
			ftlNormsSecond.reloadData();
			requestStock();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_norms_close:
				finish();
				break;
			case R.id.tv_norms_buy:
				String result = "";
				Intent intent = new Intent();
				if (from != CommonConstant.FROM_DRAW_DETAIL) {
					if (normEntities != null && normEntities.size() > 0) {
						if (normEntities.size() == 1) {
							if (TextUtils.isEmpty(firstId)) {
								ToastUtil.shortShow(this, getString(R.string.please_select_norm));
								return;
							}
							result = firstId;
						} else {
							if (TextUtils.isEmpty(firstId) || TextUtils.isEmpty(secondId)) {
								ToastUtil.shortShow(this, getString(R.string.please_select_norm2));
								return;
							}
							result = firstId + "," + secondId;
						}
					}

					if (from == CommonConstant.FROM_SHOP_CAR_LIST) {
						intent.putExtra("parentPosition", parentPosition);
						intent.putExtra("position", position);
					}
					intent.putExtra(CommonConstant.EXTRA_CODE, result);
				}
				intent.putExtra(CommonConstant.EXTRA_NUM, amlNormsNum.getResult());
				setResult(RESULT_OK, intent);
				finish();
				break;
		}
	}

	@Override
	public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
		if (normEntities != null && normEntities.size() > 0) {
			switch (parent.getId()) {
				case R.id.ftl_norms_first:
					ProductDetailEntity.NormEntity normEntity1 = normEntities.get(0);
					if (selectedList.size() == 1) {
						firstId = normEntity1.getNormEntities().get(selectedList.get(0)).getRelationId();
					} else {
						firstId = "";
					}
					requestStock();
					break;
				case R.id.ftl_norms_second:
					ProductDetailEntity.NormEntity normEntity2 = normEntities.get(1);
					if (selectedList.size() == 1) {
						secondId = normEntity2.getNormEntities().get(selectedList.get(0)).getRelationId();
					} else {
						secondId = "";
					}
					requestStock();
					break;
			}
		}
	}

	private void requestStock() {
		if(TextUtils.isEmpty(firstId)||TextUtils.isEmpty(secondId)||TextUtils.isEmpty(goodId)){
			amlNormsNum.setLimitNum(1,999);
			tvStock.setText("数量");
			return;
		}
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("goods_id", goodId);
		params.put("attr_relationids",firstId+","+secondId);
		requestHttpData(Constants.Urls.URL_POST_SELECT_STOCKQUANTITY,CommonConstant.REQUEST_NET_ONE, FProtocol.HttpMethod.POST,params);

	}

	@Override
	public void success(int requestCode, String data) {
		super.success(requestCode, data);
		if(requestCode==CommonConstant.REQUEST_NET_ONE){
			StockEntity stock = Parsers.getStock(data);
			if("0".equals(stock.getResultCode())){
				String stockQuantity = stock.getStockQuantity();
				try {
					amlNormsNum.setLimitNum(1,Integer.parseInt(stockQuantity));
				}catch (Exception e){
					amlNormsNum.setLimitNum(1,999);
				}
				amlNormsNum.setResult(num);
				tvStock.setText("数量 （库存:"+stockQuantity+")");
			}else{
				tvStock.setText("数量");
			}
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		super.mistake(requestCode, status, errorMessage);
		tvStock.setText("数量");
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.actionsheet_dialog_out);
	}
}

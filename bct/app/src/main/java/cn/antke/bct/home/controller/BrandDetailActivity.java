package cn.antke.bct.home.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.DeviceUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.view.GridViewForInner;
import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.IdentityHashMap;

import cn.antke.bct.R;
import cn.antke.bct.base.BaseActivity;
import cn.antke.bct.home.adapter.BrandProductAdapter;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.BrandDetailEntity;
import cn.antke.bct.network.entities.ProductEntity;
import cn.antke.bct.product.controller.ProductDetailActivity;
import cn.antke.bct.product.controller.ProductListActivity;
import cn.antke.bct.utils.ImageUtils;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.common.CommonConstant.EXTRA_ID;
import static cn.antke.bct.common.CommonConstant.REQUEST_NET_SUCCESS;

/**
 * Created by liuzhichao on 2017/1/5.
 * 品牌详情
 */
public class BrandDetailActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

	private static final int REQUEST_NET_BRAND_DETAIL = 10;

	@ViewInject(R.id.iv_brand_detail_back)
	private View ivBrandDetailBack;
	@ViewInject(R.id.tv_brand_detail_title)
	private TextView tvBrandDetailTitle;
	@ViewInject(R.id.sdv_brand_detail_logo)
	private SimpleDraweeView sdvBrandDetailLogo;
	@ViewInject(R.id.tv_brand_detail_name)
	private TextView tvBrandDetailName;
	@ViewInject(R.id.tv_brand_detail_num)
	private TextView tvBrandDetailNum;
	@ViewInject(R.id.tv_brand_detail_more)
	private TextView tvBrandDetailMore;
	@ViewInject(R.id.tv_brand_detail_desc)
	private TextView tvBrandDetailDesc;
	@ViewInject(R.id.gvfi_brand_detail_product)
	private GridViewForInner gvfiBrandDetailProduct;

	private boolean isRunAnim;
	private boolean isOpen;
	private int height;
	private String brandId;
	private String brandName;
	private BrandProductAdapter adapter;
	private int minHeight;

	public static void startBrandDetailActivity(Context context, String id) {
		Intent intent = new Intent(context, BrandDetailActivity.class);
		intent.putExtra(EXTRA_ID, id);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		setContentView(R.layout.act_brand_detail);
		ViewInjectUtils.inject(this);
		initView();
		loadData();
	}

	private void initView() {
		brandId = getIntent().getStringExtra(EXTRA_ID);
		minHeight = DeviceUtil.dp_to_px(this, 100);

		ivBrandDetailBack.setOnClickListener(v -> finish());

		tvBrandDetailDesc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				tvBrandDetailDesc.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				height = tvBrandDetailDesc.getHeight();
				tvBrandDetailDesc.getLayoutParams().height = minHeight;
				tvBrandDetailDesc.requestLayout();
			}
		});

		gvfiBrandDetailProduct.setOnItemClickListener(this);
		tvBrandDetailDesc.setOnClickListener(this);
		tvBrandDetailMore.setOnClickListener(this);
	}

	private void loadData() {
		showProgressDialog();
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		params.put("brand_id", brandId);
		requestHttpData(Constants.Urls.URL_POST_BRAND_DETAIL, REQUEST_NET_BRAND_DETAIL, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void success(int requestCode, String data) {
		closeProgressDialog();
		BrandDetailEntity brandDetail = Parsers.getBrandDetail(data);
		if (REQUEST_NET_SUCCESS.equals(brandDetail.getResultCode())) {
			if (REQUEST_NET_BRAND_DETAIL == requestCode) {
				tvBrandDetailTitle.setText(brandDetail.getName());
				tvBrandDetailName.setText(brandDetail.getName());
				brandName = brandDetail.getName();
				ImageUtils.setSmallImg(sdvBrandDetailLogo,brandDetail.getLogo());
				if (StringUtil.isEmpty(brandDetail.getDesc())) {
					tvBrandDetailDesc.setVisibility(View.GONE);
				} else {
					tvBrandDetailDesc.setText(brandDetail.getDesc());
				}
				tvBrandDetailNum.setText(getString(R.string.product_detail_sale_num, String.valueOf(brandDetail.getNum())));
				adapter = new BrandProductAdapter(this, brandDetail.getProductEntities());
				gvfiBrandDetailProduct.setAdapter(adapter);
			}
		} else {
			ToastUtil.shortShow(this, brandDetail.getResultMsg());
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		closeProgressDialog();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_brand_detail_more:
				if (TextUtils.isEmpty(brandId)  || TextUtils.isEmpty(brandName)) {
					return;
				}
				ProductListActivity.startProductListActivity(this,-1, brandId, brandName,brandName);
				break;
			case R.id.tv_brand_detail_desc:
				if (isRunAnim) return;

				ValueAnimator valueAnimator;
				if (isOpen) {
					//展开的，执行收缩动画，数字递减
					valueAnimator = ValueAnimator.ofInt(height, minHeight);
				} else {
					//收缩的，执行展开动画，数字递增
					valueAnimator = ValueAnimator.ofInt(minHeight, height);
				}
				valueAnimator.addUpdateListener(animation -> {
					tvBrandDetailDesc.getLayoutParams().height = (int) animation.getAnimatedValue();
					tvBrandDetailDesc.requestLayout();
				});
				valueAnimator.setDuration(250);
				valueAnimator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						isRunAnim = true;
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						isRunAnim = false;
						if (isOpen) {
							tvBrandDetailDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.up_icon);
						} else {
							tvBrandDetailDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.down_icon);
						}
					}
				});
				isOpen = !isOpen;
				valueAnimator.start();
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ProductEntity productEntity = adapter.getItem(position);
		if (productEntity != null) {
			ProductDetailActivity.startProductDetailActivity(this, productEntity.getGoodsId());
		}
	}
}

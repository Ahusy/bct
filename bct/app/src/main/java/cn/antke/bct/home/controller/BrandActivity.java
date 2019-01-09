package cn.antke.bct.home.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.RefreshRecyclerView;

import java.util.IdentityHashMap;

import cn.antke.bct.R;
import cn.antke.bct.base.ToolBarActivity;
import cn.antke.bct.home.adapter.BrandAdapter;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.BrandEntity;
import cn.antke.bct.network.entities.Entity;
import cn.antke.bct.network.entities.PagesEntity;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.common.CommonConstant.REQUEST_NET_SUCCESS;


/**
 * Created by liuzhichao on 2017/1/6.
 * 品牌列表
 */
public class BrandActivity extends ToolBarActivity implements View.OnClickListener, RefreshRecyclerView.OnRefreshAndLoadMoreListener {

	private static final int REQUEST_NET_BRAND_LIST = 10;
	private static final int REQUEST_NET_BRAND_LIST_MORE = 20;

	@ViewInject(R.id.rrv_brand_list)
	private RefreshRecyclerView rrvBrandList;
	private BrandAdapter adapter;

	public static void startBrandActivity(Context context){
		context.startActivity(new Intent(context, BrandActivity.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_brand);
		ViewInjectUtils.inject(this);
		initView();
		loadData(false);
	}

	private void initView() {
		setLeftTitle("品牌");

		rrvBrandList.setHasFixedSize(true);
		rrvBrandList.setMode(RefreshRecyclerView.Mode.BOTH);
		rrvBrandList.setLayoutManager(new GridLayoutManager(this, 3));
		rrvBrandList.setOnRefreshAndLoadMoreListener(this);
	}

	private void loadData(boolean isMore) {
		IdentityHashMap<String, String> params = new IdentityHashMap<>();
		int page = 1;
		int request = REQUEST_NET_BRAND_LIST;
		if (isMore) {
			page = adapter.getPage() + 1;
			request = REQUEST_NET_BRAND_LIST_MORE;
		}
		params.put("current_page", String.valueOf(page));
		params.put("page_size", "20");
		requestHttpData(Constants.Urls.URL_POST_BRAND_LIST, request, FProtocol.HttpMethod.POST, params);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ll_brand_layout:
				BrandEntity brandEntity = (BrandEntity) v.getTag();
				if (brandEntity != null) {
					BrandDetailActivity.startBrandDetailActivity(this, brandEntity.getId());
				}
				break;
		}
	}

	@Override
	public void onRefresh() {
		loadData(false);
	}

	@Override
	public void onLoadMore() {
		loadData(true);
	}

	@Override
	public void success(int requestCode, String data) {
		rrvBrandList.resetStatus();
		Entity result = Parsers.getResult(data);
		if (REQUEST_NET_SUCCESS.equals(result.getResultCode())) {
			if (REQUEST_NET_BRAND_LIST == requestCode) {
				PagesEntity<BrandEntity> brandEntities = Parsers.getPageBrand(data);
				if (brandEntities != null && brandEntities.getDatas().size() > 0) {
					adapter = new BrandAdapter(brandEntities.getDatas(), this);
					rrvBrandList.setAdapter(adapter);
					if (brandEntities.getTotalPage() > 1) {
						rrvBrandList.setCanAddMore(true);
					} else {
						rrvBrandList.setCanAddMore(false);
					}
				} else {
					ToastUtil.shortShow(this,"暂无数据");
				}
			} else if (REQUEST_NET_BRAND_LIST_MORE == requestCode){
				PagesEntity<BrandEntity> brandEntities = Parsers.getPageBrand(data);
				if (brandEntities != null && brandEntities.getDatas().size() > 0) {
					adapter.addDatas(brandEntities.getDatas());
					if (brandEntities.getTotalPage() <= adapter.getPage()) {
						rrvBrandList.setCanAddMore(false);
					}
				} else {
					ToastUtil.shortShow(this,"暂无数据");
				}
			}
		} else {
			ToastUtil.shortShow(this, result.getResultMsg());
		}
	}

	@Override
	public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
		rrvBrandList.resetStatus();
		ToastUtil.shortShow(this, errorMessage);
	}
}

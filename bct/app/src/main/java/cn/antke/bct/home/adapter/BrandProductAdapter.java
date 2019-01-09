package cn.antke.bct.home.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.antke.bct.R;
import cn.antke.bct.network.entities.ProductEntity;
import cn.antke.bct.utils.LayoutUtil;

/**
 * Created by liuzhichao on 2017/1/6.
 * 品牌商品
 */
public class BrandProductAdapter extends BaseAdapterNew<ProductEntity> {
	private Context context;

	public BrandProductAdapter(Context context, List<ProductEntity> mDatas) {
		super(context, mDatas);
		this.context = context;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.home_recommend_cloth_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		ProductEntity productEntity = getItem(position);
		SimpleDraweeView homeRecommendCloth = ViewHolder.get(convertView, R.id.home_recommend_cloth);
		TextView homeRecommendBrand = ViewHolder.get(convertView, R.id.home_recommend_brand);
		TextView homeRecommendName = ViewHolder.get(convertView, R.id.home_recommend_name);
		TextView homeRecommendPrice = ViewHolder.get(convertView, R.id.home_recommend_price);

		LayoutUtil.setHeightAsWidth(context,homeRecommendCloth,3,5);
		if (productEntity != null) {
			homeRecommendCloth.setImageURI(productEntity.getPicUrl());
			homeRecommendBrand.setText(productEntity.getBrandName());
			homeRecommendName.setText(productEntity.getGoodsName());
			homeRecommendPrice.setText(getContext().getString(R.string.product_price, productEntity.getPrice()));
		}
	}
}

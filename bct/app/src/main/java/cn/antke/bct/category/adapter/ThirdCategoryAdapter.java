package cn.antke.bct.category.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.antke.bct.R;
import cn.antke.bct.network.entities.CategoryTwoEntity;
import cn.antke.bct.utils.ImageUtils;

/**
 * Created by liuzhichao on 2017/4/18.
 * 三级分类adapter
 */
class ThirdCategoryAdapter extends BaseAdapterNew<CategoryTwoEntity> {

	private View.OnClickListener onClickListener;

	ThirdCategoryAdapter(Context context, List<CategoryTwoEntity> mDatas, View.OnClickListener onClickListener) {
		super(context, mDatas);
		this.onClickListener = onClickListener;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.item_third_category;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		CategoryTwoEntity categoryEntity = getItem(position);
		if (categoryEntity != null) {
			TextView tvThirdCategrory = (TextView) convertView.findViewById(R.id.tv_third_category);
			tvThirdCategrory.setText(categoryEntity.getName());

			LinearLayout llThirdCategory = (LinearLayout) convertView.findViewById(R.id.ll_third_category);
			llThirdCategory.setOnClickListener(onClickListener);
			ImageUtils.setSmallImg(ViewHolder.get(convertView, R.id.sdv_item_home_plate_pic), categoryEntity.getImgUrl());
			llThirdCategory.setTag(categoryEntity.getId()+":"+categoryEntity.getName());
		}
	}
}

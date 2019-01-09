package cn.antke.bct.category.adapter;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;

import java.util.List;

import cn.antke.bct.R;
import cn.antke.bct.network.entities.CategoryTwoEntity;

/**
 * Created by liuzhichao on 2017/4/17.
 * 二级分类Adapter
 */
public class SecondCategoryAdapter extends BaseAdapterNew<CategoryTwoEntity> {

	private View.OnClickListener onClickListener;

	public SecondCategoryAdapter(Context context, List<CategoryTwoEntity> mDatas, View.OnClickListener onClickListener) {
		super(context, mDatas);
		this.onClickListener = onClickListener;
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.item_second_category;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		CategoryTwoEntity categoryEntity = getItem(position);
		if (categoryEntity != null) {
			TextView secondCategoryName = (TextView) convertView.findViewById(R.id.second_category_name);
			GridView secondCategoryDetail = (GridView) convertView.findViewById(R.id.second_category_detail);

			secondCategoryName.setText(categoryEntity.getName());
			ThirdCategoryAdapter thirdAdapter = new ThirdCategoryAdapter(getContext(), categoryEntity.getCategoryEntities(), onClickListener);
			secondCategoryDetail.setAdapter(thirdAdapter);
		}
	}
}

package cn.antke.bct.home.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.common.adapter.BaseAdapterNew;
import com.common.adapter.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.antke.bct.R;
import cn.antke.bct.network.entities.CategoryTwoEntity;
import cn.antke.bct.utils.ImageUtils;

/**
 * Created by zhaoweiwei on 2016/12/18.
 *
 */
public class HomeTypeAdapter extends BaseAdapterNew<CategoryTwoEntity> {

	public HomeTypeAdapter(Context context, List<CategoryTwoEntity> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.home_type_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		CategoryTwoEntity categoryEntity = getItem(position);

		SimpleDraweeView logo = ViewHolder.get(convertView, R.id.home_type_item_img);
		TextView name = ViewHolder.get(convertView,R.id.home_type_item_name);

		if (categoryEntity != null) {
			String url = categoryEntity.getImgUrl();
			ImageUtils.setImgUrl(logo, url);
			name.setText(categoryEntity.getName());
		}
	}
}

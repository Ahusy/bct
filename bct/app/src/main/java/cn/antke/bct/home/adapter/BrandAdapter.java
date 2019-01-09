package cn.antke.bct.home.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.common.widget.BaseRecycleAdapter;
import com.common.widget.RecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.antke.bct.R;
import cn.antke.bct.network.entities.BrandEntity;
import cn.antke.bct.utils.ImageUtils;


/**
 * Created by liuzhichao on 2017/1/6.
 * 品牌列表
 */
public class BrandAdapter extends BaseRecycleAdapter<BrandEntity> {

	private View.OnClickListener onClickListener;

	public BrandAdapter(List<BrandEntity> datas, View.OnClickListener onClickListener) {
		super(datas);
		this.onClickListener = onClickListener;
	}

	@Override
	public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new RecyclerViewHolder(parent, R.layout.item_brand);
	}

	@Override
	public void onBindViewHolder(RecyclerViewHolder holder, int position) {
		BrandEntity brandEntity = getItemData(position);

		SimpleDraweeView img = holder.getView(R.id.sdv_brand_logo);
		holder.setText(R.id.tv_brand_name, brandEntity.getName());
		ImageUtils.setSmallImg(img,brandEntity.getLogo());
		holder.setTag(R.id.ll_brand_layout, brandEntity);
		holder.setOnClickListener(R.id.ll_brand_layout, onClickListener);
	}
}

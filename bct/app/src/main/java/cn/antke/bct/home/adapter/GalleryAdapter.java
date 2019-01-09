package cn.antke.bct.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.antke.bct.R;
import cn.antke.bct.utils.CommonTools;
import cn.antke.bct.utils.ImageUtils;

/**
 * Created by zhaoweiwei on 2016/12/19.
 * 水平滚动
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

	private LayoutInflater mInflater;
	private List<String> mDatas;
	private OnItemClickListener onItemClickListener;
	private View.OnClickListener onClickListener;
	private int which;
	private Context context;

	public GalleryAdapter(Context context, List<String> datats, int which) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		mDatas = datats;
		this.which = which;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View arg0) {
			super(arg0);
		}

		FrameLayout rootView;
		SimpleDraweeView mImg;
		ImageView delete;
	}

	@Override
	public int getItemCount() {
		return mDatas.size();
	}

	/**
	 * 创建ViewHolder
	 */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = mInflater.inflate(R.layout.home_brand_item, viewGroup, false);
		ViewHolder viewHolder = new ViewHolder(view);
		viewHolder.rootView = (FrameLayout) view.findViewById(R.id.home_brand_item_layout);
		viewHolder.mImg = (SimpleDraweeView) view.findViewById(R.id.home_brand_item_img);
		ViewGroup.LayoutParams lp = viewHolder.rootView.getLayoutParams();
		if (1 == which) {//首页品牌
			lp.width = CommonTools.dp2px(context,90);
			lp.height = CommonTools.dp2px(context,60);
		} else if (2 == which){//订单中多商品显示
			lp.width = CommonTools.dp2px(context,92);
			lp.height = CommonTools.dp2px(context,141);
		}
		viewHolder.rootView.setLayoutParams(lp);
		return viewHolder;
	}

	/**
	 * 设置值
	 */
	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		ImageUtils.setSmallImg(holder.mImg, mDatas.get(position));
		if (null != onItemClickListener) {
			holder.itemView.setOnClickListener(view -> onItemClickListener.onItemClick(holder.itemView, position));
		}
	}

	public interface OnItemClickListener {
		void onItemClick(View view, int position);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
}

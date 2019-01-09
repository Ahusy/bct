package cn.antke.bct.home.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.loader.ImageLoader;

import cn.antke.bct.R;
import cn.antke.bct.utils.ImageUtils;

/**
 * Created by zhaoweiwei on 2016/12/18.
 * 首页轮播图加载图片
 */

public class BannerImageLoader extends ImageLoader {
	@Override
	public void displayImage(Context context, Object path, ImageView imageView) {
		ImageUtils.setSmallImg(imageView, (String) path);
	}

	@Override
	public ImageView createImageView(Context context) {
		SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		simpleDraweeView.setLayoutParams(layoutParams);

		GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
		GenericDraweeHierarchy hierarchy = builder
				.setFadeDuration(300)//淡入淡出
//				.setPlaceholderImage(R.drawable.default_banner_bg)
//				.setFailureImage(R.drawable.default_banner_bg)
				.setBackground(new ColorDrawable(context.getResources().getColor(R.color.common_spilt_line)))
				.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
				.build();
		simpleDraweeView.setHierarchy(hierarchy);
		return simpleDraweeView;
	}
}

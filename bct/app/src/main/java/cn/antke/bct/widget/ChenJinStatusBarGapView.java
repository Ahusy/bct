package cn.antke.bct.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import cn.antke.bct.base.BaseTabsDrawerActivity;
import cn.antke.bct.utils.CommonTools;

public class ChenJinStatusBarGapView extends View {

    private Context context;

    public ChenJinStatusBarGapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (BaseTabsDrawerActivity.isUseChenJinStatusBar()) {
            int statusBarHeight =  CommonTools.getStatusBarHeight(context);
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),statusBarHeight);
        } else {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST));
        }
    }
}

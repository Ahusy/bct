package cn.antke.bct.person.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.common.viewinject.annotation.ViewInject;
import com.facebook.drawee.view.SimpleDraweeView;

import cn.antke.bct.R;
import cn.antke.bct.base.ToolBarActivity;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.login.utils.UserCenter;
import cn.antke.bct.product.controller.ProductListActivity;
import cn.antke.bct.utils.ImageUtils;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.common.CommonConstant.EXTRA_ID;

/**
 * Created by zhaoweiwei on 2017/5/12.
 * 我的店铺
 */

public class StoreMyStoreActivity extends ToolBarActivity implements View.OnClickListener {

    @ViewInject(R.id.store_mystore_icon)
    private SimpleDraweeView myStoreIcon;
    @ViewInject(R.id.store_mystore_name)
    private TextView myStoreName;
    @ViewInject(R.id.store_store_info)
    private TextView storeInfo;
    @ViewInject(R.id.store_order_manager)
    private TextView orderManager;
    @ViewInject(R.id.store_good_manager)
    private TextView goodManager;
    @ViewInject(R.id.store_add_new_good)
    private TextView addNewGood;

    private String storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_store_mystore);
        ViewInjectUtils.inject(this);
        initView();
    }

    private void initView() {
        storeId = getIntent().getStringExtra(EXTRA_ID);
        setLeftTitle(getString(R.string.store_my_store));
        myStoreName.setText(UserCenter.getStoreName(this));
        ImageUtils.setImgUrl(myStoreIcon, UserCenter.getStorePic(this));

        storeInfo.setOnClickListener(this);
        orderManager.setOnClickListener(this);
        goodManager.setOnClickListener(this);
        addNewGood.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.store_store_info://店铺信息
                StoreDetailActivity.startStoreDetailActivity(this);
                break;
            case R.id.store_order_manager://订单管理
                startActivity(new Intent(this, StoreOrderActivity.class).putExtra(EXTRA_ID,storeId));
                break;
            case R.id.store_good_manager://商品管理
                ProductListActivity.startProductListActivity(this, CommonConstant.FROM_PERSONAL_PRODUCT, "", "商品管理", "");
                break;
            case R.id.store_add_new_good://添加新商品
                startActivity(new Intent(this, GoodEditActivity.class));
                break;
        }
    }
}

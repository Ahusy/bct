package cn.antke.bct.person.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.viewinject.annotation.ViewInject;

import cn.antke.bct.R;
import cn.antke.bct.base.ToolBarActivity;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.IntegralEntity;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.common.CommonConstant.REQUEST_NET_ONE;
import static cn.antke.bct.common.CommonConstant.TYPE_1;
import static cn.antke.bct.common.CommonConstant.TYPE_2;
import static cn.antke.bct.common.CommonConstant.TYPE_3;
import static cn.antke.bct.common.CommonConstant.TYPE_4;
import static cn.antke.bct.common.CommonConstant.TYPE_6;

/**
 * Created by zhaoweiwei on 2017/5/21.
 * 额度
 */

public class IntegralActivity extends ToolBarActivity implements View.OnClickListener {
    @ViewInject(R.id.integral_useable_ll)
    private LinearLayout integralUseableLl;
    @ViewInject(R.id.integral_useable)
    private TextView integralUseable;
    @ViewInject(R.id.integral_shop_ll)
    private LinearLayout integralShopLl;
    @ViewInject(R.id.integral_shop)
    private TextView integralShop;
    @ViewInject(R.id.integral_aside_ll)
    private LinearLayout integralAsideLl;
    @ViewInject(R.id.integral_aside)
    private TextView integralAside;
    @ViewInject(R.id.integral_multi_function_ll)
    private LinearLayout integralMultiLl;
    @ViewInject(R.id.integral_multi_function)
    private TextView integralMulti;
    @ViewInject(R.id.integral_share_ll)
    private LinearLayout integralShareLl;
    @ViewInject(R.id.integral_share)
    private TextView integralShare;
    @ViewInject(R.id.integral_give)
    private TextView integralGive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_integral);
        ViewInjectUtils.inject(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        showProgressDialog();
        requestHttpData(Constants.Urls.URL_POST_INTEGRALNUM_QUERY, REQUEST_NET_ONE, FProtocol.HttpMethod.POST, null);
    }

    private void initView() {
        setLeftTitle(getString(R.string.store_good_integral));
        integralUseableLl.setOnClickListener(this);
        integralShopLl.setOnClickListener(this);
        integralAsideLl.setOnClickListener(this);
        integralMultiLl.setOnClickListener(this);
        integralShareLl.setOnClickListener(this);
        integralGive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //额度类型 1、可用额度；2、待用额度；3、购物额度；4、分享额度；5、红分；6、多功能额度；7、债券
            case R.id.integral_useable_ll:
                IntegralOrderActivity.startIntegralOrderActivity(this, TYPE_1);
                break;
            case R.id.integral_shop_ll:
                IntegralOrderActivity.startIntegralOrderActivity(this, TYPE_3);
                break;
            case R.id.integral_aside_ll:
                IntegralOrderActivity.startIntegralOrderActivity(this, TYPE_2);
                break;
            case R.id.integral_multi_function_ll:
                IntegralOrderActivity.startIntegralOrderActivity(this, TYPE_6);
                break;
            case R.id.integral_share_ll:
                IntegralOrderActivity.startIntegralOrderActivity(this, TYPE_4);
                break;
            case R.id.integral_give:
                IntegralGiveActivity.startIntegralGiveActivity(this);
                break;
        }
    }

    @Override
    protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
        IntegralEntity entity = Parsers.getIntegralNum(data);
        if (entity != null) {
            setIntegralData(integralUseable,entity.getIntegralUsable());
            setIntegralData(integralShop,entity.getIntegralShop());
            setIntegralData(integralAside,entity.getIntegralFreeze());
            setIntegralData(integralMulti,entity.getIntegralFunction());
            setIntegralData(integralShare,entity.getIntegralShare());
        }
    }

    private void setIntegralData(TextView textView,String num) {
        if (null!=textView) {
            if (StringUtil.isEmpty(num)) {
                textView.setText(getString(R.string.product_sell_integral, "0"));
            } else {
                textView.setText(getString(R.string.product_sell_integral, num));
            }
        }
    }
}

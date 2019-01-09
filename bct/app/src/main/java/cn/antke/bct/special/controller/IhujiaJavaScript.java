package cn.antke.bct.special.controller;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.common.network.FProtocol;
import com.common.utils.LogUtil;
import com.common.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.IdentityHashMap;

import cn.antke.bct.base.WebViewActivity;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.login.controller.LoginActivity;
import cn.antke.bct.login.utils.UserCenter;
import cn.antke.bct.product.controller.ProductDetailActivity;
import cn.antke.bct.product.controller.ProductListActivity;

import static cn.antke.bct.network.Constants.Urls.YOUXI_PAY;

/**
 * Created by liuzhichao on 2017/4/18.
 * 活动和店铺装修JS交互接口
 */
public class IhujiaJavaScript {

    private static final String TAG = IhujiaJavaScript.class.getSimpleName();

    private Context context;
    private String storeId;
    private IcallbackJS icallbackJS;

    public IhujiaJavaScript(Context context) {
        this.context = context;
    }

    public IhujiaJavaScript setStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public IhujiaJavaScript setIcallbackJS(IcallbackJS icallbackJS) {
        this.icallbackJS = icallbackJS;
        return this;
    }

    @JavascriptInterface
    public void open(String params) {
        if (StringUtil.isEmpty(params)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(params);
            String type = jsonObject.optString("type");
            switch (type) {
                case "goodsdetail": {
                    String goodsId = jsonObject.optString("goods_id");
                    ProductDetailActivity.startProductDetailActivity(context, goodsId);
                    break;
                }
                case "goodslist":
                    ProductListActivity.startProductListActivity(context, -1, storeId, "", "");
                    break;
                case "coupon": {
                    if (UserCenter.isLogin(context)) {
                        String couponId = jsonObject.optString("coupon_id");
                        String title = jsonObject.optString("title");
                        String desc = jsonObject.optString("desc");
                    } else {
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                    break;
                }
                case "getUserId": {
                    if (UserCenter.isLogin(context)) {
                        if (icallbackJS != null) {
                            icallbackJS.callJS("javascript:setUserId('" + UserCenter.getUserId(context) + "')");
                        } else {
                            LogUtil.e(TAG, "未设置JS回调接口");
                        }
                    } else {
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void payMoney(int money, int userId, int storeId) {
        LogUtil.d("wangfan","userID"+userId);
        ((WebViewActivity) context).onPayMoney(money,userId,storeId);
    }

}

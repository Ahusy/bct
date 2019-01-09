package cn.antke.bct.base;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.common.network.FProtocol;
import com.common.network.HttpUtil;
import com.common.utils.EnCryptionUtils;
import com.common.utils.LogUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;

import java.util.IdentityHashMap;

import cn.antke.bct.R;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.login.controller.LoginActivity;
import cn.antke.bct.login.utils.UserCenter;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.CreateOrderEntity;
import cn.antke.bct.network.entities.Entity;
import cn.antke.bct.network.entities.PayEntity;
import cn.antke.bct.pay.util.PayUtils;
import cn.antke.bct.special.controller.IcallbackJS;
import cn.antke.bct.special.controller.IhujiaJavaScript;

import static cn.antke.bct.common.CommonConstant.EXTRA_FROM;
import static cn.antke.bct.common.CommonConstant.REQUEST_NET_SUCCESS;
import static cn.antke.bct.network.Constants.Urls.YOUXI_PAY;

/**
 * 公用WebView加载页
 */
public class WebViewActivity extends ToolBarActivity implements IcallbackJS {

    private ProgressBar mHorizontalProgress;
    private WebView mWebView;
    private ImageView backBtn;
    private boolean mIsImmediateBack = false;
    private boolean mIsLeftBtnDisplay = true;
    private boolean mIsRightBtnDisplay = true;
    private int from;
    private String url;
    private String title;
    private Handler refreshProgressHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (msg.arg1 >= 100) {
                        if (mHorizontalProgress != null) {
                            mHorizontalProgress.setVisibility(View.GONE);
                        }
                    } else {
                        if (mHorizontalProgress != null && msg.arg1 >= 0) {
                            mHorizontalProgress.setVisibility(View.VISIBLE);
                            mHorizontalProgress.setProgress(msg.arg1);
                        }
                    }
                    break;
                case 1:
                    String jsMethod = (String) msg.obj;
                    if (mWebView != null && !StringUtil.isEmpty(jsMethod)) {
                        mWebView.loadUrl(jsMethod);
                    }
                    break;
            }
        }
    };
    private CreateOrderEntity createOrder;
    private BroadcastReceiver broadcastReceiver;
    private int money;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.act_webview);

        url = getIntent().getStringExtra(CommonConstant.EXTRA_URL);
        //url = "file:///android_asset/test.html";
        title = getIntent().getStringExtra(CommonConstant.EXTRA_TITLE);
        from = getIntent().getIntExtra(EXTRA_FROM, 0);
        if (title == null) {
            title = "";
        }
        setLeftTitle(title);

        mHorizontalProgress = (ProgressBar) findViewById(R.id.progress_horizontal);
        mWebView = (WebView) findViewById(R.id.webview);
        // 设置支持JavaScript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new IhujiaJavaScript(this).setIcallbackJS(this), "vjia");
        webSettings.setSupportZoom(true);
//		webSettings.setDatabaseEnabled(true);
//		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationEnabled(true);
//		webSettings.setGeolocationDatabasePath(dir);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);//图片调整到适合WebView的大小
        webSettings.setLoadWithOverviewMode(true);//缩放至屏幕大小
//		webSettings.setAppCacheEnabled(true);
//		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                resend.sendToTarget();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(view.getContext(), getString(R.string.custom_net_error), Toast.LENGTH_SHORT).show();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (refreshProgressHandler != null) {
                    if (refreshProgressHandler.hasMessages(0)) {
                        refreshProgressHandler.removeMessages(0);
                    }
                    Message mMessage = refreshProgressHandler.obtainMessage(0, newProgress, 0, null);
                    refreshProgressHandler.sendMessageDelayed(mMessage, 100);
                }
            }

            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        url = check(url);
        mWebView.loadUrl(url);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra(CommonConstant.EXTRA_CODE, -1);
                switch (intExtra) {
                    case 0:
                        ToastUtil.shortShow(context, "支付成功");
                        LogUtil.d("wangfan","支付成功");
                        mWebView.loadUrl("javascript:paySuccess('" + Integer.parseInt(createOrder.getUserId()) + "','" + createOrder.getOrderId() + "','" + money+"')");
                        break;
                    case -1:
                        ToastUtil.shortShow(context, "支付失败");
                        LogUtil.d("wangfan","支付失败");
                        mWebView.loadUrl("javascript:payFail('" + Integer.parseInt(createOrder.getUserId()) + "')");
                        break;
                    case -2:
                        ToastUtil.shortShow(context, "取消支付");
                        LogUtil.d("wangfan","取消支付");
                        mWebView.loadUrl("javascript:payFail('" + Integer.parseInt(createOrder.getUserId()) + "')");
                        break;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(CommonConstant.ACTION_BROADCAST_PAY_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private String check(String url) {
        if(!TextUtils.isEmpty(url)&&!url.contains("gou.wjike.com")){
            return url;
        }

        if(url.contains("gou.wjike.com")&&TextUtils.isEmpty(UserCenter.getUserId(this))){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        url += ("&user_id="+UserCenter.getUserId(this));
        url+=("&mobile="+UserCenter.getPhone(this));
        url+=("&sign="+ EnCryptionUtils.MD5(UserCenter.getPhone(this)+"goucomwjike"));
        return url;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        if (backBtn != null) {
            if (mIsLeftBtnDisplay) {
                backBtn.setVisibility(View.VISIBLE);
            } else {
                backBtn.setVisibility(View.GONE);
            }

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(backBtn.getApplicationWindowToken(), 0);

                    if (mIsImmediateBack) {
                        onBackPressed();
                    } else {
                        if (mWebView.canGoBack()) {
                            mWebView.goBack();
                        } else {
                            onBackPressed();
                        }
                    }
                }
            });
        }

        final TextView closeBtn = null;
        if (closeBtn != null) {
            if (mIsRightBtnDisplay) {
                closeBtn.setVisibility(View.VISIBLE);
            } else {
                closeBtn.setVisibility(View.GONE);
            }
            closeBtn.setText(getString(R.string.close));
            closeBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(closeBtn.getApplicationWindowToken(), 0);
                    onBackPressed();
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void callJS(String params) {
        Message message = refreshProgressHandler.obtainMessage(1, params);
        refreshProgressHandler.sendMessage(message);
    }

    public void onPayMoney(int money,int userId,int storeId){
        this.money = money;
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put("order_total", "" + money);
        params.put("store_id", "" + storeId);
        params.put("integral_type", "1");
        params.put("channel", "20");
        params.put(HttpUtil.Config.INTERFACE_NAME,"");
        requestHttpData(YOUXI_PAY, CommonConstant.REQUEST_NET_ONE, FProtocol.HttpMethod.POST, params);
    }

    @Override
    public void success(int requestCode, String data) {
        Entity result = Parsers.getResult(data);
        if (REQUEST_NET_SUCCESS.equals(result.getResultCode())) {
            switch (requestCode) {
                case CommonConstant.REQUEST_NET_ONE: {
                    createOrder = Parsers.getCreateOrder(data);
                    IdentityHashMap<String, String> params = new IdentityHashMap<>();
                    params.put("order_id", createOrder.getOrderId());
                    params.put("pay_type", "2");//1：支付宝、2：微信
                    params.put("user_id", createOrder.getUserId());
                    params.put(HttpUtil.Config.INTERFACE_NAME,"");
                    requestHttpData(Constants.Urls.URL_YOUXI_PAY, CommonConstant.REQUEST_NET_TWO, FProtocol.HttpMethod.POST, params);
                    break;
                }
                case CommonConstant.REQUEST_NET_TWO: {
                    if (CommonConstant.REQUEST_NET_SUCCESS.equals(result.getResultCode())) {
                        PayEntity pay = Parsers.getPay(data);
                        PayUtils.startWXPay(this, pay.getPayId());
                    }
                    break;
                }
            }
        }
    }
}

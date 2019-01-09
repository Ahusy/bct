package cn.antke.bct.special.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.common.utils.LogUtil;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;

import cn.antke.bct.R;
import cn.antke.bct.base.BaseFragment;
import cn.antke.bct.main.controller.MainActivity;
import cn.antke.bct.utils.ViewInjectUtils;

/**
 * Created by liuzhichao on 2016/12/16.
 * 我的
 */
public class H5TwoFragment extends BaseFragment implements View.OnClickListener,IcallbackJS {
    private View view;
    private MainActivity mActivity;
    private WebView mWebView;

    private Handler refreshProgressHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String jsMethod = (String) msg.obj;
                    if (mWebView != null && !StringUtil.isEmpty(jsMethod)) {
                        mWebView.loadUrl(jsMethod);
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fg_webview, null);
            ViewInjectUtils.inject(this, view);
            initView(view);
        }
        ViewGroup mViewParent = (ViewGroup) view.getParent();
        if (mViewParent != null) {
            mViewParent.removeView(view);
        }
        return view;
    }

    private void initView(View view) {
        mWebView = (WebView) view.findViewById(R.id.webview);
        // 设置支持JavaScript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new IhujiaJavaScript(mActivity).setIcallbackJS(this), "vjia");
        webSettings.setSupportZoom(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);//图片调整到适合WebView的大小
        webSettings.setLoadWithOverviewMode(true);//缩放至屏幕大小
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.e("H5Fragment", "url=" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                resend.sendToTarget();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                ToastUtil.shortShow(getActivity(),getString(R.string.custom_net_error));
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
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
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.loadUrl("http://api.fyznyw.com/activity/index.html");
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void callJS(String params) {
        Message message = refreshProgressHandler.obtainMessage(1, params);
        refreshProgressHandler.sendMessage(message);
    }
}

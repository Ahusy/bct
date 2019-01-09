package cn.antke.bct.login.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;

import java.util.IdentityHashMap;

import cn.antke.bct.R;
import cn.antke.bct.base.ToolBarActivity;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.Entity;
import cn.antke.bct.network.entities.RecommenderEntity;
import cn.antke.bct.utils.DownTimeUtil;
import cn.antke.bct.utils.InputUtil;
import cn.antke.bct.utils.VerifyUtils;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.common.CommonConstant.REQUEST_NET_ONE;
import static cn.antke.bct.common.CommonConstant.REQUEST_NET_THREE;
import static cn.antke.bct.common.CommonConstant.REQUEST_NET_TWO;

/**
 * Created by zhaoweiwei on 2017/5/3.
 * 忘记密码
 */

public class ForgetPwdActivity extends ToolBarActivity implements View.OnClickListener {
    @ViewInject(R.id.forget_usercode)
    private EditText forgetUsercode;
    @ViewInject(R.id.forget_phone)
    private TextView forgetPhone;
    @ViewInject(R.id.forget_password)
    private EditText newPassword;
    @ViewInject(R.id.forget_password_re)
    private EditText newPasswordRe;
    @ViewInject(R.id.forget_verify_code)
    private EditText verifyCodeEt;
    @ViewInject(R.id.forget_get_verify_code)
    private TextView getVerifyCode;
    @ViewInject(R.id.forget_confirm)
    private TextView confirm;

    private String phone;
    private String password;
    private String verifyCode;
    private DownTimeUtil downTimeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_froget_pwd);
        ViewInjectUtils.inject(this);
        setLeftTitle(getString(R.string.forget_password_title));
        getVerifyCode.setOnClickListener(this);
        confirm.setOnClickListener(this);
        InputUtil.editIsEmpty(confirm, forgetUsercode, newPasswordRe, newPassword, verifyCodeEt);
        downTimeUtil = new DownTimeUtil(this);
        downTimeUtil.initCountDownTime(getVerifyCode);

        getPhone();
    }

    private void getPhone() {
        forgetUsercode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String userCode = forgetUsercode.getText().toString();
                if (7 == userCode.length()) {
                    getRecommendInfo(userCode);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        String userCode = forgetUsercode.getText().toString();
        password = newPassword.getText().toString();
        String paswordRe = newPasswordRe.getText().toString();
        verifyCode = verifyCodeEt.getText().toString();
        switch (v.getId()) {
            case R.id.forget_get_verify_code:
                getVerifyCode();
                downTimeUtil.countDownTimer.start();
                break;
            case R.id.forget_confirm:
                if (paswordRe.equals(password)) {
                    resetPassword(userCode);
                } else {
                    ToastUtil.shortShow(this, getString(R.string.login_change_pwd_different));
                }
                break;
        }
    }

    private void resetPassword(String usercode) {
        if (VerifyUtils.isPassword(this, password)) {
            showProgressDialog();
            IdentityHashMap<String, String> params = new IdentityHashMap<>();
            params.put("loginName", phone);
            params.put("user_code", usercode);
            params.put("password", password);
            params.put("verifiationCode", verifyCode);
            params.put("type", "1");
            requestHttpData(Constants.Urls.URL_POST_RESET_PASSWORD, REQUEST_NET_TWO, FProtocol.HttpMethod.POST, params);
        }
    }

    private void getVerifyCode() {
        showProgressDialog();
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put("token", "");
        params.put("loginName", phone);
        requestHttpData(Constants.Urls.URL_POST_SMS_CODE, REQUEST_NET_ONE, FProtocol.HttpMethod.POST, params);
    }

    private void getRecommendInfo(String usercode) {
        showProgressDialog();
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put("recommender", usercode);
        requestHttpData(Constants.Urls.URL_POST_USER_RECOMMEND_CODE, REQUEST_NET_THREE, FProtocol.HttpMethod.POST, params);
    }

    @Override
    public void success(int requestCode, String data) {
        super.success(requestCode, data);
        Entity entity = Parsers.getResult(data);
        if (CommonConstant.REQUEST_NET_SUCCESS.equals(entity.getResultCode())) {
            closeProgressDialog();
        } else {
            closeProgressDialog();
            ToastUtil.shortShow(this, entity.getResultMsg());
            getVerifyCode.setEnabled(true);
            getVerifyCode.setText(getString(R.string.register_get_verify_code));
            downTimeUtil.countDownTimer.cancel();
        }
        super.success(requestCode, data);
    }

    @Override
    public void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
        closeProgressDialog();
        switch (requestCode) {
            case REQUEST_NET_ONE://获取验证码
                break;
            case REQUEST_NET_TWO://重新设置新密码
//                UserEntity userEntity = Parsers.getUserInfo(data);
//                UserCenter.savaUserInfo(this, userEntity);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case REQUEST_NET_THREE://根据用户编号获取用户手机号
                RecommenderEntity recommenderEntity = Parsers.getRecommender(data);
                phone = recommenderEntity.getPhone();
                forgetPhone.setText(phone);
                break;
        }
    }
}

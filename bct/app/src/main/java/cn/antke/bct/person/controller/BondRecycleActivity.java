package cn.antke.bct.person.controller;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.utils.StringUtil;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;

import java.util.IdentityHashMap;

import cn.antke.bct.R;
import cn.antke.bct.base.ToolBarActivity;
import cn.antke.bct.login.utils.UserCenter;
import cn.antke.bct.network.Constants;
import cn.antke.bct.pay.controller.OnlinePayActivity;
import cn.antke.bct.utils.DialogUtils;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.common.CommonConstant.EXTRA_ENTITY;
import static cn.antke.bct.common.CommonConstant.REQUEST_NET_TWO;

/**
 * Created by zhaoweiwei on 2017/5/19.
 * 债券回收
 */

public class BondRecycleActivity extends ToolBarActivity {
    @ViewInject(R.id.bond_recycle_usercode)
    private TextView userCode;
    @ViewInject(R.id.bond_recycle_bond)
    private EditText bond;
    @ViewInject(R.id.bond_recycle_confirm)
    private TextView bondConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bond_recycle);
        ViewInjectUtils.inject(this);
        setLeftTitle(getString(R.string.bond_recycle));
        String bondNum = getIntent().getStringExtra(EXTRA_ENTITY);
        userCode.setText(UserCenter.getUserCode(this));
        bond.setHint(getString(R.string.bond_most_recycle, bondNum));
        bond.setHintTextColor(ContextCompat.getColor(this, R.color.text_introduce_color));
        bondConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bondNumInput = bond.getText().toString();
                if (!StringUtil.isEmpty(bondNumInput)) {
                    int bondNumInt = Integer.parseInt(bondNumInput);
                    if (bondNumInt > 0) {
                        DialogUtils.showTwoBtnDialog(BondRecycleActivity.this,
                                getString(R.string.bond_recycle),
                                getString(R.string.bond_is_recycle),
                                null, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showProgressDialog();
                                        IdentityHashMap<String, String> params = new IdentityHashMap<>();
                                        params.put("bond", bondNumInput);
                                        requestHttpData(Constants.Urls.URL_POST_BOND_RECYCLE, REQUEST_NET_TWO, FProtocol.HttpMethod.POST, params);
                                        DialogUtils.closeDialog();
                                    }
                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DialogUtils.closeDialog();
                                    }
                                });
                    } else {
                        ToastUtil.shortShow(BondRecycleActivity.this, getString(R.string.bond_mine_num));
                    }
                }
            }
        });
    }

    @Override
    protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
        finish();
    }
}

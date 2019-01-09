package cn.antke.bct.person.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.common.network.FProtocol;
import com.common.viewinject.annotation.ViewInject;

import java.util.IdentityHashMap;
import java.util.List;

import cn.antke.bct.R;
import cn.antke.bct.base.ToolBarActivity;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.LogisticEnity;
import cn.antke.bct.network.entities.LogisticItemEntity;
import cn.antke.bct.person.adapter.LogisticDetailAdapter;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.common.CommonConstant.EXTRA_ENTITY;
import static cn.antke.bct.common.CommonConstant.REQUEST_NET_ONE;

/**
 * Created by zhaoweiwei on 2017/5/23.
 * 物流详情
 */

public class LogisticDetailActivity extends ToolBarActivity {
    @ViewInject(R.id.logistic_company)
    private TextView logisticCompany;
    @ViewInject(R.id.logistic_number)
    private TextView logisticNumber;
    @ViewInject(R.id.logistic_phone)
    private TextView logisticPhone;
    @ViewInject(R.id.logistic_list)
    private ListView logisticList;

    public static void startLogisticActivity(Context context, String orderCode) {
        Intent intent = new Intent(context, LogisticDetailActivity.class);
        intent.putExtra(EXTRA_ENTITY, orderCode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_logistic_detail);
        ViewInjectUtils.inject(this);
        setLeftTitle(getString(R.string.person_order_logistic_detail));
        String orderCode = getIntent().getStringExtra(EXTRA_ENTITY);
        loadData(orderCode);
    }

    private void loadData(String orderCode) {
        showProgressDialog();
        IdentityHashMap<String, String> params = new IdentityHashMap<>();
        params.put("order_code", orderCode);
        requestHttpData(Constants.Urls.URL_POST_QUERY_LOGISTICS, REQUEST_NET_ONE, FProtocol.HttpMethod.POST, params);
    }

    @Override
    protected void parseData(int requestCode, String data) {
        super.parseData(requestCode, data);
        LogisticEnity logisticEntity = Parsers.getLogistics(data);
        if (logisticEntity != null) {
            logisticCompany.setText(logisticEntity.getLogisticName());
            logisticNumber.setText(logisticEntity.getLogisticNo());
            logisticPhone.setText(logisticEntity.getLogisticPhone());

            List<LogisticItemEntity> entities = logisticEntity.getLogisticItemEntities();
            LogisticDetailAdapter adapter = new LogisticDetailAdapter(this, entities);
            logisticList.setAdapter(adapter);
        }
    }
}

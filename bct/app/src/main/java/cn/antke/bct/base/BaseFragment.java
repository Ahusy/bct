package cn.antke.bct.base;

import android.content.Intent;
import android.os.Bundle;

import com.common.interfaces.IActivityHelper;
import com.common.network.FProtocol;
import com.common.ui.FBaseFragment;
import com.common.utils.ToastUtil;

import java.util.IdentityHashMap;

import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.login.controller.LoginActivity;
import cn.antke.bct.login.utils.UserCenter;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.Entity;
import cn.antke.bct.utils.ConfigUtils;

public class BaseFragment extends FBaseFragment implements IActivityHelper {

    private String baseTitle = "";

    public String getTitle() {
        return baseTitle;
    }

    public void setTitle(int titleId) {
        baseTitle = getString(titleId);
    }

    public void setTitle(String title) {
        baseTitle = title;
    }

    @Override
    public void requestHttpData(String path, int requestCode) {
        if (path.contains("?")) {
            path += ("&user_id=" + UserCenter.getUserId(getActivity()) + "&site_id=" + UserCenter.getSiteId(getActivity()) + "&language=" + ConfigUtils.getPreArea(getActivity()));
        } else {
            path += ("?user_id=" + UserCenter.getUserId(getActivity()) + "&site_id=" + UserCenter.getSiteId(getActivity()) + "&language=" + ConfigUtils.getPreArea(getActivity()));
        }
        super.requestHttpData(path, requestCode);
    }


    @Override
    public void requestHttpData(String path, int requestCode, FProtocol.HttpMethod method, IdentityHashMap<String, String> postParameters) {
        if (postParameters == null) {
            postParameters = new IdentityHashMap<>();
        }
        postParameters.put("user_id", UserCenter.getUserId(getActivity()));
        postParameters.put("site_id", UserCenter.getSiteId(getActivity()));
        postParameters.put("language", ConfigUtils.getPreLanguage(getActivity()));
        super.requestHttpData(path, requestCode, method, postParameters);
    }

    @Override
    public void success(int requestCode, String data) {
        Entity entity = Parsers.getResult(data);
        if (CommonConstant.REQUEST_NET_SUCCESS.equals(entity.getResultCode())) {
            parseData(requestCode, data);
        } else if ("999999".equals(entity.getResultCode())) {
            //强退
            UserCenter.cleanLoginInfo(getActivity());
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            closeProgressDialog();
            ToastUtil.shortShow(getActivity(), entity.getResultMsg());
        }
    }

    /**
     * 请求成功后实际处理数据的方法
     */
    protected void parseData(int requestCode, String data) {
        closeProgressDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("baseTitle", baseTitle);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            baseTitle = savedInstanceState.getString("baseTitle", getTitle());
        }
    }
}

package cn.antke.bct.category.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cn.antke.bct.R;
import cn.antke.bct.base.BaseFragment;
import cn.antke.bct.base.WebViewActivity;
import cn.antke.bct.category.adapter.SecondCategoryAdapter;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.home.controller.SearchActivity;
import cn.antke.bct.login.controller.LoginActivity;
import cn.antke.bct.login.utils.UserCenter;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.BannerEntity;
import cn.antke.bct.network.entities.CategoryTwoEntity;
import cn.antke.bct.network.entities.Entity;
import cn.antke.bct.network.entities.PagesEntity;
import cn.antke.bct.network.entities.ShopCarEntity;
import cn.antke.bct.product.controller.ProductDetailActivity;
import cn.antke.bct.product.controller.ProductListActivity;
import cn.antke.bct.product.controller.ShopCarActivity;
import cn.antke.bct.product.controller.StoreDetailActivity;
import cn.antke.bct.utils.ImageUtils;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.common.CommonConstant.REQUEST_NET_SUCCESS;

/**
 * Created by liuzhichao on 2017/4/27.
 * 分类
 */
public class CategoryTwoFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private View view;
    private static final int REQUEST_NET_CATEGORY_LIST = 10;
    private ConvenientBanner banner;
    @ViewInject(R.id.lv_category_main)
    private ListView lvCategoryMain;
    @ViewInject(R.id.lv_category_detail)
    private ListView lvCategoryDetail;
    @ViewInject(R.id.fl_home_search)
    private View flHomeSearch;
    @ViewInject(R.id.iv_home_scan)
    private ImageView ivHomeScan;

    private List<CategoryTwoEntity> categoryList;
    private List<BannerEntity> bannerEntities;
    private int preCategory = 0;
    private SecondCategoryAdapter secondCategoryAdapter;
    private ArrayAdapter<String> leftAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_categroy_two, null);
            ViewInjectUtils.inject(this, view);
            initView();
        }
        loadData();
        ViewGroup mViewParent = (ViewGroup) view.getParent();
        if (mViewParent != null) {
            mViewParent.removeView(view);
        }
        return view;
    }

    private void initView() {
        View topView = View.inflate(getActivity(), R.layout.ban_ner, null);
        banner = (ConvenientBanner) topView.findViewById(R.id.categrory_banner);
//		lvCategoryDetail.addHeaderView(topView);
        flHomeSearch.setOnClickListener(this);
        ivHomeScan.setOnClickListener(this);
        //默认选中第一个
        lvCategoryMain.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            View firstView = lvCategoryMain.getChildAt(preCategory);
            if (firstView != null) {
                firstView.setSelected(true);
            }
        });

        banner.setPageIndicator(new int[]{R.drawable.dot_dark, R.drawable.dot_light})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .startTurning(3000);
        banner.setOnItemClickListener(position -> {
            if (bannerEntities != null && bannerEntities.size() > position) {
                BannerEntity bannerEntity = bannerEntities.get(position);
                switch (bannerEntity.getType()) {
                    case "1"://网页
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra(CommonConstant.EXTRA_TITLE, bannerEntity.getTitle());
                        intent.putExtra(CommonConstant.EXTRA_URL, bannerEntity.getLinkUrl());
                        startActivity(intent);
                        break;
                    case "2"://店铺
                        StoreDetailActivity.startStoreDetailActivity(getActivity(), bannerEntity.getLinkUrl());
                        break;
                    case "3"://商品
                        ProductDetailActivity.startProductDetailActivity(getActivity(), bannerEntity.getLinkUrl());
                        break;
                }
            }
        });
        lvCategoryMain.setOnItemClickListener(this);
    }

    private void loadData() {
        showProgressDialog();
        requestHttpData(Constants.Urls.URL_POST_CATEGORY_LIST, REQUEST_NET_CATEGORY_LIST, FProtocol.HttpMethod.POST, null);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserCenter.isLogin(getContext())) {
            requestHttpData(Constants.Urls.URL_POST_SHOP_CAR_LIST, CommonConstant.REQUEST_ACT_THREE, FProtocol.HttpMethod.POST, null);
        }
    }

    @Override
    public void success(int requestCode, String data) {
        closeProgressDialog();
        Entity result = Parsers.getResult(data);
        if (REQUEST_NET_SUCCESS.equals(result.getResultCode())) {
            if (REQUEST_NET_CATEGORY_LIST == requestCode) {
                categoryList = Parsers.getCategoryList(data);
                bannerEntities = Parsers.getCategoryBanner(data);
//				banner.setPages(new CBViewHolderCreator<ImageHolder>() {
//
//					@Override
//					public ImageHolder createHolder() {
//						return new ImageHolder();
//					}
//				}, bannerEntities);
                if (categoryList != null) {
                    List<String> mainCategories = new ArrayList<>();
                    for (CategoryTwoEntity CategoryTwoEntity : categoryList) {
                        mainCategories.add(CategoryTwoEntity.getName());
                    }
                    leftAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_category, mainCategories);
                    lvCategoryMain.setAdapter(leftAdapter);

                    List<CategoryTwoEntity> categoryEntities = new ArrayList<>();
                    CategoryTwoEntity categoryTwoEntity = categoryList.get(0);
                    if (categoryTwoEntity != null) {
                        categoryEntities.addAll(categoryTwoEntity.getCategoryEntities());
                    }
                    secondCategoryAdapter = new SecondCategoryAdapter(getActivity(), categoryEntities, this);
                    lvCategoryDetail.setAdapter(secondCategoryAdapter);
                } else {
                    ToastUtil.shortShow(getActivity(), "暂无数据");
                }
            } else if (CommonConstant.REQUEST_ACT_THREE == requestCode) {
                PagesEntity<ShopCarEntity> shopCarPage = Parsers.getShopCarPage(data);
                List<ShopCarEntity> datas = shopCarPage.getDatas();
                if (datas != null && datas.size() != 0) {
                    ivHomeScan.setImageResource(R.drawable.car_02);
                } else {
                    ivHomeScan.setImageResource(R.drawable.car_01);
                }
            }
        } else {
            ToastUtil.shortShow(getActivity(), result.getResultMsg());
        }
    }

    @Override
    public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
        closeProgressDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        preCategory = position;
        if (categoryList != null) {
            CategoryTwoEntity CategoryTwoEntity = categoryList.get(position);
            List<CategoryTwoEntity> categoryEntities = new ArrayList<>();
            if (CategoryTwoEntity != null) {
                categoryEntities.addAll(CategoryTwoEntity.getCategoryEntities());
            }
            secondCategoryAdapter = new SecondCategoryAdapter(getActivity(), categoryEntities, this);
            lvCategoryDetail.setAdapter(secondCategoryAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_third_category:
                String categoryTwoEntity = (String) v.getTag();
                if (!TextUtils.isEmpty(categoryTwoEntity)) {
                    String[] split = categoryTwoEntity.split(":");
                    ProductListActivity.startProductListActivity(getActivity(), -1, split[0], split[1], "");
                }
                break;
            case R.id.iv_home_scan:
                if (UserCenter.isLogin(getActivity())) {
                    ShopCarActivity.startShopCarActivity(getActivity());
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.fl_home_search:
                SearchActivity.startSearchActivity(getActivity());
        }
    }

    private class ImageHolder implements Holder<BannerEntity> {

        private ImageView sdvBannerPic;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null);
            sdvBannerPic = (ImageView) view.findViewById(R.id.sdv_banner_pic);
            return view;
        }

        @Override
        public void UpdateUI(Context context, int position, BannerEntity data) {
            ImageUtils.setSmallImg(sdvBannerPic, data.getImgUrl());
        }
    }
}

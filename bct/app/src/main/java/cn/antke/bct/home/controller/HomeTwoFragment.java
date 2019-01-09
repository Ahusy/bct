package cn.antke.bct.home.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.Holder;
import com.common.network.FProtocol;
import com.common.utils.AnalysisUtil;
import com.common.utils.ToastUtil;
import com.common.view.GridViewForInner;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.FootLoadingListView;
import com.common.widget.PullToRefreshBase;
import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import cn.antke.bct.BuildConfig;
import cn.antke.bct.R;
import cn.antke.bct.base.BaseFragment;
import cn.antke.bct.base.WebViewActivity;
import cn.antke.bct.common.CommonConfig;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.draw.controller.DrawListActivity;
import cn.antke.bct.home.adapter.BannerImageLoader;
import cn.antke.bct.home.adapter.GalleryAdapter;
import cn.antke.bct.home.adapter.HomeRecommendAdapter;
import cn.antke.bct.home.adapter.HomeTypeAdapter;
import cn.antke.bct.home.adapter.PlateAdapter;
import cn.antke.bct.home.listener.OnItemHeightGetListener;
import cn.antke.bct.login.controller.LoginActivity;
import cn.antke.bct.login.utils.UserCenter;
import cn.antke.bct.main.controller.MainActivity;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.ADEntity;
import cn.antke.bct.network.entities.ActivityEntity;
import cn.antke.bct.network.entities.BrandEntity;
import cn.antke.bct.network.entities.CategoryTwoEntity;
import cn.antke.bct.network.entities.Entity;
import cn.antke.bct.network.entities.HomeRecommendEntity;
import cn.antke.bct.network.entities.HomeTopEntity;
import cn.antke.bct.network.entities.PagesEntity;
import cn.antke.bct.network.entities.PlateEntity;
import cn.antke.bct.network.entities.ProductEntity;
import cn.antke.bct.network.entities.ShopCarEntity;
import cn.antke.bct.product.controller.ProductDetailActivity;
import cn.antke.bct.product.controller.ProductListActivity;
import cn.antke.bct.product.controller.ShopCarActivity;
import cn.antke.bct.product.controller.StoreDetailActivity;
import cn.antke.bct.utils.ImageUtils;
import cn.antke.bct.utils.LayoutUtil;
import cn.antke.bct.utils.ViewInjectUtils;

import static cn.antke.bct.base.BaseActivity.REQUEST_PERMISSION_CODE;
import static cn.antke.bct.common.CommonConstant.EXTRA_TITLE;
import static cn.antke.bct.common.CommonConstant.EXTRA_URL;
import static cn.antke.bct.common.CommonConstant.REQUEST_NET_SUCCESS;


/**
 * Created by liuzhichao on 2016/12/16.
 * 首页
 */
public class HomeTwoFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_NET_HOME_TOP = 10;
    private static final int REQUEST_NET_HOME_BOTTOM = 20;

    @ViewInject(R.id.home_recommend_list)
    private FootLoadingListView listView;
    @ViewInject(R.id.ll_title)
    private LinearLayout llTitle;
    @ViewInject(R.id.fl_home_search)
    private View flHomeSearch;
    @ViewInject(R.id.iv_home_scan)
    private ImageView ivHomeScan;
    @ViewInject(R.id.cjsb_view)
    private View cjsbView;

    private View view;
    private GridViewForInner homeType;
    private RecyclerView homeBrand;
    private Banner mbanner;
    private List<ActivityEntity> activityEntities;
    private List<CategoryTwoEntity> categoryEntities;
    private List<BrandEntity> brandEntities;
    private boolean topSuccess;
    private boolean bottomSuccess;
    private FrameLayout homeAd;
    private ADEntity adEntity;


    private List<PlateEntity> plateEntities;
    private ConvenientBanner cbHomePlate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.home_frag, null);
            ViewInjectUtils.inject(this, view);
            initTopView(inflater);
        }
        ViewGroup mViewParent = (ViewGroup) view.getParent();
        if (mViewParent != null) {
            mViewParent.removeView(view);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    /*初始化listview头部*/
    private void initTopView(LayoutInflater inflater) {
        View topView = inflater.inflate(R.layout.home_top_layout, null);
        mbanner = (Banner) topView.findViewById(R.id.home_banner);
        cbHomePlate = (ConvenientBanner) topView.findViewById(R.id.cb_home_plate);
        cbHomePlate.setPageIndicator(new int[]{R.drawable.dot_dark, R.drawable.dot_light})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        cbHomePlate.setCanLoop(false);
        cbHomePlate.setPageIndicator(new int[]{R.drawable.dot_dark, R.drawable.dot_light})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        cbHomePlate.setcurrentitem(0);

        homeType = (GridViewForInner) topView.findViewById(R.id.home_grid);
        homeBrand = (RecyclerView) topView.findViewById(R.id.home_brand_list);
        homeAd = (FrameLayout) topView.findViewById(R.id.home_ad);
        View homeBrandMore = topView.findViewById(R.id.home_brand_more);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.getRefreshableView().addHeaderView(topView);
        listView.setOnRefreshListener(refreshView -> loadData());
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View firstChild = view.getChildAt(0);
                if (firstChild != null && firstVisibleItem <= 1) {
                    int dAlpha;
                    int distance = -firstChild.getTop();
                    float total = 200;
                    if (distance > 20) {
                        llTitle.setBackgroundColor(Color.parseColor("#2BB8AA"));
                        cjsbView.setBackgroundColor(Color.parseColor("#2BB8AA"));
                        float result = (distance - 20) / total;
                        dAlpha = 25 + (int) (230 * result);
                        dAlpha = dAlpha > 250 ? 250 : dAlpha;
                        llTitle.getBackground().mutate().setAlpha(dAlpha);
                        cjsbView.getBackground().mutate().setAlpha(dAlpha);
                    } else {
                        llTitle.setBackgroundResource(R.drawable.home_title_bg);
                        cjsbView.setBackgroundResource(R.color.transparent);
                    }
                }
            }
        });
        flHomeSearch.setOnClickListener(this);
        ivHomeScan.setOnClickListener(this);
        homeBrandMore.setOnClickListener(this);
    }

    private void loadData() {
        requestHttpData(Constants.Urls.URL_POST_HOME_TOP, REQUEST_NET_HOME_TOP, FProtocol.HttpMethod.POST, null);
        requestHttpData(Constants.Urls.URL_POST_HOME_BOTTOM, REQUEST_NET_HOME_BOTTOM, FProtocol.HttpMethod.POST, null);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(UserCenter.isLogin(getContext())){
            requestHttpData(Constants.Urls.URL_POST_SHOP_CAR_LIST, CommonConstant.REQUEST_ACT_THREE, FProtocol.HttpMethod.POST, null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_brand_more:
                AnalysisUtil.onEvent(getActivity(), "sy_gd1");
                BrandActivity.startBrandActivity(getActivity());
                break;
            case R.id.home_commend_more:
                AnalysisUtil.onEvent(getActivity(), "sy_gd" + view.getTag(R.id.home_recommend));
                HomeRecommendEntity homeRecommendEntity = (HomeRecommendEntity) view.getTag();
                if (homeRecommendEntity != null) {
                    ProductListActivity.startProductListActivity(getActivity(), -1,homeRecommendEntity.getCategoryId(),homeRecommendEntity.getName(),"");
                }
                break;
            case R.id.recommend_cloth1:
            case R.id.recommend_cloth2:
            case R.id.recommend_cloth3:
                AnalysisUtil.onEvent(getActivity(), "sy_tj" + view.getTag(R.id.home_recommend));
                ProductEntity productEntity = (ProductEntity) view.getTag();
                ProductDetailActivity.startProductDetailActivity(getActivity(), productEntity.getGoodsId());
                break;
            case R.id.fl_home_search:
                SearchActivity.startSearchActivity(getActivity());
            case R.id.sdv_home_ad1:
                AnalysisUtil.onEvent(getActivity(), "sy_ad1");
                activityJump((ActivityEntity) view.getTag());
                break;
            case R.id.iv_home_scan:
                if (UserCenter.isLogin(getActivity())) {
                    ShopCarActivity.startShopCarActivity(getActivity());
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.sdv_home_ad2:
                AnalysisUtil.onEvent(getActivity(), "sy_ad2");
                activityJump((ActivityEntity) view.getTag());
                break;
            case R.id.sdv_home_ad3:
                AnalysisUtil.onEvent(getActivity(), "sy_ad3");
                activityJump((ActivityEntity) view.getTag());
                break;
            case R.id.sdv_home_ad4:
                AnalysisUtil.onEvent(getActivity(), "sy_ad4");
                activityJump((ActivityEntity) view.getTag());
                break;
            case R.id.sdv_home_ad5:
                AnalysisUtil.onEvent(getActivity(), "sy_ad5");
                activityJump((ActivityEntity) view.getTag());
                break;
            case R.id.tv_home_retry:
                loadData();
                break;
            case R.id.ll_item_home_plate:
                PlateEntity plateEntity = (PlateEntity) view.getTag(R.id.plate_click_tag);
                if (plateEntity != null) {
                    switch (plateEntity.getType()) {
                        case "3"://抽奖
                            DrawListActivity.startDrawListActivity(getActivity(), plateEntity.getName());
                            break;
                        case "2"://商品列表
                            ProductListActivity.startProductListActivity(getActivity(), CommonConstant.FROM_HOME_PRODUCT, plateEntity.getId(), plateEntity.getName(), "");
                            break;
                        case "1"://板块
                            PlateListActivity.startPlateListActivity(getActivity(), plateEntity.getId(), plateEntity.getName());
                            break;
                    }
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_PERMISSION_CODE == requestCode) {
            if (grantResults.length > 0 && Manifest.permission.CAMERA.equals(permissions[0]) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                prepareScan();
            } else {
                ToastUtil.shortShow(getActivity(), getString(R.string.get_permission_failed));
            }
        }
    }

    private void prepareScan() {
        startActivity(new Intent(getActivity(), ScanActivity.class));
    }

    @Override
    public void success(int requestCode, String data) {
        Entity result = Parsers.getResult(data);
        Log.d("wf",data);
        if (REQUEST_NET_SUCCESS.equals(result.getResultCode())) {
            switch (requestCode) {
                case REQUEST_NET_HOME_TOP:
                    topSuccess = true;
                    HomeTopEntity homeTop = Parsers.getHomeTop(data);
                    if (homeTop != null) {
                        activityEntities = homeTop.getActivityEntities();
                        categoryEntities = homeTop.getCategoryEntities();
                        plateEntities = homeTop.getPlateEntities();
                        adEntity = homeTop.getAdEntity();
                        brandEntities = homeTop.getBrandEntities();
                        handCarousel();
                        handCategory();
                        handAD();
                        handBrand();
                        handPlate();
                    }
                    break;
                case REQUEST_NET_HOME_BOTTOM:
                    bottomSuccess = true;
                    List<HomeRecommendEntity> homeRecommendList = Parsers.getHomeRecommendList(data);
                    if (homeRecommendList != null) {
                        HomeRecommendAdapter adapter = new HomeRecommendAdapter(getActivity(), homeRecommendList, this);
                        listView.setAdapter(adapter);
                    }
                    break;

                case CommonConstant.REQUEST_ACT_THREE:{
                    PagesEntity<ShopCarEntity> shopCarPage = Parsers.getShopCarPage(data);
                    List<ShopCarEntity> datas = shopCarPage.getDatas();
                    if(datas!=null&&datas.size()!=0){
                        ivHomeScan.setImageResource(R.drawable.car_02);
                    }else{
                        ivHomeScan.setImageResource(R.drawable.car_01);
                    }
                    break;
                }
                case CommonConstant.REQUEST_NET_FOUR:{
                    PagesEntity<ShopCarEntity> shopCarPage = Parsers.getShopCarPage(data);
                    List<ShopCarEntity> datas = shopCarPage.getDatas();
                    if(datas!=null&&datas.size()!=0){
                        ivHomeScan.setImageResource(R.drawable.car_02);
                    }else{
                        ivHomeScan.setImageResource(R.drawable.car_01);
                    }
                    break;
                }

            }
        } else {
            if (REQUEST_NET_HOME_TOP == requestCode) {
                topSuccess = false;
            }
            if (REQUEST_NET_HOME_BOTTOM == requestCode) {
                bottomSuccess = false;
            }
            ToastUtil.shortShow(getActivity(), result.getResultMsg());
        }
        if (topSuccess && bottomSuccess) {
            listView.setOnRefreshComplete();
        }
        if (topSuccess || bottomSuccess) {
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.GONE);
        }
    }

    private void handPlate() {
        if (plateEntities != null && plateEntities.size() > 0) {
            List<List<PlateEntity>> platePages = new ArrayList<>();
            //以6个为一页来分页
            int pageSize;
            if (plateEntities.size() % CommonConfig.HOME_SPECIAL_ONE_PAGE_NUM == 0) {
                pageSize = plateEntities.size() / CommonConfig.HOME_SPECIAL_ONE_PAGE_NUM;
            } else {
                pageSize = plateEntities.size() / CommonConfig.HOME_SPECIAL_ONE_PAGE_NUM + 1;
            }

            for (int i = 0; i < pageSize; i++) {
                List<PlateEntity> plateEntities2 = new ArrayList<>();
                if (i == pageSize - 1) {
                    //最后一页,拿到所有剩余的
                    plateEntities2.addAll(plateEntities.subList(i * CommonConfig.HOME_SPECIAL_ONE_PAGE_NUM, plateEntities.size()));
                } else {
                    plateEntities2.addAll(plateEntities.subList(i * CommonConfig.HOME_SPECIAL_ONE_PAGE_NUM, (i + 1) * CommonConfig.HOME_SPECIAL_ONE_PAGE_NUM));
                }
                //添加到集合中,每一页的数据
                platePages.add(plateEntities2);
            }
            cbHomePlate.setPages(PlateHolder::new, platePages);
        }

    }

    @Override
    public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
        if (REQUEST_NET_HOME_TOP == requestCode) {
            topSuccess = false;
        }
        if (REQUEST_NET_HOME_BOTTOM == requestCode) {
            bottomSuccess = false;
        }
        if (!topSuccess && !bottomSuccess) {
            listView.setOnRefreshComplete();
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
        }
        ToastUtil.shortShow(getActivity(), errorMessage);
    }

    //处理轮播图
    private void handCarousel() {
        LayoutUtil.setHeightAndWidth(getActivity(), mbanner, 0.6f);
        if (activityEntities != null && activityEntities.size() > 0) {
            List<String> imageUrls = new ArrayList<>();
            for (ActivityEntity activityEntity : activityEntities) {
                imageUrls.add(activityEntity.getPicUrl());
            }
            mbanner.setImageLoader(new BannerImageLoader());
            mbanner.setImages(imageUrls);
            mbanner.start();
            mbanner.setOnBannerListener(position -> {
                ActivityEntity activityEntity = activityEntities.get(position);
                AnalysisUtil.onEvent(getActivity(), "banner" + (position + 1));
                activityJump(activityEntity);
            });
        }
    }

    //处理分类
    private void handCategory() {
        if (categoryEntities != null && categoryEntities.size() > 0) {
            if (categoryEntities.size() > 7) {
                categoryEntities = categoryEntities.subList(0, 7);
                categoryEntities.add(new CategoryTwoEntity("", "更多", "res://" + BuildConfig.APPLICATION_ID + "/" + R.drawable.category_more_icon));
            }
            HomeTypeAdapter adapter = new HomeTypeAdapter(getActivity(), categoryEntities);
            homeType.setAdapter(adapter);
            homeType.setOnItemClickListener((parent, view1, position, id) -> {
                if (categoryEntities.size() > 7 && position == categoryEntities.size() - 1) {
                    AnalysisUtil.onEvent(getActivity(), "sy_fl8");
                    ((MainActivity)getActivity()).setCurrentTab(1);
                } else {
                    AnalysisUtil.onEvent(getActivity(), "sy_fl" + (position + 1));
                    CategoryTwoEntity categoryEntity = categoryEntities.get(position);
                    ProductListActivity.startProductListActivity(getActivity(), -1,  categoryEntity.getId(),categoryEntity.getName(),"");
                }
            });
        }
    }

    //处理广告
    private void handAD() {
        if (adEntity != null && adEntity.getActivityEntities() != null && adEntity.getActivityEntities().size() > 0) {
            homeAd.removeAllViews();
            List<ActivityEntity> activityEntities = adEntity.getActivityEntities();
            View adView = null;
            switch (adEntity.getType()) {
                case "app_special_ad1":
                    adView = LayoutInflater.from(getActivity()).inflate(R.layout.home_ad_layout1, homeAd);
                    break;
                case "app_special_ad2":
                    adView = LayoutInflater.from(getActivity()).inflate(R.layout.home_ad_layout2, homeAd);
                    break;
                case "app_special_ad3":
                    adView = LayoutInflater.from(getActivity()).inflate(R.layout.home_ad_layout3, homeAd);
                    break;
                case "app_special_ad4":
                    adView = LayoutInflater.from(getActivity()).inflate(R.layout.home_ad_layout4, homeAd);
                    break;
                case "app_special_ad5":
                    adView = LayoutInflater.from(getActivity()).inflate(R.layout.home_ad_layout5, homeAd);
                    break;
            }
            if (adView != null) {
                for (ActivityEntity activity : activityEntities) {
                    switch (activity.getAdNo()) {
                        case "1":
                            SimpleDraweeView sdvHomeAd1 = (SimpleDraweeView) adView.findViewById(R.id.sdv_home_ad1);
                            ImageUtils.setSmallImg(sdvHomeAd1, activity.getPicUrl());
                            sdvHomeAd1.setTag(activity);
                            sdvHomeAd1.setOnClickListener(this);
                            break;
                        case "2":
                            SimpleDraweeView sdvHomeAd2 = (SimpleDraweeView) adView.findViewById(R.id.sdv_home_ad2);
                            ImageUtils.setSmallImg(sdvHomeAd2, activity.getPicUrl());
                            sdvHomeAd2.setTag(activity);
                            sdvHomeAd2.setOnClickListener(this);
                            break;
                        case "3":
                            SimpleDraweeView sdvHomeAd3 = (SimpleDraweeView) adView.findViewById(R.id.sdv_home_ad3);
                            ImageUtils.setSmallImg(sdvHomeAd3, activity.getPicUrl());
                            sdvHomeAd3.setTag(activity);
                            sdvHomeAd3.setOnClickListener(this);
                            break;
                        case "4":
                            SimpleDraweeView sdvHomeAd4 = (SimpleDraweeView) adView.findViewById(R.id.sdv_home_ad4);
                            ImageUtils.setSmallImg(sdvHomeAd4, activity.getPicUrl());
                            sdvHomeAd4.setTag(activity);
                            sdvHomeAd4.setOnClickListener(this);
                            break;
                        case "5":
                            SimpleDraweeView sdvHomeAd5 = (SimpleDraweeView) adView.findViewById(R.id.sdv_home_ad5);
                            ImageUtils.setSmallImg(sdvHomeAd5, activity.getPicUrl());
                            sdvHomeAd5.setTag(activity);
                            sdvHomeAd5.setOnClickListener(this);
                            break;
                    }
                }
            }
        }
    }

    //处理品牌
    private void handBrand() {
        if (brandEntities != null && brandEntities.size() > 0) {
            List<String> urls = new ArrayList<>();
            for (BrandEntity brandEntity : brandEntities) {
                urls.add(brandEntity.getLogo());
            }
            //设置布局管理器
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            homeBrand.setLayoutManager(linearLayoutManager);
            //设置适配器
            GalleryAdapter adapter = new GalleryAdapter(getActivity(), urls, 1);
            homeBrand.setAdapter(adapter);
            adapter.setOnItemClickListener((view1, position) -> {
                AnalysisUtil.onEvent(getActivity(), "sy_pp" + (position + 1));
                BrandDetailActivity.startBrandDetailActivity(getActivity(), brandEntities.get(position).getId());
            });
        }
    }

    //banner和ad的活动跳转
    private void activityJump(ActivityEntity activityEntity) {
        if (activityEntity == null) {
            return;
        }
        switch (activityEntity.getType()) {
            case 1:
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(EXTRA_URL, activityEntity.getLinkUrl());
                intent.putExtra(EXTRA_TITLE, "活动详情");
                getActivity().startActivity(intent);
                break;
            case 2://店铺
                StoreDetailActivity.startStoreDetailActivity(getActivity(),activityEntity.getLinkUrl());
                break;
            case 3://商品
                ProductDetailActivity.startProductDetailActivity(getActivity(), activityEntity.getLinkUrl());
                break;
            case 4://商品
                ((MainActivity)getActivity()).setCurrentTab(3);
                break;
        }
    }


    private class PlateHolder implements Holder<List<PlateEntity>>, OnItemHeightGetListener {

        private GridView gvItemHomePlate;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_home_plate, null);
            gvItemHomePlate = (GridView) view.findViewById(R.id.gv_item_home_plate);
            return view;
        }

        @Override
        public void UpdateUI(Context context, int position, List<PlateEntity> data) {
            PlateAdapter plateAdapter = new PlateAdapter(getActivity(), data, HomeTwoFragment.this);
            plateAdapter.setOnItemHeightGetListener(this);
            gvItemHomePlate.setAdapter(plateAdapter);
        }

        @Override
        public void onItemHeightGet(int itemHeight) {
            //因为专区的ConvenientBanner获取不到高度，所以这里通过先拿到item的高度后再动态设置高度
            if (plateEntities != null) {
                ViewGroup.LayoutParams layoutParams = cbHomePlate.getLayoutParams();
                int height;
                if (plateEntities.size() > 3) {
                    height = itemHeight * (CommonConfig.HOME_SPECIAL_ONE_PAGE_NUM / gvItemHomePlate.getNumColumns());
                } else {
                    height = itemHeight;
                }
                if (layoutParams.height != height) {
                    layoutParams.height = height;
                    cbHomePlate.setLayoutParams(layoutParams);
                }
            }
        }
    }
}

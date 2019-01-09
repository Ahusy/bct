package cn.antke.bct.special.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.common.network.FProtocol;
import com.common.utils.ToastUtil;
import com.common.viewinject.annotation.ViewInject;
import com.common.widget.RefreshRecyclerView;

import java.util.IdentityHashMap;
import java.util.List;

import cn.antke.bct.R;
import cn.antke.bct.base.BaseFragment;
import cn.antke.bct.base.WebViewActivity;
import cn.antke.bct.common.CommonConstant;
import cn.antke.bct.home.adapter.ProductsAdapter;
import cn.antke.bct.login.controller.LoginActivity;
import cn.antke.bct.login.utils.UserCenter;
import cn.antke.bct.network.Constants;
import cn.antke.bct.network.Parsers;
import cn.antke.bct.network.entities.BannerEntity;
import cn.antke.bct.network.entities.Entity;
import cn.antke.bct.network.entities.HomeEntity;
import cn.antke.bct.network.entities.ProductDetailEntity;
import cn.antke.bct.network.entities.ProductEntity;
import cn.antke.bct.person.controller.StoreApplyActivity;
import cn.antke.bct.product.controller.ProductDetailActivity;
import cn.antke.bct.product.controller.ProductListActivity;
import cn.antke.bct.product.controller.StoreDetailActivity;
import cn.antke.bct.utils.CommonTools;
import cn.antke.bct.utils.ImageUtils;
import cn.antke.bct.utils.ViewInjectUtils;

/**
 * Created by liuzhichao on 2017/4/27.
 * 易购专区
 */
public class SpecialFragment extends BaseFragment implements View.OnClickListener, RefreshRecyclerView.OnRefreshAndLoadMoreListener {

    @ViewInject(R.id.left_button)
    private View leftButton;
    @ViewInject(R.id.toolbar_title)
    private TextView toolbarTitle;
    @ViewInject(R.id.rigth_text)
    private TextView rigthText;
    @ViewInject(R.id.rrv_special_list)
    private RefreshRecyclerView rrvSpecialList;
    @ViewInject(R.id.cb_home_banner)
    private ConvenientBanner cbHomeBanner;
    @ViewInject(R.id.cb_home_plate)
    private ConvenientBanner cbHomePlate;
    @ViewInject(R.id.tv_home_subtitle)
    private TextView tvHomeSubtitle;
//	@ViewInject(R.id.tv_special_more)
//	private View tvSpecialMore;

    @ViewInject(R.id.fl_product_sort_num)
    private View flProductSortNum;
    @ViewInject(R.id.tv_product_sort_num_text)
    private TextView tvProductSortNumText;
    @ViewInject(R.id.fl_product_sort_price)
    private View flProductSortPrice;
    @ViewInject(R.id.tv_product_sort_price_text)
    private TextView tvProductSortPriceText;

    @ViewInject(R.id.ll_common_sort)
    private LinearLayout llCommonSort;

    private TextView preSort;//当前选择的tv
    private String orderFiled;//价格or销量
    private String orderType;//降序or升序

    private boolean isHightToLow;

    private View view;
    private View topView;

    private List<BannerEntity> bannerEntities;
    private ProductsAdapter productsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.frag_special, null);
            topView = inflater.inflate(R.layout.layout_home_top, null);
            ViewInjectUtils.inject(this, view);
            ViewInjectUtils.inject(this, topView);
            initView();
        }
        ViewGroup mViewParent = (ViewGroup) view.getParent();
        if (mViewParent != null) {
            mViewParent.removeView(view);
        }
        return view;
    }

    private void initView() {
        leftButton.setVisibility(View.INVISIBLE);
        toolbarTitle.setText(getString(R.string.main_tab_special));
        toolbarTitle.setVisibility(View.VISIBLE);
        rigthText.setText(getString(R.string.store_apply));
        rigthText.setVisibility(View.VISIBLE);
        rigthText.setOnClickListener(this);

        tvHomeSubtitle.setText(getString(R.string.product_list));
        tvHomeSubtitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        cbHomePlate.setVisibility(View.GONE);

        tvProductSortNumText.setSelected(true);
        tvProductSortNumText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.product_down_green_icon, 0);
        isHightToLow = true;
        preSort = tvProductSortNumText;
        orderFiled = CommonConstant.ORDER_FILED_VOLUME;
        orderType = CommonConstant.ORDER_TYPE_DESC;

        flProductSortNum.setOnClickListener(this);
        flProductSortPrice.setOnClickListener(this);

        llCommonSort.setVisibility(View.VISIBLE);
        rrvSpecialList.addHeaderView(topView);
        rrvSpecialList.setHasFixedSize(true);
        rrvSpecialList.setMode(RefreshRecyclerView.Mode.BOTH);
        rrvSpecialList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rrvSpecialList.setOnRefreshAndLoadMoreListener(this);
        rrvSpecialList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getLayoutManager() instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
                    int spanCount = gridLayoutManager.getSpanCount();//设置的列数
                    int headerSize = rrvSpecialList.getHeaderSize();//头部数量
                    int pos = parent.getChildLayoutPosition(view) - headerSize;//减去头部后的下标位置

                    if (pos < 0) {//头部
                        return;
                    }

                    //item左右偏移量(左右间距)，仅对2列有效
                    if (pos % 2 == 0) {
                        outRect.right = CommonTools.dp2px(getActivity(), 4);
                    } else {
                        outRect.left = CommonTools.dp2px(getActivity(), 4);
                    }
                    //item上下偏移量(上下间距)
                    if (pos >= spanCount) {
                        outRect.top = CommonTools.dp2px(getActivity(), 8);
                    }
                }
            }
        });
        rrvSpecialList.setCanAddMore(true);

        cbHomeBanner.setPageIndicator(new int[]{R.drawable.dot_dark, R.drawable.dot_light})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .startTurning(3000);
        cbHomeBanner.setOnItemClickListener(position -> {
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(false);
    }

    private void loadData(boolean isMore) {
        IdentityHashMap<String, String> params = new IdentityHashMap();
        int requestCode = CommonConstant.REQUEST_NET_ONE;
        int page = 1;
        if (isMore) {
            requestCode = CommonConstant.REQUEST_NET_TWO;
            page = productsAdapter.getPage() + 1;
        }
        params.put("orderField", orderFiled);
        params.put("orderType", orderType);
        params.put(CommonConstant.PAGESIZE, CommonConstant.PAGE_SIZE_10);
        params.put(CommonConstant.PAGENUM, String.valueOf(page));
        params.put("is_easyBuyGoods", String.valueOf(1));
        requestHttpData(Constants.Urls.URL_POST_PLATE_PRODUCT_LIST, requestCode, FProtocol.HttpMethod.POST, params);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(UserCenter.getStoreId(getActivity()))) {
            rigthText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void success(int requestCode, String data) {
        rrvSpecialList.resetStatus();
        switch (requestCode) {
            case CommonConstant.REQUEST_NET_ONE: {
                Entity result = Parsers.getResult(data);
                if (result != null && CommonConstant.REQUEST_NET_SUCCESS.equals(result.getResultCode())) {
                    HomeEntity homeEntity = Parsers.getHomeEntity(data);
                    if (homeEntity != null) {
                        bannerEntities = homeEntity.getBannerEntities();
                        List<ProductDetailEntity> productEntities = homeEntity.getProductEntities();
                        cbHomeBanner.setPages(new CBViewHolderCreator<ImageHolder>() {
                            @Override
                            public ImageHolder createHolder() {
                                return new ImageHolder();
                            }
                        }, bannerEntities);
                        productsAdapter = new ProductsAdapter(productEntities, this);
                        rrvSpecialList.setAdapter(productsAdapter);
                        rrvSpecialList.setCanAddMore(Integer.parseInt(homeEntity.getTotalPage()) > productsAdapter.getPage());
                    } else {
                        ToastUtil.shortShow(getActivity(), getString(R.string.no_data_now));
                    }
                } else {
                    ToastUtil.shortShow(getActivity(), result.getResultMsg());
                }
                break;
            }
            case CommonConstant.REQUEST_NET_TWO: {
                HomeEntity homeEntity = Parsers.getHomeEntity(data);
                Entity result = Parsers.getResult(data);
                if (result != null && CommonConstant.REQUEST_NET_SUCCESS.equals(result.getResultCode())) {
                    if (homeEntity != null) {
                        List<ProductDetailEntity> productEntities = homeEntity.getProductEntities();
                        if (productEntities != null && productEntities.size() != 0) {
                            productsAdapter.addDatas(productEntities);
                            productsAdapter.notifyDataSetChanged();
                            rrvSpecialList.setCanAddMore(Integer.parseInt(homeEntity.getTotalPage()) > productsAdapter.getPage());
                        }
                    }
                }else{
                    ToastUtil.shortShow(getActivity(),result.getResultMsg());
                }
            }
            break;
        }
    }

    @Override
    public void mistake(int requestCode, FProtocol.NetDataProtocol.ResponseStatus status, String errorMessage) {
        rrvSpecialList.resetStatus();
        ToastUtil.shortShow(getActivity(), errorMessage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rigth_text:
                if (UserCenter.isLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), StoreApplyActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.ll_item_product_layout:
                ProductEntity productEntity = (ProductEntity) v.getTag();
                if (productEntity != null) {
                    ProductDetailActivity.startProductDetailActivity(getActivity(), productEntity.getGoodsId());
                }
                break;
            case R.id.fl_product_sort_num:
                orderFiled = CommonConstant.ORDER_FILED_VOLUME;
                sort(tvProductSortNumText);
                break;
            case R.id.fl_product_sort_price:
                orderFiled = CommonConstant.ORDER_FILED_PRICE;
                sort(tvProductSortPriceText);
                break;
        }
    }

    @Override
    public void onRefresh() {
        loadData(false);
    }

    @Override
    public void onLoadMore() {
        loadData(true);
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

    private void sort(TextView textView) {
        if (preSort != textView) {
            //重置前一个排序条件,默认一开始都是从高到低
            preSort.setSelected(false);
            preSort.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.product_down_black_icon, 0);
            isHightToLow = false;//因为需要使排序默认为箭头向下，所以设置上一次箭头是朝上
        }
        if (isHightToLow) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.product_up_green_icon, 0);
            isHightToLow = false;
            orderType = CommonConstant.ORDER_TYPE_ASC;
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.product_down_green_icon, 0);
            isHightToLow = true;
            orderType = CommonConstant.ORDER_TYPE_DESC;
        }
        textView.setSelected(true);
        preSort = textView;
        loadData(false);
    }
}

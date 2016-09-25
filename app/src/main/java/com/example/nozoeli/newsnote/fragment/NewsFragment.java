package com.example.nozoeli.newsnote.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.astuetz.PagerSlidingTabStrip;
import com.example.nozoeli.newsnote.R;
import com.example.nozoeli.newsnote.adapter.NewsSlideAdapter;
import com.example.nozoeli.newsnote.bean.ImgExtra;
import com.example.nozoeli.newsnote.bean.NewsListBean;
import com.example.nozoeli.newsnote.util.UrlLinks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nozoeli on 16-4-13.
 */
public class NewsFragment extends Fragment {

    private PagerSlidingTabStrip slidingTabs;
    private ViewPager slidingPager;
    private NewsSlideAdapter slidingPagerAdapter;
    private FragmentManager fm;
    public static NewsListBean data[] = new NewsListBean[5];

    public static NewsFragment newInstance() {
        NewsFragment instance = new NewsFragment();

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("frag", "newsfrag create");
        init();
        download(0);
        download(1);
        download(2);
        download(3);
        download(4);
        fm = getActivity().getSupportFragmentManager();
    }

    private void init() {
        for (int i = 0; i < data.length; i++) {
            data[i] = new NewsListBean();
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        slidingPager = (ViewPager) root.findViewById(R.id.sliding_pager);
        slidingPagerAdapter = new NewsSlideAdapter(getActivity().getSupportFragmentManager());
        if (slidingPager != null) {
            slidingPager.setAdapter(slidingPagerAdapter);
        }

        slidingTabs = (PagerSlidingTabStrip) root.findViewById(R.id.tabs);
        slidingTabs.setViewPager(slidingPager);

        return root;
    }

    public void download(final int position) {
        final NewsListBean bean = new NewsListBean();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest temp = new StringRequest(
                UrlLinks.URL_LIST[position] + 0 + UrlLinks.COMMON_POSTFIX_STRING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String jsonString = response.toString().substring(29, response.toString().length() - 2);
                        try {
                            JSONArray newsArray = new JSONArray(jsonString);
                            for (int i = 0; i < newsArray.length(); i++) {
                                JSONObject item = newsArray.getJSONObject(i);
                                if (bean != null) {
                                    if (!item.optString("skipType").equals("live")) {
                                        bean.getTitleList().add(item.getString("title"));
                                        bean.getArticleImgList().add(item.getString("imgsrc"));
                                        bean.getConcreteUrl().add(item.getString("url"));
                                        bean.getDocIdList().add(item.getString("docid"));
                                        bean.getStitleList().add(item.getString("stitle"));
                                        if (item.optString("photosetID") != "" && item.optJSONArray("imgextra") != null) {
                                            bean.getIsAlbumFlag().add("true");
                                            JSONArray imgExtraList = item.getJSONArray("imgextra");
                                            ImgExtra extra = new ImgExtra();
                                            bean.getDigestList().add(null);
                                            bean.getPhotoSetIDList().add(item.getString("photosetID"));
                                            for (int count = 0; count < imgExtraList.length(); count++) {
                                                extra.getImgExtra().add(imgExtraList.getJSONObject(count).getString("imgsrc"));
                                            }
                                            bean.getImgExtraList().add(extra);
                                        } else {
                                            bean.getDigestList().add(item.getString("digest"));
                                            bean.getIsAlbumFlag().add("false");
                                            bean.getImgExtraList().add(new ImgExtra());
                                            bean.getPhotoSetIDList().add("");
                                        }
                                    }

                                }
                            }

                            initData(position, bean);
                            slidingPagerAdapter = new NewsSlideAdapter(getActivity().getSupportFragmentManager());
                            if (slidingPager != null) {
                                slidingPager.setAdapter(slidingPagerAdapter); // 在数据加载完成后才setAdapter保证第一个页面的正常加载
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(temp);
    }


    private void initData(int position, NewsListBean bean) {
        data[position].setTitleList(bean.getTitleList());
        data[position].setImgExtraList(bean.getImgExtraList());
        data[position].setArticleImgList(bean.getArticleImgList());
        data[position].setIsAlbumFlag(bean.getIsAlbumFlag());
        data[position].setDigestList(bean.getDigestList());
        data[position].setConcreteUrl(bean.getConcreteUrl());
        data[position].setPhotoSetIDList(bean.getPhotoSetIDList());
        data[position].setDocIdList(bean.getDocIdList());
        data[position].setStitleList(bean.getStitleList());
    }
}

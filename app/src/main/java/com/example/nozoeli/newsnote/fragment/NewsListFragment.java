package com.example.nozoeli.newsnote.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nozoeli.newsnote.R;
import com.example.nozoeli.newsnote.activity.AlbumActivity;
import com.example.nozoeli.newsnote.activity.NewsDetailActivity;
import com.example.nozoeli.newsnote.adapter.NewsRecyclerAdapter;
import com.example.nozoeli.newsnote.bean.ImgExtra;
import com.example.nozoeli.newsnote.bean.NewsListBean;
import com.example.nozoeli.newsnote.listener.EndlessScrollListener;
import com.example.nozoeli.newsnote.util.UrlLinks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nozoeli on 16-4-13.
 */
public class NewsListFragment extends Fragment {

    private RecyclerView newsRecycler;
    private NewsRecyclerAdapter newsAdapter;
    private NewsListBean news;
    private FragmentManager fm;
    private LinearLayoutManager layoutManager;
    private EndlessScrollListener mScrollListener;
    private int position;
    private int size = 10;
    private SwipeRefreshLayout freshLayout;

    public static NewsListFragment getInstance(NewsListBean bean, FragmentManager fm, int position) {
        NewsListFragment instance = new NewsListFragment();
        instance.setFm(fm);
        instance.setNews(bean);
        instance.setPosition(position);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setNews(NewsListBean news) {
        this.news = news;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_news_list, container, false);

        freshLayout = (SwipeRefreshLayout) root.findViewById(R.id.news_list_fresh);
        newsRecycler = (RecyclerView) root.findViewById(R.id.news_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        newsRecycler.setLayoutManager(layoutManager);
        newsAdapter = new NewsRecyclerAdapter(getContext(), news, fm);

        mScrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                loadMore(position, false);
                Log.d("fresh", "onScroll");
            }
        };

        newsAdapter.setListener(new NewsRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(final RecyclerView.ViewHolder holder, final int position) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 根据holder的viewType进行相应的跳转
                        if (holder.getItemViewType() == NewsRecyclerAdapter.IMG_ALBUM) {
                            String link = null;
                            if (((NewsRecyclerAdapter.ImgAlbumViewHolder)holder).getLink() != null) {
                                link = ((NewsRecyclerAdapter.ImgAlbumViewHolder)holder).getLink();
                            }
                            Intent jumpToAlbum = new Intent(getActivity(), AlbumActivity.class);
                            jumpToAlbum.putExtra("link", link);
                            startActivity(jumpToAlbum);
                            getActivity().overridePendingTransition(R.anim.righ_in, R.anim.left_out);
                        }
                        if (holder.getItemViewType() == NewsRecyclerAdapter.TEXT_NEWS) {
                            String id = ((NewsRecyclerAdapter.NewsTextViewHolder)holder).getId();
                            Intent jumpToNews = new Intent(getActivity(), NewsDetailActivity.class);
                            jumpToNews.putExtra("id", id);
                            startActivity(jumpToNews);
                            getActivity().overridePendingTransition(R.anim.righ_in, R.anim.left_out);
                        }

                    }
                });
            }
        });

        newsRecycler.setAdapter(newsAdapter);
        newsRecycler.addOnScrollListener(mScrollListener);

        freshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMore(position, true);
                Log.d("fresh", "onFresh");
                freshLayout.setRefreshing(false);
            }
        });

        return root;
    }


    public void loadMore(final int position, final boolean isFresh) {
        final NewsListBean bean = new NewsListBean();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest temp = getCorrectRequest(position, isFresh, bean);
        queue.add(temp);

    }

    // 将获取到的
    private void addData(NewsListBean bean) {
        for (int i = 0; i < bean.getTitleList().size(); i++) {
            if (!news.getTitleList().contains(bean.getTitleList().get(i))) {
                news.getTitleList().add(bean.getTitleList().get(i));
                news.getArticleImgList().add(bean.getArticleImgList().get(i));
                news.getConcreteUrl().add(bean.getConcreteUrl().get(i));
                news.getDigestList().add(bean.getDigestList().get(i));
                news.getDocIdList().add(bean.getDocIdList().get(i));
                news.getImgExtraList().add(bean.getImgExtraList().get(i));
                news.getIsAlbumFlag().add(bean.getIsAlbumFlag().get(i));
                news.getPhotoSetIDList().add(bean.getPhotoSetIDList().get(i));
                news.getStitleList().add(bean.getStitleList().get(i));
                newsAdapter.removeFooter();
            }

        }
    }

    private void reverseAddData(NewsListBean bean) {
        for (int i = bean.getTitleList().size() - 1; i >= 0; i--) {
            if (!news.getTitleList().contains(bean.getTitleList().get(i))) {
                news.getTitleList().add(0, bean.getTitleList().get(i));
                news.getArticleImgList().add(0, bean.getArticleImgList().get(i));
                news.getConcreteUrl().add(0, bean.getConcreteUrl().get(i));
                news.getDigestList().add(0, bean.getDigestList().get(i));
                news.getDocIdList().add(0, bean.getDocIdList().get(i));
                news.getImgExtraList().add(0, bean.getImgExtraList().get(i));
                news.getIsAlbumFlag().add(0, bean.getIsAlbumFlag().get(i));
                news.getPhotoSetIDList().add(0, bean.getPhotoSetIDList().get(i));
                news.getStitleList().add(0, bean.getStitleList().get(i));
                newsAdapter.removeFooter();
            }

        }
    }

    // 往临时对象中填充数据
    private void appendData(JSONObject item, NewsListBean bean) throws JSONException {
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


    /**
     *
     * @param position  页面fragment的position
     * @param isFresh   是否是刷新获得的数据
     * @param bean      获得的新数据
     * @return          返回相应的StringRequest
     */
    private StringRequest getCorrectRequest(int position, boolean isFresh, final NewsListBean bean) {

        StringRequest request;
        if (isFresh) {
            request = new StringRequest(UrlLinks.URL_LIST[position] + 0 + UrlLinks.COMMON_POSTFIX_STRING,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String jsonString = response.substring(29, response.length() - 2);
                            try {
                                JSONArray array = new JSONArray(jsonString);
                                for (int i = array.length() - 1; i >= 0; i--) {
                                    JSONObject item = array.getJSONObject(i);
                                    appendData(item, bean);
                                }
                                reverseAddData(bean);
                                newsAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
        } else {
            request = new StringRequest(UrlLinks.URL_LIST[position] + size + UrlLinks.COMMON_POSTFIX_STRING,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String jsonString = response.substring(29, response.length() - 2);
                            try {
                                JSONArray newsArray = new JSONArray(jsonString);
                                for (int i = 0; i < newsArray.length(); i++) {
                                    JSONObject item = newsArray.getJSONObject(i);
                                    appendData(item, bean);
                                }
                                addData(bean);
                                newsAdapter.notifyDataSetChanged();
                                size += 10;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }

            );
        }
        return request;
    }
}

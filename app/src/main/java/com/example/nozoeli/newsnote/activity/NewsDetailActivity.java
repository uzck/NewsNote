package com.example.nozoeli.newsnote.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nozoeli.newsnote.R;
import com.example.nozoeli.newsnote.bean.NoteBean;
import com.example.nozoeli.newsnote.util.UtilTools;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by nozoeli on 16-4-18.
 */
public class NewsDetailActivity extends SwipeBackActivity {

    private SwipeBackLayout mSwipLayout;
    private Toolbar mToolBar;
    private String id;
    private TextView detailTitle;
    private TextView pTimeAndSource;
    private TextView originTitle;
    private LinearLayout wraper;
    private LinearLayout contentContainer;
    private ArrayList<String> picLinks = new ArrayList<>();
    private Pattern pattern = Pattern.compile("<!--IMG#[0-9]-->");
    private Matcher matcher;
    private LinearLayout.LayoutParams params;
    private TextView addToContainer;
    private StringBuilder content = new StringBuilder();
    private NoteBean savedNews = new NoteBean();
    private boolean hasSaved = false;
    private String title;
    private TextView contentText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_news);

        detailTitle = (TextView) findViewById(R.id.detail_title);
        pTimeAndSource = (TextView) findViewById(R.id.time_and_source);
        originTitle = new TextView(this);
        wraper = (LinearLayout) findViewById(R.id.title_container);
        contentContainer = (LinearLayout) findViewById(R.id.content_container);

        addToContainer = new TextView(this);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addToContainer.setLayoutParams(params);

        mSwipLayout = getSwipeBackLayout();
        mSwipLayout.setEdgeSize(300);
        mSwipLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        final LinearLayout statusBar = (LinearLayout) findViewById(R.id.status_bar);
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                UtilTools.getBarHeight(getResources())));

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mToolBar = (Toolbar) findViewById(R.id.detail_toolbar);
        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        id = getIntent().getStringExtra("id");
        String link = "http://3g.163.com/touch/article/" + id + "/full.html";

        downloadNews(link);

        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_to_favorite:

                        savedNews = new NoteBean(
                                detailTitle.getText().toString(),
                                content.toString(),
                                String.valueOf(System.currentTimeMillis()));
                        savedNews.save();

                        mToolBar.getMenu().removeItem(R.id.add_to_favorite);
                        mToolBar.inflateMenu(R.menu.after_favoriate);
                        Toast.makeText(NewsDetailActivity.this, getResources().getText(R.string.save_the_news),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.added_favorite:
                        if (savedNews.isSaved()) {
                            savedNews.delete();
                        }
                        Toast.makeText(NewsDetailActivity.this, getResources().getText(R.string.delete_the_news),
                                Toast.LENGTH_SHORT).show();
                        mToolBar.getMenu().removeItem(R.id.added_favorite);
                        mToolBar.inflateMenu(R.menu.read_setting);
                    default:
                }
                return true;
            }
        });
    }

    private void downloadNews(String link) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(link,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String subString = response.substring(12, response.length());
                        try {
                            JSONObject newsData = new JSONObject(subString).getJSONObject(id);
                            String body = newsData.getString("body");
                            if (newsData.optJSONArray("img") != null) {
                                JSONArray picList = newsData.getJSONArray("img");
                                for (int picCount = 0; picCount < picList.length(); picCount++) {
                                    picLinks.add(picList.getJSONObject(picCount).getString("src"));
                                }
                            }
                            parserHTML(body);
                            String editor = newsData.getString("ec");  // 责任编辑字段
                            contentText = new TextView(NewsDetailActivity.this);
                            contentText.setLayoutParams(params);
                            params.gravity = Gravity.RIGHT;
                            if (!newsData.optString("otitle").equals("")) {
                                originTitle.setText("原标题: " + newsData.getString("otitle"));
                                originTitle.setLayoutParams(params);
                                contentContainer.addView(originTitle);
                            }
                            contentText.setText("责任编辑: " + editor);
                            contentText.setLineSpacing(1.5f, 1.5f);
                            contentContainer.addView(contentText);
                            title = newsData.getString("title");
                            inflateMenu(isHasSaved());
                            detailTitle.setText(title);
                            String publishTime = newsData.getString("ptime");
                            String source = newsData.getString("source");
                            pTimeAndSource.setText(publishTime + "   " + source);
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
        queue.add(request);
    }

    private void parserHTML(String body) {
        StringBuilder result = new StringBuilder();
        Document document = Jsoup.parse(body);
        findComment(document, result);

    }

    int position = 0;

    // 将文本转为document遍历获取<p>节点及注释节点
    private void findComment(Node node, StringBuilder builder) {

        for (int count = 0; count < node.childNodes().size(); ) {
            Node child = node.childNode(count);
            if (child.nodeName().equals("strong")
                    || child.nodeName().equals("b")) {
                builder.append(((Element) child).text());
                contentText = new TextView(this);
                contentText.setLineSpacing(1.5f, 1.5f);                                     // 设置字体大小及行距
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                contentText.setLayoutParams(params);
                contentText.setText(builder.toString());
                content.append(builder.toString());
                contentContainer.addView(contentText);
                builder.delete(0, builder.length());
                count++;
            } else if (child.nodeName().equals("p")) {
                if (((Element)child).text() != "") {
                    builder.append(((Element) child).text());
                    contentText = new TextView(this);
                    contentText.setLineSpacing(1.5f, 1.5f);
                    contentText.setLayoutParams(params);
                    contentText.setText(builder.toString() + "\n");
                    contentContainer.addView(contentText);
                    builder.delete(0, builder.length());
                    content.append(builder.toString());
                    count++;
                }

            } else if (child.nodeName().equals("#comment")) {
                matcher = pattern.matcher(child.toString());
                if (matcher.find()) {
                    if (position < picLinks.size()) {
                        contentContainer.addView(displayContentPic(position));
                        position += 1;
                    }
                }
                count++;
            } else {
                findComment(child, builder);
                count++;
            }
        }
    }

    // 具体新闻页面中的图片异步加载
    private ImageView displayContentPic(int position) {

        ImageView picHolder = new ImageView(this);
        picHolder.setScaleType(ImageView.ScaleType.FIT_CENTER);  // 设置图片居中显示
        picHolder.setAdjustViewBounds(true);                     // 拉伸图片
        if (picLinks.size() > 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.setMargins(0, 0, 0, UtilTools.dip2px(this, 10));
            picHolder.setLayoutParams(params);
            Picasso.with(this)
                    .load(picLinks.get(position))
                    .placeholder(R.drawable.common_fail_placeholder)
                    .into(picHolder);
        }

        return picHolder;
    }

    @Override
    protected void onStop() {
        super.onStop();
        picLinks.clear();
        finish();
    }

    // 查询新闻的标题在数据库中是否已存在
    private boolean isHasSaved() {
        List<NoteBean> query = DataSupport
                .select("title")
                .where("title == ?", title)
                .find(NoteBean.class);
        Log.d("query", "query size " + query.size());
        if (query != null && query.size() > 0) {
            hasSaved = true;
            savedNews = query.get(0);
        }

        return hasSaved;
    }

    // 根据isHasSaved的结果进行menu的加载
    // true时favorite图标为红，false时为白
    private void inflateMenu(boolean hasSaved) {
        if (hasSaved) {
            mToolBar.inflateMenu(R.menu.after_favoriate);
        } else {
            mToolBar.inflateMenu(R.menu.read_setting);
        }
    }


}

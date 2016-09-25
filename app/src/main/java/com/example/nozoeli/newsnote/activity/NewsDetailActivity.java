package com.example.nozoeli.newsnote.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
import com.example.nozoeli.newsnote.util.UtilFunction;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.litepal.crud.DataSupport;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by nozoeli on 16-4-18.
 */
public class NewsDetailActivity extends SwipeBackActivity{

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initWindow();
        setContentView(R.layout.news_detail_layout);

        detailTitle = (TextView) findViewById(R.id.detail_title);
        pTimeAndSource = (TextView) findViewById(R.id.time_and_source);
        originTitle = (TextView) findViewById(R.id.origin_title);
        wraper = (LinearLayout) findViewById(R.id.title_wraper);
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
                UtilFunction.getBarHeight(getResources())));

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
                        mToolBar.getMenu().removeItem(R.id.night_mode);
                        mToolBar.inflateMenu(R.menu.after_favoriate);
                        Toast.makeText(NewsDetailActivity.this, "已保存到笔记", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.night_mode:
                        Toast.makeText(NewsDetailActivity.this, "夜间模式", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.added_favorite:
                        if (savedNews.isSaved()) {
                            savedNews.delete();
                        }
                        Toast.makeText(NewsDetailActivity.this, "从笔记中删除", Toast.LENGTH_SHORT).show();
                        mToolBar.getMenu().removeItem(R.id.added_favorite);
                        mToolBar.getMenu().removeItem(R.id.night_mode);
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
                                Log.d("arraySize", "length " + picList.length());
                                for (int picCount = 0; picCount < picList.length(); picCount++) {
                                    picLinks.add(picList.getJSONObject(picCount).getString("src"));
                                }
                            }
                            if (!newsData.optString("otitle").equals("")) {
                                originTitle.setText("原标题: " + newsData.getString("otitle"));
                            } else {
                                originTitle.setVisibility(View.GONE);
                                wraper.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        UtilFunction.dip2px(NewsDetailActivity.this, 55f)));
                            }
                            parserHTML(body);
                            String editor = newsData.getString("ec");
                            TextView contentText = new TextView(NewsDetailActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            contentText.setLayoutParams(params);
                            contentText.setText(editor);
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
    private void findComment(Node node, StringBuilder builder) {

        for (int count = 0; count < node.childNodes().size();) {
            Node child = node.childNode(count);
            if (child.nodeName().equals("strong")
                    || child.nodeName().equals("b")) {
                builder.append(((Element)child).text());
                TextView contentText = new TextView(this);
                contentText.setTypeface(contentText.getTypeface(), Typeface.BOLD_ITALIC);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                contentText.setLayoutParams(params);
                contentText.setText(builder.toString());
                content.append(builder.toString());
                contentContainer.addView(contentText);
                builder.delete(0, builder.length());
                count++;
            } else if (child.nodeName().equals("p")) {
                builder.append(((Element)child).text());
                TextView contentText = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                contentText.setLayoutParams(params);
                contentText.setText(builder.toString() + "\n");
                contentContainer.addView(contentText);
                builder.delete(0, builder.length());
                content.append(builder.toString());
                count++;
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

    private ImageView displayContentPic(int position) {

        ImageView picHolder = new ImageView(this);
        picHolder.setPadding(0, 20, 0, 20);
        if (picLinks.size() > 0) {
            Log.d("pic", picLinks.get(position));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            picHolder.setLayoutParams(params);
            Picasso.with(this)
                    .load(picLinks.get(position))
                    .into(picHolder);
        }

        return picHolder;
    }

    @Override
    protected void onStop() {
        super.onStop();
        picLinks.clear();
        finish();
//        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

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

    private void inflateMenu(boolean hasSaved) {
        if (hasSaved) {
            mToolBar.inflateMenu(R.menu.after_favoriate);
        } else {
            mToolBar.inflateMenu(R.menu.read_setting);
        }
    }
}

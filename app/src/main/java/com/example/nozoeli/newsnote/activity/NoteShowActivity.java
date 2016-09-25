package com.example.nozoeli.newsnote.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nozoeli.newsnote.R;
import com.example.nozoeli.newsnote.bean.NoteBean;
import com.example.nozoeli.newsnote.util.UtilTools;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nozoeli on 16-4-23.
 */
public class NoteShowActivity extends AppCompatActivity {

    private EditText showTitle;
    private EditText showContent;
    private LinearLayout showStatusBar;
    private Toolbar showToolbar;
    private List<NoteBean> instance;
    private String date;
    private TextView showDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_show_note);

        showTitle = (EditText) findViewById(R.id.note_show_title);
        showContent = (EditText) findViewById(R.id.note_show_content);
        showStatusBar = (LinearLayout) findViewById(R.id.note_show_status_bar);
        showToolbar = (Toolbar) findViewById(R.id.note_show_toolbar);
        showDate = (TextView) findViewById(R.id.note_show_date);

        showStatusBar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                UtilTools.getBarHeight(getResources())));

        date = getIntent().getStringExtra("date");

        // 查询时间戳获取唯一对象而不是标题或内容
        // 多个文档可能存在相同标题或内容， 修改其中一个整体都会被修改
        if (date != "") {
            instance = DataSupport
                    .where("date == ?", date)
                    .find(NoteBean.class);
            if (instance.size() > 0) {
                showTitle.setText(instance.get(0).getTitle());
                showContent.setText(instance.get(0).getContent());
                showDate.setText(formatDate(instance.get(0).getDate()));
            }

        }

        setSupportActionBar(showToolbar);
        getSupportActionBar().setTitle(" ");
        showToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        showToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // toolbar上menu的点击事件
        showToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.note_show_edit:
                        showTitle.setEnabled(true);
                        showContent.setEnabled(true);
                        break;
                    case R.id.note_show_finish:
                        saveNote();
                        finish();
                        break;
                    case R.id.note_show_delete:
                        if (instance.get(0).isSaved()) {
                            instance.get(0).delete();
                        }
                        finish();
                }
                return true;
            }
        });
    }

    // 更新数据库中相应项的数据
    private void saveNote() {
        if (instance.size() > 0 && instance != null) {
            String date = instance.get(0).getDate();
            ContentValues values = new ContentValues();
            values.put("title", showTitle.getText().toString());
            values.put("content", showContent.getText().toString());
            values.put("date", String.valueOf(System.currentTimeMillis()));
            DataSupport.updateAll(NoteBean.class, values, "date == ?", date);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_show_menu, menu);
        return true;
    }

    // 避免因错误按下返回键导致编辑内容的丢失
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveNote();
    }

    private String formatDate(String date) {
        long timeStamp = Long.parseLong(date);
        Date time = new Date(timeStamp);
        if (time != null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");   // 将数据库中读取出的时间戳格式化
            return format.format(time);
        }

       return "";
    }

}

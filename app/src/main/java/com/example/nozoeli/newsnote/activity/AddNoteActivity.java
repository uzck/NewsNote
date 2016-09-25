package com.example.nozoeli.newsnote.activity;

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

import com.example.nozoeli.newsnote.R;
import com.example.nozoeli.newsnote.bean.NoteBean;
import com.example.nozoeli.newsnote.util.UtilTools;

/**
 * Created by nozoeli on 16-4-23.
 */
public class AddNoteActivity extends AppCompatActivity {

    private LinearLayout statusBar;
    private Toolbar addToolbar;
    private EditText title;
    private EditText content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        title = (EditText) findViewById(R.id.add_note_title);
        content = (EditText) findViewById(R.id.add_note_content);

        statusBar = (LinearLayout) findViewById(R.id.add_note_status_bar);
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                UtilTools.getBarHeight(getResources())));

        addToolbar = (Toolbar)findViewById(R.id.add_note_toolbar);
        setSupportActionBar(addToolbar);
        getSupportActionBar().setTitle(" ");
        addToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        addToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                saveNote();
                finish();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.finish_edit, menu);

        return true;
    }

    // 取消返回键的功能
    @Override
    public void onBackPressed() {
    }

    private void saveNote() {
        if (title.getText().toString() != "") {
            NoteBean note = new NoteBean(title.getText().toString(),
                    content.getText().toString(),
                    String.valueOf(System.currentTimeMillis()));
            note.save();
        }

    }


}

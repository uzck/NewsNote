package com.example.nozoeli.newsnote.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.nozoeli.newsnote.R;
import com.example.nozoeli.newsnote.activity.AddNoteActivity;
import com.example.nozoeli.newsnote.activity.MainActivity;
import com.example.nozoeli.newsnote.activity.NoteShowActivity;
import com.example.nozoeli.newsnote.adapter.NoteRecyclerAdapter;
import com.example.nozoeli.newsnote.bean.NewsListBean;
import com.example.nozoeli.newsnote.bean.NoteBean;
import com.example.nozoeli.newsnote.listener.EndlessScrollListener;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by nozoeli on 16-4-13.
 */
public class NoteFragment extends Fragment {

    private RecyclerView noteRecycler;
    private NoteRecyclerAdapter noteAdapter;
    private Toolbar toolbar;

    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> tempTitleList = new ArrayList<>();
    private ArrayList<String> contentList = new ArrayList<>();
    private ArrayList<String> tempContentList = new ArrayList<>();
    private ArrayList<String> dateList = new ArrayList<>();
    private ArrayList<String> tempDateList = new ArrayList<>();
    private ArrayList<String> recordTitleList = new ArrayList<>();
    private ArrayList<String> recordContentList = new ArrayList<>();
    private ArrayList<String> recordDateList = new ArrayList<>();

    private EndlessScrollListener mScrollListener;
    private LinearLayoutManager manager;
    private Handler handler;
    private android.widget.SearchView searchView;


    public static NoteFragment newInstance() {
        NoteFragment instance =  new NoteFragment();
        instance.titleList = new ArrayList<>();
        instance.contentList = new ArrayList<>();
        instance.dateList = new ArrayList<>();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                noteAdapter.notifyDataSetChanged();
            }
        };
        SQLiteDatabase db = Connector.getDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_note, container, false);
        noteRecycler = (RecyclerView) root.findViewById(R.id.note_recycler);
        noteAdapter = new NoteRecyclerAdapter(getContext(), titleList, contentList,
                dateList);
        manager = new LinearLayoutManager(getContext());

        if (toolbar != null) {
            toolbar.getMenu().removeItem(R.id.action_settings);
            toolbar.inflateMenu(R.menu.menu_note_fragment);
        }


        MenuItem searchItem = toolbar.getMenu().findItem(R.id.search_btn);
        searchView = (android.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
           searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
               @Override
               public boolean onQueryTextSubmit(String query) {
                   return false;
               }

               @Override
               public boolean onQueryTextChange(final String newText) {
                   new Thread() {
                   @Override
                   public void run() {
                       super.run();
                       searchResult(newText);
                   }
               }.start();
                   return false;
               }
           });
        }

        mScrollListener = new EndlessScrollListener(manager) {
            @Override
            public void onLoadMore() {
                int count = manager.getItemCount();
                getDataFromDatabase(count);
            }
        };

        noteRecycler.setLayoutManager(manager);
        noteRecycler.setAdapter(noteAdapter);

        noteRecycler.addOnScrollListener(mScrollListener);
        noteAdapter.setListener(new NoteRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(final NoteRecyclerAdapter.NoteHolder holder) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent jumpToShow = new Intent(getActivity(), NoteShowActivity.class);
                        jumpToShow.putExtra("date", holder.getDate());
                        startActivity(jumpToShow);
                    }
                });

            }
        });
        FloatingActionButton addButton = (FloatingActionButton) root.findViewById(R.id.add_note);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddNoteActivity.class));
            }
        });
        return root;
    }



    private void getDataFromDatabase() {

        List<NoteBean> noteList = DataSupport
                .limit(8)                       // 限制每次加载的数据为8个，避免一次性加载过多造成卡顿
                .order("date desc")
                .find(NoteBean.class);
        if (noteList != null && noteList.size() > 0) {
            for (int i = 0; i < noteList.size(); i++) {
                tempTitleList.add(noteList.get(i).getTitle());
                tempContentList.add(noteList.get(i).getContent());
                tempDateList.add(noteList.get(i).getDate());
            }
        }

        // 从NoteShowActivity返回时通过搜索框中文本长度是否为0来进行数据还原

        if (searchView.getQuery().length() > 0) {   // 不为0时恢复查询界面
//            titleList.addAll(recordTitleList);
//            contentList.addAll(recordContentList);
//            dateList.addAll(recordDateList);
            searchResult(searchView.getQuery().toString());
        } else {                                    // 为0时恢复初始界面
            titleList.addAll(tempTitleList);
            contentList.addAll(tempContentList);
            dateList.addAll(tempDateList);
        }
        noteAdapter.notifyDataSetChanged();

    }

    private void getDataFromDatabase(int offset) {
        List<NoteBean> noteList = DataSupport
                .limit(5)
                .offset(offset)
                .order("id desc")
                .find(NoteBean.class);
        if (noteList != null && noteList.size() > 0) {
            for (int i = 0; i < noteList.size(); i++) {
                tempTitleList.add(noteList.get(i).getTitle());
                titleList.add(noteList.get(i).getTitle());
                tempContentList.add(noteList.get(i).getContent());
                contentList.add(noteList.get(i).getContent());
                tempDateList.add(noteList.get(i).getDate());
                dateList.add(noteList.get(i).getDate());
            }
        }
        noteAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStop() {
        super.onStop();
        titleList.clear();
        contentList.clear();
        dateList.clear();
        tempTitleList.clear();
        tempContentList.clear();
        tempDateList.clear();
    }

    // 查询相应标题的笔记
    private void searchResult(String value) {

        // 如果搜索框中的关键词为空，恢复原始界面
        if (value.equals("")) {
            recoveryData();
        } else {
            List<NoteBean> result = DataSupport
                    .where("title like ?", "%" + value + "%")
                    .find(NoteBean.class);
            titleList.clear();
            contentList.clear();
            dateList.clear();
            recordTitleList.clear();
            recordContentList.clear();
            recordDateList.clear();
            // recordList记录查询后的结果以便还原查询界面
            for (NoteBean bean : result) {
                titleList.add(bean.getTitle());
                recordTitleList.add(bean.getTitle());
                contentList.add(bean.getContent());
                recordContentList.add(bean.getContent());
                dateList.add(bean.getDate());
                recordDateList.add(bean.getDate());
            }
        }
        mScrollListener.isSearch = true;
        Message message = new Message();
        handler.sendMessage(message);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tempTitleList.size() == 0) {
            getDataFromDatabase();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        toolbar.getMenu().removeItem(R.id.search_btn);
        toolbar.inflateMenu(R.menu.main);
    }

    // 数据源从tempList里面读取原始数据
    private void recoveryData() {
        titleList.clear();
        contentList.clear();
        dateList.clear();

        titleList.addAll(tempTitleList);
        contentList.addAll(tempContentList);
        dateList.addAll(tempDateList);
        recordTitleList.clear();
        recordContentList.clear();
        recordDateList.clear();

        mScrollListener.isSearch = false;
    }

}

package com.example.nozoeli.newsnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nozoeli.newsnote.R;

import java.util.ArrayList;

/**
 * Created by nozoeli on 16-4-16.
 */
public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteHolder> {

    private Context context;
    private ArrayList<String> titleList;
    private ArrayList<String> contentList;
    private ArrayList<String> dateList;

    public NoteRecyclerAdapter(Context context,
                               ArrayList<String> titleList,
                               ArrayList<String> contentList,
                               ArrayList<String> dateList) {
        this.context = context;
        this.titleList = titleList;
        this.contentList = contentList;
        this.dateList = dateList;
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(NoteHolder holder);
    }

    private OnRecyclerItemClickListener mListener;

    public void setListener(OnRecyclerItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {
        if (mListener != null && holder != null) {
            mListener.onItemClick(holder);
        }
        holder.noteTitle.setText(titleList.get(position));
        holder.noteContent.setText(contentList.get(position));
        holder.setDate(dateList.get(position));
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
        return new NoteHolder(view);
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class NoteHolder extends RecyclerView.ViewHolder {

        private TextView noteTitle;
        private TextView noteContent;
        private String date;

        public TextView getNoteTitle() {
            return noteTitle;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setNoteTitle(TextView noteTitle) {
            this.noteTitle = noteTitle;
        }

        public TextView getNoteContent() {
            return noteContent;
        }

        public void setNoteContent(TextView noteContent) {
            this.noteContent = noteContent;
        }

        public NoteHolder(View itemView) {
            super(itemView);
            noteTitle = (TextView) itemView.findViewById(R.id.note_title);
            noteContent = (TextView) itemView.findViewById(R.id.note_content);
        }
    }

}

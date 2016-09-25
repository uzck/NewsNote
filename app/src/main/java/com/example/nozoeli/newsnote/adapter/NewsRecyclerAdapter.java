package com.example.nozoeli.newsnote.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nozoeli.newsnote.R;
import com.example.nozoeli.newsnote.bean.NewsListBean;
import com.squareup.picasso.Picasso;

/**
 * Created by nozoeli on 16-4-13.
 */
public class NewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private NewsListBean newsList;
    private FragmentManager fm;
    private OnRecyclerItemClickListener mListener;
    public static final int TEXT_NEWS = 0;
    public static final int IMG_ALBUM = 1;
    public static final int FOOTER = 2;
    private boolean showFooter = true;

    public NewsRecyclerAdapter(Context context, NewsListBean list, FragmentManager fm) {
        this.context = context;
        this.newsList = list;
        this.fm = fm;
    }

    // 暴露借口，具体逻辑在NewsListFragment中实现
    public interface OnRecyclerItemClickListener {
        void onItemClick(RecyclerView.ViewHolder holder, int position);
    }

    public void setListener(OnRecyclerItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mListener != null && holder != null) {
           mListener.onItemClick(holder, position);
        }
        if (holder.getItemViewType() == TEXT_NEWS) {
            if (newsList.getDigestList().get(position) != null) {
                ((NewsTextViewHolder)holder).setId(newsList.getDocIdList().get(position));
                ((NewsTextViewHolder)holder).detailTitle.setText(newsList.getTitleList().get(position));
                ((NewsTextViewHolder)holder).detailDigest.setText(newsList.getDigestList().get(position));
                if (newsList.getArticleImgList().get(position).trim().length() != 0) {                    //有些新闻没有略缩图,检查url长度是否为0
                   asynLoadPic(position, ((NewsTextViewHolder)holder).detailPic);
                }
            }

        } else if (holder.getItemViewType() == IMG_ALBUM) {
            ((ImgAlbumViewHolder)holder).setLink(newsList.getPhotoSetIDList().get(position));
            if (newsList.getDigestList().get(position) == null) {
                ((ImgAlbumViewHolder)holder).mTitle.setText(newsList.getTitleList().get(position));
                asynLoadPic(position, ((ImgAlbumViewHolder)holder).mFirstImg);
                asynLoadPic(position, ((ImgAlbumViewHolder)holder).mSecondImg, 0);
                asynLoadPic(position, ((ImgAlbumViewHolder)holder).mThirdImg, 1);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (newsList != null) {       // Activity Resume时可能会产生空指针
            if (showFooter) {
                return newsList.getTitleList().size() + 1;
            } else {
                return newsList.getTitleList().size();
            }
        }

        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root;
        if (viewType == FOOTER) {
            root = LayoutInflater.from(context).inflate(R.layout.footer_view, parent, false);
            return new FootHolder(root);
        }
        if (viewType == TEXT_NEWS) {
            root = LayoutInflater.from(context).inflate(R.layout.news_list_item, parent, false);
            return new NewsTextViewHolder(root);
        } else if (viewType == IMG_ALBUM) {
            root = LayoutInflater.from(context).inflate(R.layout.album_img_list_item, parent, false);
            return new ImgAlbumViewHolder(root);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return FOOTER;
        }
        if (newsList.getIsAlbumFlag().get(position) == "true") {
            return IMG_ALBUM;
        }
        return TEXT_NEWS;
    }

    public static class NewsTextViewHolder extends RecyclerView.ViewHolder {

        private ImageView detailPic;
        private TextView detailTitle;
        private TextView detailDigest;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public NewsTextViewHolder(View itemView) {
            super(itemView);

            detailPic = (ImageView) itemView.findViewById(R.id.scale_pic);
            detailTitle = (TextView) itemView.findViewById(R.id.item_title);
            detailDigest = (TextView) itemView.findViewById(R.id.item_digest);
        }
    }

    public static class ImgAlbumViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private ImageView mFirstImg;
        private ImageView mSecondImg;
        private ImageView mThirdImg;
        private String link = null;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public ImgAlbumViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.pic_title);
            mFirstImg = (ImageView) itemView.findViewById(R.id.first_pic_container);
            mSecondImg = (ImageView) itemView.findViewById(R.id.second_pic_container);
            mThirdImg = (ImageView) itemView.findViewById(R.id.third_pic_container);
        }
    }

    public static class FootHolder extends RecyclerView.ViewHolder {

        public FootHolder(View itemView) {
            super(itemView);
        }
    }

    private void asynLoadPic(int position, ImageView imageView) {
        Picasso.with(context)
                .load(newsList.getArticleImgList().get(position))
                .resize(100, 80)
                .centerCrop()
                .placeholder(R.drawable.common_fail_placeholder)
                .into(imageView);
    }

    private void asynLoadPic(int position, ImageView imageView, int count) {
        Picasso.with(context)
                .load(newsList.getImgExtraList().get(position).getImgExtra().get(count))
                .resize(100, 80)
                .centerCrop()
                .placeholder(R.drawable.common_fail_placeholder)
                .into(imageView);
    }

    public void removeFooter() {
        showFooter = false;
    }


}

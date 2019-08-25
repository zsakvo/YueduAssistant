package cc.zsakvo.yueduassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.bean.CacheBook;
import cc.zsakvo.yueduassistant.bean.CacheChapter;
import cc.zsakvo.yueduassistant.listener.FlagsListener;
import cc.zsakvo.yueduassistant.view.BookDetailActivity;

public class CacheChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<CacheChapter> mCacheChapters;
    private List<Boolean> flags;
    private FlagsListener flagsListener;


    public CacheChapterAdapter(Context context, FlagsListener flagsListener) {
        this.context = context;
        mCacheChapters = new ArrayList<>();
        flags = new ArrayList<>();
        this.flagsListener = flagsListener;
    }

    public void setItems(List<CacheChapter> data, List<Boolean> flags) {
        this.mCacheChapters.addAll(data);
        this.flags = flags;
        notifyDataSetChanged();
    }

    public void cleanItems() {
        this.mCacheChapters.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cache_chapter, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            recyclerViewHolder.chapterText.setText(mCacheChapters.get(position).getName());
            if (flags.get(position)) {
                recyclerViewHolder.chapterText.setPaintFlags(recyclerViewHolder.chapterText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else {
                recyclerViewHolder.chapterText.setPaintFlags(recyclerViewHolder.chapterText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            recyclerViewHolder.mView.setOnClickListener(view -> {
                boolean status = !flags.get(position);
                flags.set(position, status);
                this.flagsListener.setFlags(flags);
                if (status) {
                    recyclerViewHolder.chapterText.setPaintFlags(recyclerViewHolder.chapterText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                } else {
                    recyclerViewHolder.chapterText.setPaintFlags(recyclerViewHolder.chapterText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCacheChapters.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView chapterText;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            chapterText = itemView.findViewById(R.id.cache_chapter_check);
        }
    }
}
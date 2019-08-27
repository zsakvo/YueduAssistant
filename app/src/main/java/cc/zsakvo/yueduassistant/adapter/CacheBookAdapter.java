package cc.zsakvo.yueduassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.bean.CacheBook;
import cc.zsakvo.yueduassistant.view.BookDetailActivity;

public class CacheBookAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<CacheBook> mCacheBooks;



    public CacheBookAdapter(Context context) {
        this.context = context;
        mCacheBooks = new ArrayList<>();
    }

    public void setItems(List<CacheBook> data) {
        this.mCacheBooks.addAll(data);
        notifyDataSetChanged();
    }

    public void cleanItems(){
        this.mCacheBooks.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cache_book, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;

            recyclerViewHolder.bookName.setText("《"+mCacheBooks.get(position).getName()+"》");
            recyclerViewHolder.bookInfo.setText("\t共"+mCacheBooks.get(position).getChapterNum()+"章\n\t来源："+mCacheBooks.get(position).getSource());


            recyclerViewHolder.mView.setOnClickListener(view -> {
                Logger.d("activity!");
                Intent intent = new Intent(context, BookDetailActivity.class);
                Logger.d(mCacheBooks.get(position).getInfo());
                intent.putExtra("info", mCacheBooks.get(position).getInfo());
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCacheBooks.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView mCard;
        private TextView bookName;
        private TextView bookInfo;
        private View mView;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mCard = itemView.findViewById(R.id.card_cache_book);
            bookName = itemView.findViewById(R.id.card_cache_book_name);
            bookInfo = itemView.findViewById(R.id.card_cache_book_source);
        }
    }
}
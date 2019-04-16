package cc.zsakvo.yueduassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduassistant.R;
import cc.zsakvo.yueduassistant.bean.CacheBook;

//public class CacheBookAdapter extends RecyclerView.Adapter<CacheBookAdapter.ViewHolder> implements View.OnClickListener{
//
//    private List<String> bookName = new ArrayList<>();
//    private List<String> bookInfo = new ArrayList<>();
//    private OnItemClickListener mOnItemClickListener = null;
//
//    public CacheBookAdapter(List<String> bookName, List<String> bookInfo){
//
//        this.bookName = bookName;
//        this.bookInfo = bookInfo;
//    }
//
//
//    public static interface OnItemClickListener {
//        void onItemClick(View view , int position);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.mOnItemClickListener = listener;
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (mOnItemClickListener != null) {
//            mOnItemClickListener.onItemClick(v,(int)v.getTag());
//        }
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = View.inflate(parent.getContext(), R.layout.list_cache_book, null);
//        view.setOnClickListener(this);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.nameText.setText(bookName.get(position));
//        holder.infoText.setText(bookInfo.get(position));
//        holder.itemView.setTag(position);
//    }
//    @Override
//    public int getItemCount() {
//        return  bookName == null ? 0 : bookName.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView nameText;
//        TextView infoText;
//        ViewHolder(View itemView) {
//            super(itemView);
//            nameText = (TextView) itemView.findViewById(R.id.card_cache_book_name);
//            infoText = (TextView) itemView.findViewById(R.id.card_cache_book_source);
//        }
//    }
//}

public class CacheBookAdapter extends BaseQuickAdapter<CacheBook, BaseViewHolder>{

    public CacheBookAdapter(int layoutResId, List<CacheBook> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CacheBook item) {
        helper.setText(R.id.card_cache_book_name,"《"+item.getName()+"》");
        helper.setText(R.id.card_cache_book_source,"\t来源："+item.getSource());
    }
}



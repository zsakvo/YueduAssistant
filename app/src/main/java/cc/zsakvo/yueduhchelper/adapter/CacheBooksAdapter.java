package cc.zsakvo.yueduhchelper.adapter;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cc.zsakvo.yueduhchelper.R;

public class CacheBooksAdapter extends RecyclerView.Adapter<CacheBooksAdapter.ViewHolder> implements View.OnClickListener{

    private List<String> bookName = new ArrayList<>();
    private List<String> bookInfo = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    public CacheBooksAdapter(List<String> bookName, List<String> bookInfo){

        this.bookName = bookName;
        this.bookInfo = bookInfo;
    }


    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(),R.layout.cache_list, null);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameText.setText(bookName.get(position));
        holder.infoText.setText(bookInfo.get(position));
        holder.itemView.setTag(position);
    }
    @Override
    public int getItemCount() {
        return  bookName == null ? 0 : bookName.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView infoText;
        ViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.list_bookName);
            infoText = (TextView) itemView.findViewById(R.id.list_bookInfo);
        }
    }
}
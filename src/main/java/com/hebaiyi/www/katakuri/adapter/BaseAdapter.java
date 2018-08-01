package com.hebaiyi.www.katakuri.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter {

    private List<T> mList;
    private List<T> mCopyList;
    private int mLayoutId;
    private ItemClickListener mListener;

    public BaseAdapter(List<T> list, int layoutId) {
        mCopyList = list;
        mList = list;
        mLayoutId = layoutId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, null);
        return new CommonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        // 获取元素
        T t = mList.get(position);
        // 获取viewHolder
        CommonViewHolder commHolder = (CommonViewHolder) holder;
        // 设置监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
        // 更新UI
        renewListItem(commHolder, t, position);
    }

    /**
     *  获取当前数据源
     * @return 当前数据源
     */
    public List<T> getData() {
        return mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setItemClickListener(ItemClickListener listener) {
        mListener = listener;
    }

    /**
     *  更换数据源
     * @param list 数据源
     */
    public void exchangeData(List<T> list) {
        if (mList != list) {
            mList = list;
            notifyDataSetChanged();
        }
    }

    /**
     *  格式化数据，回到初始化状态
     */
    public void formatDate() {
        if (!mList.equals(mCopyList)) {
            mList = mCopyList;
            notifyDataSetChanged();
        }
    }

    /**
     *  获取初始数据源
     * @return 初始数据源
     */
    public List<T> getFormatDate(){
        return mCopyList;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    protected abstract void renewListItem(CommonViewHolder viewHolder, T t, int position);

    public interface ItemClickListener {
        void onItemClick(int position);
    }

}

package com.example.recyleviewdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;


/**
 * this is a BaseAdapter for RecyclerView
 * author lizhangqu
 * version 1.0
 * date 2015/6/3.
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends BaseRecyclerViewAdapter.SparseArrayViewHolder > extends RecyclerView.Adapter<VH> {

    /**
     * click listener
     */
    protected OnRVItemClickListener<T> mOnItemClickListener;
    /**
     * long click listener
     */
    protected OnRVItemLongClickListener<T> mOnItemLongClickListener;
    /**
     * data
     */
    protected List<T> mList;
    protected Context mContext;
    protected int mViewId;

    /**
     * @param list the datas to attach the adapter
     */
    public  BaseRecyclerViewAdapter(Context context,List<T> list ,int viewId){
        mList = list;
        mContext = context;
        mViewId = viewId;
    }

    /**
     * get a item by index
     *
     * @param position
     * @return
     */
    protected T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * set a long click listener
     *
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(OnRVItemLongClickListener<T> onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    /**
     * set a click listener
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnRVItemClickListener<T> onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * inflate a view by viewgroup ,id ,etc
     *
     * @param viewGroup
     * @param layoutId
     * @return
     */
    protected View inflateItemView(ViewGroup viewGroup, int layoutId) {
        return inflateItemView(viewGroup, layoutId, false);
    }

    /**
     * inflate a view by viewgroup ,id ,etc
     *
     * @param viewGroup
     * @param layoutId
     * @param attach
     * @return
     */
    protected View inflateItemView(ViewGroup viewGroup, int layoutId, boolean attach) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, attach);
    }


    /**
     * a final function to avoid you override
     * use template design pattern
     *
     * @param vh
     * @param position
     */
    @Override
    public final void onBindViewHolder(VH vh, int position){
        final T item = getItem(position);
        bindDataToItemView(vh, item);
        bindItemViewClickListener(vh, item);
    }


    /**
     * bind data to itemview
     *
     * @param vh   viewholder
     * @param item item
     */
    protected abstract void bindDataToItemView(VH vh, T item);


    /**
     * bind click listner to itemview
     *
     * @param vh   viewholder
     * @param item item
     */
    protected final void bindItemViewClickListener(final VH vh, final T item) {
        if (mOnItemClickListener != null) {
            vh.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(view, item,vh.getLayoutPosition());
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onLongClick(v, item,vh.getLayoutPosition());
                    return true;
                }
            });
        }
    }


    /**
     * BaseViewHolder
     * using bindViewById(View view,int id) function to handle the relations between view and viewId
     */
    public abstract static class BaseViewHolder extends RecyclerView.ViewHolder{

        public BaseViewHolder(View itemView) {
            super(itemView);
            findView();
        }

        /**
         * you need to override this method to bind view in the viewholder
         * you'd better use bindViewById(View view,int id)
         */
        protected abstract void findView();

        /**
         * generic function to findViewById
         *
         * @param id viewId
         * @return the view found
         */
        protected <T extends View> T findViewById(int id) {
            return (T) itemView.findViewById(id);
        }

    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(mViewId,parent,false);
        SparseArrayViewHolder holder = new SparseArrayViewHolder(v);
        return (VH)holder;
    }

    public static class SparseArrayViewHolder extends RecyclerView.ViewHolder{
        private  SparseArray<View> views;
        public SparseArrayViewHolder(View itemView){
            super(itemView);
            views = new SparseArray<View>();
        }
        public <T extends View> T getView(int id){
            View view = views.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                views.put(id, view);
            }
            return (T) view;
        }

        public SparseArrayViewHolder setText(int viewId, String value) {
            TextView view = getView(viewId);
            view.setText(value);
            return SparseArrayViewHolder.this;
        }

        public SparseArrayViewHolder setTextColor(int viewId, int textColor) {
            TextView view = getView(viewId);
            view.setTextColor(textColor);
            return SparseArrayViewHolder.this;
        }

        public SparseArrayViewHolder setImageResource(int viewId, int imageResId) {
            ImageView view = getView(viewId);
            view.setImageResource(imageResId);
            return SparseArrayViewHolder.this;
        }

        public SparseArrayViewHolder setBackgroundColor(int viewId, int color) {
            View view = getView(viewId);
            view.setBackgroundColor(color);
            return SparseArrayViewHolder.this;
        }

        public SparseArrayViewHolder setBackgroundResource(int viewId, int backgroundRes) {
            View view = getView(viewId);
            view.setBackgroundResource(backgroundRes);
            return SparseArrayViewHolder.this;
        }

        public SparseArrayViewHolder setVisible(int viewId, boolean visible) {
            View view = getView(viewId);
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            return SparseArrayViewHolder.this;
        }

        public SparseArrayViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
            View view = getView(viewId);
            view.setOnClickListener(listener);
            return SparseArrayViewHolder.this;
        }

        public SparseArrayViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
            View view = getView(viewId);
            view.setOnTouchListener(listener);
            return SparseArrayViewHolder.this;
        }

        public SparseArrayViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
            View view = getView(viewId);
            view.setOnLongClickListener(listener);
            return SparseArrayViewHolder.this;
        }

        public SparseArrayViewHolder setTag(int viewId, Object tag){
            View view = getView(viewId);
            view.setTag(tag);
            return SparseArrayViewHolder.this;
        }
    }

    public interface OnRVItemClickListener<T> {
        void onClick(View view, T item,int position);
    }

    public interface OnRVItemLongClickListener<T> {
        void onLongClick(View view, T item,int position);
    }
}
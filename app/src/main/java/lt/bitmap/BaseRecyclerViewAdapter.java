package lt.bitmap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import static lt.bitmap.Utils.checkNotNull;


/**
 * 基础{@link android.support.v7.widget.RecyclerView.Adapter}类，增加了增删改查方法
 */
abstract class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {

    List<T> mDataList = new ArrayList<>();
    Context mContext;
    LayoutInflater mInflater;

    BaseRecyclerViewAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void addAllData(List<T> dataList) {
        checkNotNull(dataList);
        mDataList.addAll(dataList);
    }

    public void clearAllData() {
        mDataList.clear();
    }

    public void addData(T data) {
        checkNotNull(data);
        mDataList.add(data);
    }

    public void setData(int position, T data) {
        checkNotNull(data);
        mDataList.set(checkPosition(position), data);
    }

    public void removeData(int position) {
        mDataList.remove(checkPosition(position));
    }

    private int checkPosition(int position) {
        if (position < 0 || position >= getItemCount()) {
            throw new IllegalArgumentException("position is valid");
        }
        return position;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}

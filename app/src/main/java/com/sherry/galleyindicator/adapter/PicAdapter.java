package com.sherry.galleyindicator.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sherry.galleyindicator.R;
import com.sherry.galleyindicator.util.DisplayUtil;

/**
 * Created by shanxs on 2017/9/11.
 */
public class PicAdapter extends BaseAdapter {
    private static final int INT_MAX = 2147483647;
    private Context mContext;
    private int[] mGalleys;

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public PicAdapter(Context context, int[] drawableInts) {
        this.mContext = context;
        this.mGalleys = drawableInts;
    }

    /**
     * @return 返回默认大小
     */
    @Override
    public int getCount() {
        if (mGalleys.length > 1) {
            return INT_MAX;
        } else {
            return mGalleys.length;
        }
    }

    /**
     * @return 返回真实大小
     */
    public int getSize() {
        if (mGalleys != null) {
            return mGalleys.length;
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mGalleys != null) {
            return mGalleys[position % mGalleys.length];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Item mItem;
        if (convertView == null) {
            mItem = new Item();
            convertView = View.inflate(mContext, R.layout.image_item, null);
            mItem.mIvItem = (ImageView) convertView.findViewById(R.id.image_item);
            ViewGroup.LayoutParams lp = mItem.mIvItem.getLayoutParams();
            lp.width = DisplayUtil.getScreenWidth(mContext);
            lp.height = lp.width / 2;
            mItem.mIvItem.setLayoutParams(lp);
            convertView.setTag(mItem);
        } else {
            mItem = (Item) convertView.getTag();
        }
        if (mGalleys.length > 0) {
            // 加载图片
            mItem.mIvItem.setImageResource(mGalleys[position % mGalleys.length]);
        }
        return convertView;
    }

    class Item {
        ImageView mIvItem;
    }

}
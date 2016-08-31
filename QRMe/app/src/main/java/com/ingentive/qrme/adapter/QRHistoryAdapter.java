package com.ingentive.qrme.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.ingentive.qrme.R;
import com.ingentive.qrme.activeandroid.QRHistoryTable;
import com.ingentive.qrme.activity.VideoViewActivity;
import com.ingentive.qrme.common.NetworkChangeReceiver;

import java.util.List;

/**
 * Created by PC on 22-08-2016.
 */
public class QRHistoryAdapter extends BaseAdapter {
    List<QRHistoryTable> qrList;
    private int res;
    private Context mContext;
    private static LayoutInflater inflater = null;

    public QRHistoryAdapter(Context context, int rowId,List<QRHistoryTable> qrList){
        this.mContext = context;
        this.res = rowId;
        this.qrList = qrList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return this.qrList.size();
        // return 1;
    }

    @Override
    public Object getItem(int position) {
        return qrList.get(position);
        //return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder vh = new ViewHolder();
        if (vi == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.custom_row_qr_history, parent, false);
            vh.ivQRHistory=(ImageView)vi.findViewById(R.id.iv_qr_history);
            vh.ivDelet=(ImageView)vi.findViewById(R.id.iv_qr_delete);
            vh.tvQRHistory=(TextView)vi.findViewById(R.id.tv_qr_history);

            int id = (int)System.currentTimeMillis();
            vi.setId(id);
            vi.setTag(vh);
        } else {
            vh = (ViewHolder) vi.getTag();
        }
        final ImageView ivQRHistory,ivDelet;
        final TextView tvQRHistory;

        ivQRHistory = vh.ivQRHistory;
        tvQRHistory=vh.tvQRHistory;
        ivDelet=vh.ivDelet;

        ivQRHistory.setImageBitmap(grThumb(qrList.get(position).videoPath));
        tvQRHistory.setText(qrList.get(position).videoUrl.substring(0, 15) + ".....  Watch");

        ivDelet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Delete().from(QRHistoryTable.class).where("video_url=?",qrList.get(position).getVideoUrl()).execute();
                qrList.remove(position);
                notifyDataSetChanged();
            }
        });
        tvQRHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkChangeReceiver.isConnected) {
                    Intent intent = new Intent(mContext, VideoViewActivity.class);
                    intent.putExtra("url", qrList.get(position).getVideoUrl());
                    //videoUrl=qrLit.get(position).getVideoUrl();
                    intent.putExtra("intent_", "qr");
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "Please make sure, your network connection is ON ", Toast.LENGTH_LONG).show();
                }
            }
        });

        return vi;
    }

    public class ViewHolder {
        ImageView ivQRHistory,ivDelet;
        TextView tvQRHistory;
    }

    public Bitmap grThumb(String path){
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path,
                MediaStore.Images.Thumbnails.MINI_KIND);
        return thumb;
    }
}

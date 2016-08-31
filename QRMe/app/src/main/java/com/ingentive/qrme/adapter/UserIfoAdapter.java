package com.ingentive.qrme.adapter;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ingentive.qrme.R;
import com.ingentive.qrme.activeandroid.UserInfoTable;

import java.util.List;

/**
 * Created by PC on 19-08-2016.
 */
public class UserIfoAdapter extends BaseAdapter {
    List<String> userInfo;
    private int res;
    private Context mContext;
    private static LayoutInflater inflater = null;
    private UserInfoTable userInfoTable;

    public UserIfoAdapter(Context context, int rowId,List<String> userInfo,UserInfoTable userInfoTable){
        this.mContext = context;
        this.res = rowId;
        this.userInfo = userInfo;
        this.userInfoTable=userInfoTable;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
       return this.userInfo.size();
       // return 1;
    }

    @Override
    public Object getItem(int position) {
       return userInfo.get(position);
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
            vi = inflater.inflate(R.layout.custom_row_user_info, parent, false);
            vh.tvInfo = (TextView) vi.findViewById(R.id.tv_info);
            vh.ivInfo=(ImageView)vi.findViewById(R.id.iv_info);
            vh.btnEdit=(Button)vi.findViewById(R.id.btn_edit);
//            int id = vi.generateViewId();
            int id = (int)System.currentTimeMillis();
            vi.setId(id);
            vi.setTag(vh);
        } else {
            vh = (ViewHolder) vi.getTag();
        }
        final TextView tvInfo;
        final ImageView ivInfo;
        final Button btnEdit;

        switch (position){
            case 0:
                vh.ivInfo.setBackgroundResource(R.drawable.user);
                vh.tvInfo.setText(userInfo.get(position));
                break;
            case 1:
                vh.ivInfo.setBackgroundResource(R.drawable.email);
                vh.tvInfo.setText(userInfo.get(position));
                break;
            case 2:
                vh.ivInfo.setBackgroundResource(R.drawable.facebook);
                vh.tvInfo.setText(userInfo.get(position));
                break;
            case 3:
                vh.ivInfo.setBackgroundResource(R.drawable.twitter_icon);
                vh.tvInfo.setText(userInfo.get(position));
                break;
            case 4:
                vh.ivInfo.setBackgroundResource(R.drawable.linkdin);
                vh.tvInfo.setText(userInfo.get(position));
                break;
            case 5:
                vh.ivInfo.setBackgroundResource(R.drawable.number);
                vh.tvInfo.setText(userInfo.get(position));
                break;
        }
        tvInfo = vh.tvInfo;
        ivInfo = vh.ivInfo;
        btnEdit=vh.btnEdit;


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });
        return vi;
    }

    public class ViewHolder {
        TextView tvInfo;
        ImageView ivInfo;
        Button btnEdit;
    }


    void showDialog(final int position) {
        // Create custom dialog object
        final Dialog dialog = new Dialog(mContext);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog);
        // Set dialog title
        dialog.setTitle("Add or Change value");
        dialog.show();

        final Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        final Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText et_dialog = (EditText) dialog.findViewById(R.id.et_dialog);
        final ImageView iv_dialog = (ImageView) dialog.findViewById(R.id.iv_dialog);

        if (position == 0) {
            iv_dialog.setBackgroundResource(R.drawable.user);
            et_dialog.setText(userInfo.get(position));
        } else if (position == 1) {
            iv_dialog.setBackgroundResource(R.drawable.email);
            et_dialog.setText(userInfo.get(position));
        } else if (position == 2) {
            iv_dialog.setBackgroundResource(R.drawable.facebook);
            et_dialog.setText(userInfo.get(position));
        } else if (position == 3) {
            iv_dialog.setBackgroundResource(R.drawable.twitter_icon);
            et_dialog.setText(userInfo.get(position));
        } else if (position == 4) {
            iv_dialog.setBackgroundResource(R.drawable.linkdin);
            et_dialog.setText(userInfo.get(position));
        } else if (position == 5) {
            iv_dialog.setBackgroundResource(R.drawable.number);
            et_dialog.setText(userInfo.get(position));
        }
        // if decline button is clicked, close the custom dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_dialog.getText().toString().trim().isEmpty()) {
                    et_dialog.setError("You can't leave this empty.");
                    et_dialog.requestFocus();
                } else {
                    if (position == 0) {
                        userInfoTable.userName = et_dialog.getText().toString();
                    } else if (position == 1) {
                        userInfoTable.userEmail = et_dialog.getText().toString();
                    } else if (position == 2) {
                        userInfoTable.userFacebook = et_dialog.getText().toString();
                    } else if (position == 3) {
                        userInfoTable.userTwitter = et_dialog.getText().toString();
                    } else if (position == 4) {
                        userInfoTable.userLinkedin = et_dialog.getText().toString();
                    } else if (position == 5) {
                        userInfoTable.userPhone = et_dialog.getText().toString();
                    }
                    userInfoTable.save();
                    dialog.dismiss();
                }
            }
        });
    }

}

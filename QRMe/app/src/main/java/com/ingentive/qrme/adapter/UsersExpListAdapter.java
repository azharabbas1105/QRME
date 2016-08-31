package com.ingentive.qrme.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ingentive.qrme.R;
import com.ingentive.qrme.activeandroid.UsersTable;

import java.util.List;

/**
 * Created by PC on 15-08-2016.
 */
public class UsersExpListAdapter extends BaseExpandableListAdapter {

    private List<UsersTable> usersTableList;
    private Context mContext;
    private static LayoutInflater inflater = null;

    public UsersExpListAdapter(Context context, List<UsersTable> parent) {
        this.usersTableList = parent;
        this.mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    //counts the number of group/parent items so the list knows how many times calls getGroupView() method
    public int getGroupCount() {
        return usersTableList.size();
    }

    @Override
    //counts the number of children items so the list knows how many times calls getChildView() method
    public int getChildrenCount(int i) {
        return usersTableList.get(i).getUsersChildList().size();
    }

    @Override
    //gets the title of each parent/group
    public Object getGroup(int i) {
        return usersTableList.get(i).getUsersChildList().toString();
    }

    @Override
    //gets the name of each item
    public Object getChild(int i, int i1) {
        return usersTableList.get(i).getUsersChildList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    //in this method you must set the text to see the parent/group on the list
    public View getGroupView(final int groupPosition, boolean b, View rowView, final ViewGroup viewGroup) {


        final TextView tvUserName;
        View vi = rowView;
        ViewHolderParent vhp = new ViewHolderParent();

        if (vi == null) {
            vi = inflater.inflate(R.layout.custom_row_expand_user_name, viewGroup, false);

            vhp.tvUserName = (TextView) vi.findViewById(R.id.tv_expand_user_name);
            int id = vi.generateViewId();
            vi.setId(id);
            vi.setTag(vhp);

        } else {
            vhp = (ViewHolderParent) vi.getTag();
        }



        tvUserName=vhp.tvUserName;
        usersTableList.get(groupPosition).setView(vhp);
        tvUserName.setText(usersTableList.get(groupPosition).getUserName().toString());

        return vi;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    //in this method you must set the text to see the children on the list
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View view, final ViewGroup viewGroup) {

        View childView = view;

        ViewHolderChild vhc = new ViewHolderChild();
        final TextView tv_expand_child_email,
                tv_expand_child_facebook,
                tv_expand_child_twitter,
                tv_expand_child_linkdin,
                tv_expand_child_phone;
        final ImageView iv_expand_child_email,
                iv_expand_child_facebook,
                iv_expand_child_twitter,
                iv_expand_child_linkdin,
                iv_expand_child_phone;

        final RelativeLayout email_layout,facebook_layout,twitter_layout,linkdin_layout,phone_layout;

        if (childView == null) {
            childView = inflater.inflate(R.layout.custom_row_expand_user_child, null);
            vhc.iv_expand_child_email = (ImageView) childView.findViewById(R.id.iv_expand_child_email);
            vhc.iv_expand_child_facebook = (ImageView) childView.findViewById(R.id.iv_expand_child_facebook);
            vhc.iv_expand_child_twitter = (ImageView) childView.findViewById(R.id.iv_expand_child_twitter);
            vhc.iv_expand_child_linkdin = (ImageView) childView.findViewById(R.id.iv_expand_child_linkdin);
            vhc.iv_expand_child_phone = (ImageView) childView.findViewById(R.id.iv_expand_child_phone);;


            vhc.tv_expand_child_email = (TextView) childView.findViewById(R.id.tv_expand_child_email);
            vhc.tv_expand_child_facebook = (TextView) childView.findViewById(R.id.tv_expand_child_facebook);
            vhc.tv_expand_child_twitter = (TextView) childView.findViewById(R.id.tv_expand_child_twitter);
            vhc.tv_expand_child_linkdin = (TextView) childView.findViewById(R.id.tv_expand_child_linkdin);
            vhc.tv_expand_child_phone = (TextView) childView.findViewById(R.id.tv_expand_child_phone);

            vhc.email_layout=(RelativeLayout)childView.findViewById(R.id.email_layout);
            vhc.facebook_layout=(RelativeLayout)childView.findViewById(R.id.facebook_layout);
            vhc.twitter_layout=(RelativeLayout)childView.findViewById(R.id.twitter_layout);
            vhc.linkdin_layout=(RelativeLayout)childView.findViewById(R.id.linkdin_layout);
            vhc.phone_layout=(RelativeLayout)childView.findViewById(R.id.phone_layout);

            int id = childView.generateViewId();
            childView.setId(id);
            childView.setTag(vhc);

        } else {
            vhc = (ViewHolderChild) childView.getTag();
        }
        iv_expand_child_email=vhc.iv_expand_child_email;
        iv_expand_child_facebook=vhc.iv_expand_child_facebook;
        iv_expand_child_twitter=vhc.iv_expand_child_twitter;
        iv_expand_child_linkdin=vhc.iv_expand_child_linkdin;
        iv_expand_child_phone=vhc.iv_expand_child_phone;


        tv_expand_child_email=vhc.tv_expand_child_email;
        tv_expand_child_facebook=vhc.tv_expand_child_facebook;
        tv_expand_child_twitter=vhc.tv_expand_child_twitter;
        tv_expand_child_linkdin=vhc.tv_expand_child_linkdin;
        tv_expand_child_phone=vhc.tv_expand_child_phone;

        email_layout=vhc.email_layout;
        facebook_layout=vhc.facebook_layout;
        twitter_layout=vhc.twitter_layout;
        linkdin_layout=vhc.linkdin_layout;
        phone_layout=vhc.phone_layout;


        if(usersTableList.get(groupPosition).getUsersChildList().get(childPosition).getUserEmail().equals("")){
            email_layout.setVisibility(View.GONE);
        }else {
            email_layout.setVisibility(View.VISIBLE);
            iv_expand_child_email.setBackgroundResource(R.drawable.email);
            tv_expand_child_email.setText(usersTableList.get(groupPosition).getUsersChildList().get(childPosition).getUserEmail());
        }
        if(usersTableList.get(groupPosition).getUsersChildList().get(childPosition).getUserFacebook().equals("")){
            facebook_layout.setVisibility(View.GONE);
        }else {
            facebook_layout.setVisibility(View.VISIBLE);
            iv_expand_child_facebook.setBackgroundResource(R.drawable.facebook);
            tv_expand_child_facebook.setText(usersTableList.get(groupPosition).getUsersChildList().get(childPosition).getUserFacebook());
        }
        if(usersTableList.get(groupPosition).getUsersChildList().get(childPosition).getUserTwitter().equals("")){
            twitter_layout.setVisibility(View.GONE);
        }else{
            twitter_layout.setVisibility(View.VISIBLE);
            iv_expand_child_twitter.setBackgroundResource(R.drawable.twitter);
            tv_expand_child_twitter.setText(usersTableList.get(groupPosition).getUsersChildList().get(childPosition).getUserTwitter());
        }
        if(usersTableList.get(groupPosition).getUsersChildList().get(childPosition).getUserLinkedin().equals("")){
            linkdin_layout.setVisibility(View.GONE);
        }else{
            linkdin_layout.setVisibility(View.VISIBLE);
            iv_expand_child_linkdin.setBackgroundResource(R.drawable.linkdin);
            tv_expand_child_linkdin.setText(usersTableList.get(groupPosition).getUsersChildList().get(childPosition).getUserLinkedin());
        }
        if(usersTableList.get(groupPosition).getUsersChildList().get(childPosition).getUserPhone().equals("")){
            phone_layout.setVisibility(View.GONE);
        }else{
            phone_layout.setVisibility(View.VISIBLE);
            iv_expand_child_phone.setBackgroundResource(R.drawable.number);
            tv_expand_child_phone.setText(usersTableList.get(groupPosition).getUsersChildList().get(childPosition).getUserPhone());
        }



//        switch (groupPosition){
//            case 0:
//                tvExpandChild.ivInfo.setBackgroundResource(R.drawable.user);
//                vh.tvInfo.setText(userInfo.get(position));
//                break;
//            case 1:
//                vh.ivInfo.setBackgroundResource(R.drawable.email);
//                vh.tvInfo.setText(userInfo.get(position));
//                break;
//            case 2:
//                vh.ivInfo.setBackgroundResource(R.drawable.facebook);
//                vh.tvInfo.setText(userInfo.get(position));
//                break;
//            case 3:
//                vh.ivInfo.setBackgroundResource(R.drawable.twitter_icon);
//                vh.tvInfo.setText(userInfo.get(position));
//                break;
//            case 4:
//                vh.ivInfo.setBackgroundResource(R.drawable.linkdin);
//                vh.tvInfo.setText(userInfo.get(position));
//                break;
//            case 5:
//                vh.ivInfo.setBackgroundResource(R.drawable.number);
//                vh.tvInfo.setText(userInfo.get(position));
//                break;
//        }

        return childView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


    public class ViewHolderParent {
        TextView tvUserName;

    }

    public class ViewHolderChild {
        ImageView iv_expand_child_email,
                iv_expand_child_facebook,
                iv_expand_child_twitter,
                iv_expand_child_linkdin,
                iv_expand_child_phone;
        RelativeLayout email_layout,facebook_layout,twitter_layout,linkdin_layout,phone_layout;
        /*
        iv_expand_child_email,
        iv_expand_child_facebook,
        iv_expand_child_twitter,
        iv_expand_child_linkdin,
        iv_expand_child_phone
         */
        /*
        tv_expand_child_email,
        tv_expand_child_facebook,
        tv_expand_child_twitter,
        tv_expand_child_linkdin,
        tv_expand_child_phone
         */
        TextView tv_expand_child_email,
                tv_expand_child_facebook,
                tv_expand_child_twitter,
                tv_expand_child_linkdin,
                tv_expand_child_phone;
    }
}
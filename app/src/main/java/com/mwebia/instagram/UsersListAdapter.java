package com.mwebia.instagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.IOException;
import java.util.ArrayList;

//creating adapter to add layout tickets to our list view
public class UsersListAdapter extends BaseAdapter {

    private ArrayList<UsersInfo> usersInfo;
    private Context context;

    UsersListAdapter(Context context, ArrayList<UsersInfo> usersInfo){
        super();

        this.context = context;
        this.usersInfo = usersInfo;
    }

    @Override
    public int getCount() {
        return usersInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return usersInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.usersinfo_ticket, parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //inflating the user list ticket view;
        UsersInfo userDetails = usersInfo.get(position);


        try {
           Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), userDetails.uri);
            viewHolder.nameTextView.setText(userDetails.name);
            viewHolder.imageView.setImageBitmap(bitmap);


        } catch (IOException e) {
            e.printStackTrace();
        }



        return convertView;
    }


}

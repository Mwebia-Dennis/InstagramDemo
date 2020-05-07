package com.mwebia.instagram;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

   class ViewHolder {
        ImageView imageView;
        TextView nameTextView;

    ViewHolder(View view){

        imageView = view.findViewById(R.id.profileImage);
        nameTextView = view.findViewById(R.id.usernameTV);
    }
}

package com.example.andela.devhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ismail on 7/16/2017.
 */

public class DeveloperCardAdapter extends ArrayAdapter<DeveloperCardModel> {


    static class ViewHolder {
        public TextView usernameTextView;
        public ImageView developerAviImageView;
    }

    public DeveloperCardAdapter(Context context, ArrayList<DeveloperCardModel> developerCard) {
        super(context, 0, developerCard);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        // reuse views
        if (listItemView == null) {

            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.devcard, parent, false);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.usernameTextView = listItemView
                    .findViewById(R.id.github_username);
            viewHolder.developerAviImageView = listItemView
                    .findViewById(R.id.github_profile_pic);


            listItemView.setTag(viewHolder);
        }

        //fill data
        ViewHolder holder = (ViewHolder) listItemView.getTag();
        DeveloperCardModel devCard = getItem(position);

        Picasso.with(getContext()).load(devCard != null ? devCard.getAvaterResourceId() : null)
                .into(holder.developerAviImageView);


        holder.usernameTextView.setText(devCard.getUsername());

        return listItemView;


    }
}

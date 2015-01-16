package com.prateek.gem.groups;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prateek.gem.AppConstants;
import com.prateek.gem.BaseActivity;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Group;
import com.prateek.gem.utility.ImageLoader;
import com.prateek.gem.utility.Utils;

import java.util.ArrayList;

/**
 * Created by prateek on 23/11/14.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private ArrayList<Group> mGroups = null;
    private BaseActivity mScreen = null;
    private ImageLoader imgLoader;

    public GroupsAdapter(BaseActivity screen) {
        mScreen = screen;
        imgLoader = new ImageLoader(mScreen);
    }

    public void setGroups(ArrayList<Group> groups) {
        mGroups = groups;
        Group personalGroup = Group.getPersonalGroup();
        mGroups.add(0, personalGroup);
        DebugLogger.message("mGroups"+mGroups);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView vGroupName;
        public TextView vGroupDescription;
        public ImageView vGroupAvatar;
        public View vView;

        public ViewHolder(View v) {
            super(v);
            vView = v;
            vGroupName = (TextView) v.findViewById(R.id.vGroupName);
            vGroupDescription = (TextView) v.findViewById(R.id.vGroupDescription);
            vGroupAvatar = (ImageView) v.findViewById(R.id.vGroupAvatar);
            vGroupAvatar.setColorFilter(Utils.getColorFilter(Utils.ColorFilter.PRIMARYDARK));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final Group group = mGroups.get(position);
        DebugLogger.message("Group :: "+group+", position :: "+position);
        if(group.getTotalOfExpense() >= 0) {
            viewHolder.vGroupDescription.setText("Tap to make expenses");
        } else {
            viewHolder.vGroupDescription.setText("Expenses : " + group.getTotalOfExpense());
        }
        viewHolder.vGroupName.setText(group.getGroupName());

        if(group.getGroupIcon() != null) {
            if (group.getGroupIcon().equals("0") || TextUtils.isEmpty(group.getGroupIcon().toString())) {
                viewHolder.vGroupAvatar.setImageDrawable(mScreen.getResources().getDrawable(R.drawable.group_avatar));
            } else {
                imgLoader.DisplayImage(AppConstants.URL_IMAGES + Uri.parse(group.getGroupIcon()).getPath(), viewHolder.vGroupAvatar);
            }
        }

        viewHolder.vView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainLandingScreen) mScreen).onItemSelected(group, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
}

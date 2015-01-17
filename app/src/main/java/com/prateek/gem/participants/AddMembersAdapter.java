package com.prateek.gem.participants;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prateek.gem.BaseActivity;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Member;
import com.prateek.gem.utility.AppDataManager;
import com.prateek.gem.utility.Utils;

import java.util.ArrayList;

/**
 * Created by prateek on 14/1/15.
 */
public class AddMembersAdapter extends RecyclerView.Adapter<AddMembersAdapter.ViewHolder> {

    private LayoutInflater mInflater = null;
    private BaseActivity currentScreen = null;

    public AddMembersAdapter(BaseActivity context) {
        mInflater = LayoutInflater.from(context);
        currentScreen = context;
    }

    @Override
    public AddMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.add_member_existing_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(AddMembersAdapter.ViewHolder holder, final int position) {
        final Member member = ((AddMembersActivity)currentScreen).mExistingMembers.get(position);
        holder.memberName.setText(member.getMemberName());
        holder.memberNumber.setText(member.getPhoneNumber());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugLogger.message("mPosition"+position);
            }
        });

        holder.removeMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((AddMembersActivity)currentScreen).mExistingMembers.get(position).getPhoneNumber().equals(AppDataManager.getUser().getPhoneNumber())) {
                    Utils.showToast(currentScreen, "Cannot remove admin");
                } else {
                    ((AddMembersActivity) currentScreen).mExistingMembers.remove(position);
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return ((AddMembersActivity)currentScreen).mExistingMembers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView memberName = null;
        private TextView memberNumber = null;
        private ImageView removeMember = null;
        private View parent = null;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            memberName = (TextView) itemView.findViewById(android.R.id.text1);
            memberNumber = (TextView) itemView.findViewById(android.R.id.text2);
            removeMember = (ImageView) itemView.findViewById(android.R.id.icon);

            removeMember.setColorFilter(Utils.getColorFilter(Utils.ColorFilter.PRIMARYDARK));
        }
    }
}

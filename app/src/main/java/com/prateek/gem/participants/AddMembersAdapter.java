package com.prateek.gem.participants;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prateek.gem.R;
import com.prateek.gem.model.Member;
import com.prateek.gem.utility.Utils;

import java.util.ArrayList;

/**
 * Created by prateek on 14/1/15.
 */
public class AddMembersAdapter extends RecyclerView.Adapter<AddMembersAdapter.ViewHolder> {

    private LayoutInflater mInflater = null;
    private ArrayList<Member> mMembers = null;

    public AddMembersAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setExistingMembers(ArrayList<Member> members) {
        mMembers = members;
    }

    @Override
    public AddMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.add_member_existing_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AddMembersAdapter.ViewHolder holder, int position) {
        final Member member = mMembers.get(position);
        holder.memberName.setText(member.getMemberName());
        holder.memberNumber.setText(member.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView memberName = null;
        private TextView memberNumber = null;
        private ImageView removeMember = null;

        public ViewHolder(View itemView) {
            super(itemView);
            memberName = (TextView) itemView.findViewById(android.R.id.text1);
            memberNumber = (TextView) itemView.findViewById(android.R.id.text2);
            removeMember = (ImageView) itemView.findViewById(android.R.id.icon);

            removeMember.setColorFilter(Utils.getColorFilter(Utils.ColorFilter.PRIMARYDARK));
        }
    }
}

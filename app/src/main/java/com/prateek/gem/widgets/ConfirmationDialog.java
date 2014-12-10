package com.prateek.gem.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.prateek.gem.AppConstants.ConfirmConstants;
import com.prateek.gem.R;

import java.util.ArrayList;
import java.util.List;

public class ConfirmationDialog extends DialogFragment {


    public interface OnModeConfirmListener {
        public void modeConfirmed();

        public void deleteMemberConfirmed(int memberId);

        public void deleteExpenseConfirmed(int expenseId);

        public void deleteItemConfirmed(String deleteItemConfirmed);

        public void openNewActivity(int requestCodeClickImage);
    }

    OnModeConfirmListener onModeConfirmListener;
    private int confirmId;
    GridAdapter gridAdapter;
    public static final String TITLE = "title";
    public static final String BUTTON1 = "button1";
    public static final String BUTTON2 = "button2";
    public static final String MESSAGE = "Message";

    public ConfirmationDialog() {
        super();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("on attach");
        try {
            onModeConfirmListener = (OnModeConfirmListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onModeConfirmListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        confirmId = getArguments().getInt(ConfirmConstants.CONFIRM_KEY);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_mode_detail);
        GridView buttonsGrid = (GridView) dialog.findViewById(R.id.buttonsGrid);
        TextView modeDetailTextView = (TextView) dialog.findViewById(R.id.detail);
        modeDetailTextView.setText(getArguments().getString("Message"));
        TextView dialogHeading = (TextView) dialog.findViewById(R.id.dialogHeading);
        dialogHeading.setText(getArguments().getString(TITLE));
        List<String> buttonsArray = new ArrayList<String>();
        buttonsArray.add(getArguments().getString(BUTTON1));
        buttonsArray.add(getArguments().getString(BUTTON2));
        buttonsGrid.setAdapter(new GridAdapter(buttonsArray, getActivity()));

        return dialog;


    }

    private class GridAdapter extends BaseAdapter {

        private Context _c;
        private List<String> buttonsArray;

        public GridAdapter(List<String> buttonsArray, FragmentActivity activity) {
            super();
            this.buttonsArray = buttonsArray;
            _c = activity;
        }

        @Override
        public int getCount() {
            return buttonsArray.size();
        }

        @Override
        public Object getItem(int position) {
            return buttonsArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(position == 0) {
                    v = li.inflate(R.layout.inactivebutton, null);
                } else {
                    v = li.inflate(R.layout.activebutton, null);
                }
            }

            final Button buttonText = (Button) v.findViewById(R.id.buttonText);
            buttonText.setText(buttonsArray.get(position));

            buttonText.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    System.out.println("in");
                    System.out.println(buttonText.getText());
                    if (position == 1) {
                        System.out.println(position);
                        dismiss();
                        switch (confirmId) {
                            case ConfirmConstants.FROM_ADD_GROUP:
                                System.out.println("group");
                                onModeConfirmListener.modeConfirmed();
                                break;
                            case ConfirmConstants.MEMBER_DELETE:
                            /*int memberId = getArguments().getInt(TMembers.MEMBER_ID);
							System.out.println("dlete"+memberId);
							onModeConfirmListener.deleteMemberConfirmed(memberId);*/
                                break;
                            case ConfirmConstants.EXPENSE_DELETE:
							/*int expenseId = getArguments().getInt(TExpenses.EXPENSE_ID);
							System.out.println("confirm dlete"+expenseId);
							onModeConfirmListener.deleteExpenseConfirmed(expenseId);*/
                                break;
                            case ConfirmConstants.ITEM_DELETE:
							/*String itemName = getArguments().getString("itemname");
							onModeConfirmListener.deleteItemConfirmed(itemName);*/
                                break;
                        }
                    } else {
                        System.out.println(position);
                        dismiss();
                    }

                }
            });

            return v;
        }


    }

}

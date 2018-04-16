package com.studybuddy.studybuddy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alex on 3/20/2018.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private List<GroupListItem> list;
    private Context context;

    public GroupListAdapter(List<GroupListItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupListItem groupListItem = list.get(position);
        //TODO this is causing the app to crash... not sure why
        holder.setTextViewHeader(groupListItem.getHeader());
        holder.setTextViewText(groupListItem.getText());
        System.out.println(groupListItem.getHeader());
        System.out.println(groupListItem.getText());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItems(List<GroupListItem> items){
        list = new ArrayList<>(items);
    }

    public GroupListItem removeItem(int position){
        final GroupListItem item = list.remove(position);
        notifyItemRemoved(position);
        return item;
    }

    public void addItem(int position, GroupListItem item){
        list.add(position,item);
        notifyItemInserted(position);
    }

    public void moveItem(int initPosition, int destination){
        final GroupListItem item = list.remove(initPosition);
        list.add(destination,item);
        notifyItemMoved(initPosition,destination);
    }

    public void animateTo(List<GroupListItem> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<GroupListItem> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final GroupListItem model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<GroupListItem> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final GroupListItem model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<GroupListItem> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final GroupListItem model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public LayoutInflater.Filter getFilter() {
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewHeader;
        private TextView textViewText;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.header);
            textViewText = itemView.findViewById(R.id.text);
        }
        public void setTextViewHeader(String text) {
            textViewHeader.setText(text);
        }
        public void setTextViewText(String text){
            textViewText.setText(text);
        }
    }
}

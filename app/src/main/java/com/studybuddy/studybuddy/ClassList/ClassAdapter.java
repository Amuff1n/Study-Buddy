package com.studybuddy.studybuddy.ClassList;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.studybuddy.studybuddy.R;
import com.studybuddy.studybuddy.RecyclerView.ClassPOJO;

import java.util.List;

/**
 * Created by kyle on 3/26/18.
 */
import android.widget.TextView;

import com.studybuddy.studybuddy.R;
import com.studybuddy.studybuddy.RecyclerView.ClassPOJO;

import java.util.List;

/*class LoadingViewHolder extends RecyclerView.ViewHolder {

    public ProgressBar progressBar;
    public LoadingViewHolder(View itemView){
        super(itemView);
        progressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar);
    }
}

class ClassViewHolder extends RecyclerView.ViewHolder {
    public TextView className;
    public ClassViewHolder(View itemView){
        super(itemView);
        className = (TextView) itemView.findViewById(R.id.className);
    }
}

public class ClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    final private int VIEW_CLASS_ITEM = 0, VIEW_CLASS_LOADING = 1;
    ClassLoader classLoader;
    boolean isLoading;
    Activity activity;
    List<ClassPOJO> classList;
    int visible = 5;
    int lastVisibleClass, totalClassCount;

    public ClassAdapter(RecyclerView recyclerView, Activity activity, List<ClassPOJO> classList) {
        this.activity = activity;
        this.classList = classList;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalClassCount = linearLayoutManager.getItemCount();
                lastVisibleClass = linearLayoutManager.findLastVisibleItemPosition();
                if(!isLoading && totalClassCount <= (lastVisibleClass + visible)){
                    if(classLoader != null){
                        classLoader.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(classList.get(position) == null)
            return VIEW_CLASS_LOADING;
        return VIEW_CLASS_ITEM;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(viewType == VIEW_CLASS_ITEM){
            View view = LayoutInflater.from(activity).inflate(R.layout.class_list_layout,parent,false);
            return new ClassViewHolder(view);
        }
        else if(viewType == VIEW_CLASS_LOADING){
            View view = LayoutInflater.from(activity).inflate(R.layout.load_class,parent,false);
            return new ClassViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if(holder instanceof ClassViewHolder){
            ClassPOJO item = classList.get(position);
            ClassViewHolder classHolder = (ClassViewHolder)holder;
            classHolder.className.setText(item.getClassName());
        }
        else if(holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount(){
       return classList.size();
    }
    public void setLoaded(){
        isLoading = false;
    }

}*/

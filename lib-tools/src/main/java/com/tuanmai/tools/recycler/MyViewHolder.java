package com.tuanmai.tools.recycler;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public final class MyViewHolder extends RecyclerView.ViewHolder {

    /**
     * 附加对象
     */
    private List<Object> mObjectList;

    public MyViewHolder(View view) {
        super(view);
    }

    /**
     * 通过控件的Id获取对于的控件
     */
    public <T extends View> T getView(@IdRes int id) {
        View view = itemView.findViewById(id);
        return (T) view;
    }

    public void setText(@IdRes int id,@StringRes int resID){
        String text=itemView.getContext().getString(resID);
        if(!TextUtils.isEmpty(text)){
            setText(id,text);
        }
    }

    public void setText(@IdRes int id,String text){
        if(null==text){
            text="";
        }
        View view=getView(id);
        if(view instanceof TextView){
            ((TextView)view).setText(text);
        }else if(view instanceof Button){
            ((Button)view).setText(text);
        }else if(view instanceof EditText){
            ((EditText)view).setText(text);
        }
    }

    public void setText(@IdRes int id,Spanned spanned){
        if(null==spanned) return;
        View view=getView(id);
        if(view instanceof TextView){
            ((TextView)view).setText(spanned);
        }else if(view instanceof Button){
            ((Button)view).setText(spanned);
        }else if(view instanceof EditText){
            ((EditText)view).setText(spanned);
        }
    }

    public void setImageResource(@IdRes int id,int resID){
        View view=getView(id);
        if(view instanceof ImageView){
            ((ImageView)view).setImageResource(resID);
        }
    }

    public void setTag(@IdRes int id,Object object){
        View view=getView(id);
        if(null!=view){
            view.setTag(object);
        }
    }


    public String getText(@IdRes int id){
        View view=getView(id);
        if(null!=view){
            if(view instanceof TextView){
                return ((TextView)view).getText().toString();

            }else if(view instanceof EditText){
                return ((EditText)view).getText().toString();
            }
        }
        return null;
    }

    public void setVisibility(@IdRes int id,int visibility){
        View view=getView(id);
        if(null!=view){
            view.setVisibility(visibility);
        }
    }

    public void setBackgroundColor(@IdRes int id, int color){
        View view=getView(id);
        if(null!=view){
            view.setBackgroundColor(color);
        }
    }


    public void setOnClickListener(@IdRes int id,View.OnClickListener listener){
        View view=getView(id);
        if(null!=view){
            view.setOnClickListener(listener);
        }
    }

    public List<Object> getmObjectList() {
        return mObjectList;
    }

    public void setObjectList(List<Object> mObjectList) {
        this.mObjectList = mObjectList;
    }
}

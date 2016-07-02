package udacity.popmoviestage_2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by guoecho on 2016/6/29.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {
    private Context context;
    private int resource;
    private ArrayList<Review> mReview = new ArrayList<>();

    public ReviewAdapter(Context context, int resource, ArrayList<Review> mReview){
        super(context, resource, mReview);
        this.context = context;
        this.resource = resource;
        this.mReview = mReview;
    }

    public void setReview(ArrayList<Review> mReview){
        this.mReview = mReview;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if(view == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.listView = (ListView) view.findViewById(R.id.lv_review);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        Review review = mReview.get(position);
        return view;
    }

    private class ViewHolder {
        ListView listView;
    }
}

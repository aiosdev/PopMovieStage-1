package udacity.popmoviestage_2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
            holder.ReviewAuthor = (TextView) view.findViewById(R.id.author);
            holder.ReviewContent = (TextView) view.findViewById(R.id.content);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        Review review = mReview.get(position);
        holder.ReviewAuthor.setText(review.getAuthor());
        holder.ReviewContent.setText(review.getReview());

        return view;
    }

    private class ViewHolder {
        TextView ReviewAuthor;
        TextView ReviewContent;
    }
}

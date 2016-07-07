package udacity.popmoviestage_2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by guoecho on 2016/7/1.
 */
public class VideoAdapter extends ArrayAdapter<Video> {
    private Context context;
    private int resource;
    private ArrayList<Video> mVideo = new ArrayList<>();

    public VideoAdapter(Context context, int resource, ArrayList<Video> mVideo){
        super(context, resource, mVideo);
        this.context = context;
        this.resource = resource;
        this.mVideo = mVideo;
    }

    public void setVideo(ArrayList<Video> mVideo){
        this.mVideo = mVideo;
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
            holder.VideoKey = (ImageView) view.findViewById(R.id.key);
            holder.VideoName = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        Video video = mVideo.get(position);
        System.out.println("======================name"+ video.getTrailerName());

        holder.VideoName.setText(video.getTrailerName());
        return view;
    }

    private class ViewHolder {
        ImageView VideoKey;
        TextView VideoName;
    }
}

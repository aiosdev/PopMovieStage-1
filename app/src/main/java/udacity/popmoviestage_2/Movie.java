package udacity.popmoviestage_2;

import android.os.Parcel;
import android.os.Parcelable;

/**
I use this class to store the information for each movie as a movie object. As part of the first Android project
I developed, I thought this a good solution. As I progressed, I learned a better way to store and retrieve information,
using content loaders in later projects.
*/

public class Movie implements Parcelable {
    private String image;
    private String title;
    private String desc;
    private String year;
    private String rating;
    private String votes;
    private String id;


    public Movie() {super();}

    protected Movie(Parcel in) {
        image = in.readString();
        title = in.readString();
        desc = in.readString();
        year = in.readString();
        rating = in.readString();
        votes = in.readString();
        id = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getImage(){return image;}

    public void setImage(String image) {this.image=image;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title=title;}

    public String getDesc(){return desc;}

    public void setDesc(String desc){this.desc=desc;}

    public String getYear(){return year;}

    public void setYear(String year){this.year=year;}

    public String getRating(){return rating;}

    public void setRating (String rating){this.rating=rating;}

    public String getVotes(){return votes;}

    public void setVotes(String votes){this.votes=votes;}

    public String getId(){return id;}

    public void setId(String id){this.id=id;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(desc);
        dest.writeString(year);
        dest.writeString(rating);
        dest.writeString(votes);

    }
}
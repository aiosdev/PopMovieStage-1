package udacity.popmoviestage_2;

/**
 * Created by guoecho on 2016/7/1.
 */
public class Review {
    private String autherId;
    private String review;
    private String author;

    public Review() {
        super();
    }

    public String getAutheridId(){return autherId;}

    public void setAutheridId(String autherid){this.autherId=autherId;}

    public String getReview(){return review;}

    public void setReview(String review){this.review=review;}

    public String getAuthor(){return author;}

    public void setAuthor(String author){this.author=author;}
}

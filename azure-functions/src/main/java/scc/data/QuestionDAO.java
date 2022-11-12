package scc.data;

public class QuestionDAO {
    private String _rid;
    private String _ts;
    private String auctionId;
    private String text;
    private String userId;
    private String id;


    public QuestionDAO(){
    }

    public QuestionDAO(Question q){
        this(q.getAuctionId(),q.getText(),q.getUserId(),q.getId());
    }

    public QuestionDAO(String auctionId, String text, String userId, String id) {
        this.auctionId = auctionId;
        this.text = text;
        this.userId = userId;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String get_rid() {
        return _rid;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public String get_ts() {
        return _ts;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Question toQuestion(){return new Question(this.text,this.userId,this.id);}
}

package scc.data;

public class QuestionDAO {
    private String _rid;
    private String _ts;
    private String text;
    private String user;
    private String id;
    private String reply;


    public QuestionDAO(){
    }

    public QuestionDAO(Question q){
        this(q.getText(),q.getUser());
    }

    public QuestionDAO( String text, String user) {
        this.text = text;
        this.user = user;

    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "QuestionDAO{" +
                "text='" + text + '\'' +
                ", userId='" + user + '\'' +
                ", id='" + id + '\'' +
                ", answer='" + reply + '\'' +
                '}';
    }

    public Question toQuestion(){return new Question(this.text,this.user,this.id, this.reply);}
}

package scc.data;

public class QuestionDAO {
    private String _rid;
    private String _ts;
    private String text;
    private String userId;
    private String id;
    private String answer;


    public QuestionDAO(){
    }

    public QuestionDAO(Question q){
        this(q.getText(),q.getUserId());
    }

    public QuestionDAO( String text, String userId) {
        this.text = text;
        this.userId = userId;

    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "QuestionDAO{" +
                "text='" + text + '\'' +
                ", userId='" + userId + '\'' +
                ", id='" + id + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

    public Question toQuestion(){return new Question(this.text,this.userId,this.id, this.answer);}
}

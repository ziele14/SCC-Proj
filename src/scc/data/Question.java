package scc.data;

public class Question {
    private String text;
    private String userId;
    private String id;
    private String answer;

    public Question(String text, String userId, String id,String answer) {
        this.text = text;
        this.userId = userId;
        this.id = id;
        this.answer = answer;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                ", userId='" + userId + '\'' +
                ", id='" + id + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}

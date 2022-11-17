package scc.data;

public class Question {
    private String text;
    private String user;
    private String id;
    private String reply;

    public Question(String text, String user, String id, String reply) {
        this.text = text;
        this.user = user;
        this.id = id;
        this.reply = reply;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                ", userId='" + user + '\'' +
                ", id='" + id + '\'' +
                ", answer='" + reply + '\'' +
                '}';
    }
}

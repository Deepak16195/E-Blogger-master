package in.org.eonline.eblog.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by user on 08-08-2017.
 */

public class FaqPojo {

    @SerializedName("faq")
    public ArrayList<Faq> faq;

    public ArrayList<Faq> getFaq() {
        return faq;
    }

    public void setFaq(ArrayList<Faq> faq) {
        this.faq = faq;
    }

    public static class Faq {
        @SerializedName("id")
        public String id;
        @SerializedName("question")
        public String question;
        @SerializedName("answer")
        public String answer;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
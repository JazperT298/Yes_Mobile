package com.theyestech.yes_mobile.models;

import java.util.ArrayList;

public class Question {

    private String question_id;
    private String question_quiz_id;
    private String question_value;
    private String question_correct_answer;
    private ArrayList<Answer> answers;

    public static class Answer {
        private String answer_id;
        private String answer_value;
        private String answer_isCorrect;

        public String getAnswer_id() {
            return answer_id;
        }

        public void setAnswer_id(String answer_id) {
            this.answer_id = answer_id;
        }

        public String getAnswer_value() {
            return answer_value;
        }

        public void setAnswer_value(String answer_value) {
            this.answer_value = answer_value;
        }

        public String getAnswer_isCorrect() {
            return answer_isCorrect;
        }

        public void setAnswer_isCorrect(String answer_isCorrect) {
            this.answer_isCorrect = answer_isCorrect;
        }
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getQuestion_quiz_id() {
        return question_quiz_id;
    }

    public void setQuestion_quiz_id(String question_quiz_id) {
        this.question_quiz_id = question_quiz_id;
    }

    public String getQuestion_value() {
        return question_value;
    }

    public void setQuestion_value(String question_value) {
        this.question_value = question_value;
    }

    public String getQuestion_correct_answer() {
        return question_correct_answer;
    }

    public void setQuestion_correct_answer(String question_correct_answer) {
        this.question_correct_answer = question_correct_answer;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}

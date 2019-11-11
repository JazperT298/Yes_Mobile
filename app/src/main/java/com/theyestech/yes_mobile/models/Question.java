package com.theyestech.yes_mobile.models;

import java.util.ArrayList;

public class Question {

    private String question_id;
    private String question_quiz_id;
    private String question_value;
    private String question_correct_answer;
    private String question_is_answered;
    private ArrayList<Answer> answers;

    public static class Answer {
        private String choice_id;
        private String choice_value;
        private String choice_isCorrect;

        public String getChoice_id() {
            return choice_id;
        }

        public void setChoice_id(String choice_id) {
            this.choice_id = choice_id;
        }

        public String getChoice_value() {
            return choice_value;
        }

        public void setChoice_value(String choice_value) {
            this.choice_value = choice_value;
        }

        public String getChoice_isCorrect() {
            return choice_isCorrect;
        }

        public void setChoice_isCorrect(String choice_isCorrect) {
            this.choice_isCorrect = choice_isCorrect;
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

    public String getQuestion_is_answered() {
        return question_is_answered;
    }

    public void setQuestion_is_answered(String question_is_answered) {
        this.question_is_answered = question_is_answered;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}

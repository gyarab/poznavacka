package com.example.timad.poznavacka;

/**
 * objekt pro odpověď
 */

public class AnswerObject {
    private String answer;
    private String fieldName;

    public AnswerObject() {
    }

    public AnswerObject(String answer, String fieldName) {
        this.answer = answer;
        this.fieldName = fieldName;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}

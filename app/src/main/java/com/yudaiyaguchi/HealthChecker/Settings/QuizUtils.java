package com.yudaiyaguchi.HealthChecker.Settings;


import java.util.Map;

/**
 * This stores all the information regarding to each quiz before storing to database.
 */
public class QuizUtils {
    private static QuizUtils instance;
    private int[] questionIds;
    private int currentQuestionCount;
    private int[] valueArray;
    private String[] answers;
    private String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public void initializeArray(int length) {
        valueArray = new int[length];
        answers = new String[length];
    }

    public void setEachValue(int value) {
        valueArray[currentQuestionCount] = value;
    }

    public void setAnswer(String answer) {
        answers[currentQuestionCount] = answer;
    }

    public int[] getValueArray() {
        return valueArray;
    }

    public String[] getAnswerArray() {
        return answers;
    }

    public int getCurrentQuestionCount() {
        return currentQuestionCount;
    }

    public void setCurrentQuestionCount(int currentQuestionCount) {
        this.currentQuestionCount = currentQuestionCount;
    }

    public boolean isQuizEnded() {
        return currentQuestionCount + 1 == questionIds.length;
    }

    // Restrict the constructor from being instantiated
    private QuizUtils(){}

    public static void setInstance(QuizUtils instance) {
        QuizUtils.instance = instance;
    }

    public int[] getQuestionIds() {
        return questionIds;
    }

    public int getQuestionId() {
        return questionIds[currentQuestionCount];
    }

    public void setQuestionIds(int[] questionIds) {
        this.questionIds = questionIds;
    }

    public void increaseCounter() {
        currentQuestionCount++;
    }
    public void decrementCounter() {
        currentQuestionCount--;
    }

    public static synchronized QuizUtils getInstance(){
        if(instance==null){
            instance=new QuizUtils();
        }
        return instance;
    }
}

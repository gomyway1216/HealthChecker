package com.yudaiyaguchi.HealthChecker.Settings;


import java.util.Map;

public class QuizUtils {
    private static QuizUtils instance;
    private Map<Integer,String> languageIds;
    private int chosenLanguageId;
    private int[] questionIds;
    private boolean isGoing;
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

    public boolean isGoing() {
        return isGoing;
    }

    public void setGoing(boolean going) {
        isGoing = going;
    }

    public int getCurrentQuestionCount() {
        return currentQuestionCount;
    }

    public void setCurrentQuestionCount(int currentQuestionCount) {
        this.currentQuestionCount = currentQuestionCount;
    }

    public boolean isQuizEnded() {
        if(currentQuestionCount + 1 == questionIds.length)
            return true;
        else
            return false;
    }

    // Restrict the constructor from being instantiated
    private QuizUtils(){}

    public static void setInstance(QuizUtils instance) {
        QuizUtils.instance = instance;
    }

    public boolean getIsGoing() {
        return isGoing;
    }

    public void setIsGoing(boolean isGoing) {
        isGoing = isGoing;
    }

    public Map<Integer, String> getLanguageIds() {
        return languageIds;
    }

    public int getChosenLanguageId() {
        return chosenLanguageId;
    }

    public int[] getQuestionIds() {
        return questionIds;
    }

    public int getQuestionId() {
        return questionIds[currentQuestionCount];
    }

    public void setLanguageIds(Map<Integer, String> languageIds) {
        this.languageIds = languageIds;
    }

    public void setChosenLanguageId(int chosenLanguageId) {
        this.chosenLanguageId = chosenLanguageId;
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

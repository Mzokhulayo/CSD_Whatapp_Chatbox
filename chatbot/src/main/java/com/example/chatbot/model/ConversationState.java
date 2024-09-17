package com.example.chatbot.model;


import java.util.HashMap;
import java.util.Map;

public class ConversationState {
    private String step;  // Tracks which step the user is on
    private Map<String, String> userData;  // Stores user data temporarily

    public ConversationState() {
        this.step = "greeting";  // Initial step when user starts the conversation
        this.userData = new HashMap<>();
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public Map<String, String> getUserData() {
        return userData;
    }

    public void setUserData(String key, String value) {
        this.userData.put(key, value);
    }

    public String getUserData(String key) {
        return this.userData.get(key);
    }

    public boolean hasCollectedAllData() {
        return userData.containsKey("name") && userData.containsKey("surname")
                && userData.containsKey("phone") && userData.containsKey("email");
    }
}


package com.example.acer.todo.model;

//   为了配合recyclerview 实现viewtype 不同的 item
public class CommunicationMessage {

    private String message;
    private int type;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

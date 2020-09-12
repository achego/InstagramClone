package com.example.instagramclone.Models;

public class CommentModel {

    private String publisher, comment;

    public CommentModel() {
    }

    public CommentModel(String publisher, String comment) {
        this.publisher = publisher;
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

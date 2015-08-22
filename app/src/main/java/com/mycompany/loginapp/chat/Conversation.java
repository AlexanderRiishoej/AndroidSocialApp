package com.mycompany.loginapp.chat;

import com.mycompany.loginapp.base.ApplicationMain;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Alexander on 05-03-2015.
 */
public class Conversation {

    /**
     * The Constant STATUS_SENDING.
     */
    public static final int STATUS_SENDING = 0;

    /**
     * The Constant STATUS_SENT.
     */
    public static final int STATUS_SENT = 1;

    /**
     * The Constant STATUS_FAILED.
     */
    public static final int STATUS_FAILED = 2;

    /**
     * The message.
     */
    private String message;

    /**
     * The status.
     */
    private int status = STATUS_SENT;

    /**
     * The date.
     */
    private Date date;

    /**
     * The sender.
     */
    private ParseUser sender;
    private ParseUser receiver;

    private boolean isProgress = false;
    private String chatMessageId;

    /**
     * Instantiates a new conversation.
     *
     * @param message    the message
     * @param date   the date
     * @param sender the sender
     */
    public Conversation(String message, Date date, ParseUser sender, ParseUser receiver) {
        this.message = message;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Instantiates a new conversation.
     */
    public Conversation() {
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Checks if is sent.
     *
     * @return true, if is sent
     */
    public boolean isSender() {
        return ApplicationMain.mCurrentParseUser.getUsername().equals(sender.getUsername());
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the sender.
     *
     * @return the sender
     */
    public ParseUser getChatUser() {
        return this.isSender() ? sender : receiver;
    }

    public ParseUser getSender(){
        return sender;
    }

    public ParseUser getReceiver(){
        return receiver;
    }

    /**
     * Sets the sender.
     *
     * @param sender the new sender
     */
    public void setSender(ParseUser sender) {
        this.sender = sender;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(int status) {
        this.status = status;
    }


    /** Progressbar */
    public void setProgress(boolean isProgress){
        this.isProgress = isProgress;
    }

    public boolean isProgress(){
        return this.isProgress;
    }

    public String getChatMessageId(){
        return this.chatMessageId;
    }
}

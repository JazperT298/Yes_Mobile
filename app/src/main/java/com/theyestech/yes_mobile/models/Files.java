package com.theyestech.yes_mobile.models;

import android.net.Uri;

import java.io.File;
import java.util.Date;

public class Files {

    private String sender;
    private String sendername;
    private String senderphoto;
    private String receiver;
    private String receivername;
    private String receiverphoto;
    private String fileName;
    private String fileType;
    private Date dateposted;
    public Files(String sender, String sendername, String senderphoto, String receiver, String receivername, String receiverphoto, String fileName, String fileType, Date dateposted) {
        this.sender = sender;
        this.sendername = sendername;
        this.senderphoto = senderphoto;
        this.receiver = receiver;
        this.receivername = receivername;
        this.receiverphoto = receiverphoto;
        this.fileName = fileName;
        this.fileType = fileType;
        this.dateposted = dateposted;
    }

    public Files() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getSenderphoto() {
        return senderphoto;
    }

    public void setSenderphoto(String senderphoto) {
        this.senderphoto = senderphoto;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceivername() {
        return receivername;
    }

    public void setReceivername(String receivername) {
        this.receivername = receivername;
    }

    public String getReceiverphoto() {
        return receiverphoto;
    }

    public void setReceiverphoto(String receiverphoto) {
        this.receiverphoto = receiverphoto;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getDateposted() {
        return dateposted;
    }

    public void setDateposted(Date dateposted) {
        this.dateposted = dateposted;
    }
}
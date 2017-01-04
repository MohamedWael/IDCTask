package com.blogspot.mowael.idctask.model;

/**
 * Created by moham on 1/3/2017.
 */

public class Item {

    private String message, name, url, status;
    private int code, id;

    /**
     *
     * @param fileId
     * @param name
     * @param url
     * @param status
     * @param code
     * @param message
     */
    public Item(int fileId, String name, String url, String status, int code, String message) {


        this.message = message;
        this.name = name;
        this.url = url;
        if (status.equals("")) {
            this.status = "unknown";
        } else {
            this.status = status;
        }
        this.code = code;
        this.id = fileId;
    }

    public String getMessage() {
        return message;
    }

    public String getFileName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public int getFileId() {
        return id;
    }
}

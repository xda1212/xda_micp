package com.micp.im.bean;

import com.hyphenate.chat.EMMessage;

/**
 * Created by weipeng on 16/8/15.
 */
public class PrivateMessage {
    public EMMessage message;
    public String uHead;

    public static PrivateMessage crateMessage(EMMessage message,String uHead){
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.message = message;
        privateMessage.uHead = uHead;
        return privateMessage;
    }



}

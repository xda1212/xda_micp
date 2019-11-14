package com.micp.im.bean;

/**
 * Created by weipeng on 2017/1/17.
 */

public class RecommendUser {
    public String id;
    public String user_nicename;
    public String avatar;
    public String avatar_thumb;
    public String coin;
    public String sex;//1:男，2：女
    public String signature;
    public String consumption;
    public String votestotal;
    public String province;
    public String city;
    public String birthday;
    public String issuper;
    public String lat;
    public String lng;
    public String expiretime;
    public String level;
    public String level_anchor;
    public String distance;
    public String online_status;

    public VipType vip;

    public class VipType {
        public String type;
    }
}

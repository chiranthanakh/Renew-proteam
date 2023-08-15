package com.proteam.renew.responseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatesResponsce {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("state_name")
    @Expose
    private String state_name;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("created_on")
    @Expose
    private String created_on;

    @SerializedName("updated_on")
    @Expose
    private String updated_on;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

}

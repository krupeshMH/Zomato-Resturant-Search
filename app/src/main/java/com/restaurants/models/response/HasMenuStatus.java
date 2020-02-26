
package com.restaurants.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HasMenuStatus {

    @SerializedName("delivery")
    @Expose
    private Integer delivery;
    @SerializedName("takeaway")
    @Expose
    private Integer takeaway;

    public Integer getDelivery() {
        return delivery;
    }

    public void setDelivery(Integer delivery) {
        this.delivery = delivery;
    }

    public Integer getTakeaway() {
        return takeaway;
    }

    public void setTakeaway(Integer takeaway) {
        this.takeaway = takeaway;
    }

}

package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Unit implements Serializable {
    @SerializedName("unit_id") public String unitId = "";
    @SerializedName("name") public String name = "";
    @SerializedName("uom") public String uom = "";
    @SerializedName("price") public String price = "";
    @SerializedName("primaryUnit") public String primaryUnit = "";
}

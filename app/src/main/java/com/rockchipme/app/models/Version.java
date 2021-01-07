package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

public class Version {
    @SerializedName("version") public String version;
    @SerializedName("platform") public String platform;
    @SerializedName("forceUpdate") public String forceUpdate;
}

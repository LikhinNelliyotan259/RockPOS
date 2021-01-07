package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alisons on 4/10/2018.
 */

public class VersionResponse implements Serializable {
    @SerializedName("response_text") public ResponseText responseText;
    @SerializedName("response_code") public String responseCode = "";

    public class ResponseText  {
        @SerializedName("version") public List<Version> version;
    }
}

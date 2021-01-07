package com.rockchipme.app.models;
import com.google.gson.annotations.SerializedName;
import com.rockchipme.app.helpers.Constants;

/**
 * Created by Alisons on 9/13/2017.
 */

public class Categories {
    //    @SerializedName("") public String ;
    @SerializedName("catId") public String catId = "";
    @SerializedName("name") public String name = "";
    @SerializedName("image") private String image = "";
    @SerializedName("pdtCount") public String pdtCount = "0";

//    @SerializedName("images") public List<Images> imageList =new ArrayList<Images>();
//    public class Images{
//        @SerializedName("image") public String image = "";
        public String getImage() {
            return Constants.categoriesImageBaseUrl+image;
        }
//    }

}
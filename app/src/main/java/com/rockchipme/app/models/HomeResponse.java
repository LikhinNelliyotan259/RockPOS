package com.rockchipme.app.models;

import com.rockchipme.app.helpers.Constants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alisons on 4/10/2018.
 */

public class HomeResponse implements Serializable {
    @SerializedName("response_text") public ResponseText responseText;
    @SerializedName("response_code") public String responseCode = "";

    public class ResponseText  {
        @SerializedName("recentlyViewd")public List<Products> recentlyViewedList;
        @SerializedName("slider")public List<Slider> sliderList;
        @SerializedName("products")public List<Products> productsList;
        @SerializedName("categoriesCount") public String categoriesCount;

        @SerializedName("restaurantDetails") public List<RestaurantDetails> restaurantDetailsList;

        @SerializedName("outlets") public List<RestaurantDetails> outlets;
        @SerializedName("categories") public List<Categories> categories;

        @SerializedName("address")public List<AddressResponse.Address> addressList;
        @SerializedName("cartCount") public int cartCount;

        @SerializedName("version") public List<Version> version;

        @SerializedName("status") public String status;
        @SerializedName("message") public String message;
    }

    public class Slider{
        @SerializedName("id") public String id;
        @SerializedName("title") public String title;
        @SerializedName("description") public String description;
        @SerializedName("type") public String type;
        @SerializedName("value") public String value;
        @SerializedName("image") private String image;

        public String getImage() {
            return Constants.sliderImageUrl + image;
        }
        //        "id": "1",
//                "title": "Slider one",
//                "description": "slider one descriptions",
//                "type": "1",
//                "value": "5",
//                "image": "1.jpg"

    }
}

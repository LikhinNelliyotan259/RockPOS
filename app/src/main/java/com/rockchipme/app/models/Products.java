package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;
import com.rockchipme.app.helpers.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisons on 9/13/2017.
 */
public class Products implements Serializable {
    //    @SerializedName("") public String  = "";
    @SerializedName("pdtId") public String pdtId = "";
    @SerializedName("code") public String code = "";
    @SerializedName("catId") public String catId = "";
    @SerializedName("cart_id") public String cartId = "";
    @SerializedName("rate") public String rate = "";
    @SerializedName("name") public String name = "";
    @SerializedName("desc") public String desc="";
//    @SerializedName("images") public String images = "";
    @SerializedName("taxLabel") public String taxLabel = "";
    @SerializedName("tax") public String tax = "";
    @SerializedName("cartCount") public String cartCount = "";
    @SerializedName("total") public String total = "";
    @SerializedName("quantity") public String quantity = "";
    @SerializedName("favourite") public String favourite = "";
    @SerializedName("image") public List<Images> imageList =new ArrayList<Images>();
    @SerializedName("modifiers") public List<List<Products>> modifiers;
    @SerializedName("selectedModifiers") public List<Products> selectedModifiers;
    @SerializedName("force") public List<List<Products>> force;
    @SerializedName("selectedForce") public List<Products> selectedForce;
    @SerializedName("units") public List<Unit> units;
    @SerializedName("unit_id") public String unit_id;
    @SerializedName("remarks") public String remarks;
    @SerializedName("unitName") public String unitName;
    @SerializedName("title") public String title = "";

    public class Images implements Serializable{
        @SerializedName("image") private String image = "";
        public String getImage() {
            return Constants.productsImageBaseUrl + image;
        }
    }



//    public String getImages() {
//        return Constants.productsImageBaseUrl+images;
//    }
}


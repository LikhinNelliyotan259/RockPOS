package com.rockchipme.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.rockchipme.app.models.AddressResponse;

import java.util.List;

/**
 * Created by Alisons on 9/22/2017.
 */
public class AddressHelper {

    SharedPreferences pref_login;
    SharedPreferences.Editor login_editor;

    public AddressHelper(Context context){
//        this.context = context;
//        this.address_id = address_id;
//        this.addressList= addressList;

        pref_login = context.getSharedPreferences(Constants.PREF_LOGIN,Context.MODE_PRIVATE);
        login_editor = pref_login.edit();

//        storeAddress();
    }

    public void storeAddress(List<AddressResponse.Address> addressList, String address_id){
        if (addressList!=null && addressList.size() > 0) {
//            List<AddressResponse.Address> addressList = addressList1;

            if (addressList.size() == 1) {
//                storeAddress(addressList, 0);
                storeAddress(addressList.get(0));
                return;
            }

            if (address_id != null && !address_id.equals("")) {
                for (int i = 0; i < addressList.size(); i++) {
                    if (addressList.get(i).addressId.trim().equals(address_id)) {
//                        storeAddress(addressList, i);
                        storeAddress(addressList.get(i));
                        return;
                    }
                }
            }
        }
    }
//    public void storeAddress(List<Address> addressList,int position) {
//        login_editor.putBoolean(Constants.isAddressAdded,true);
//        login_editor.putString(Constants.addressId, addressList.get(position).addressId);
//        login_editor.putString(Constants.addressType, addressList.get(position).addressType);
//        login_editor.putString(Constants.fullName, addressList.get(position).fullName);
//        login_editor.putString(Constants.mobileNumber, addressList.get(position).mobileNumber);
//        login_editor.putString(Constants.pinCode, addressList.get(position).pinCode);
//        login_editor.putString(Constants.houseNo, addressList.get(position).houseNo);
//        login_editor.putString(Constants.street, addressList.get(position).street);
//        login_editor.putString(Constants.landmark, addressList.get(position).landmark);
//        login_editor.putString(Constants.town,  addressList.get(position).town);
//        login_editor.putString(Constants.state, addressList.get(position).state);
//        login_editor.putString(Constants.longitude, addressList.get(position).longitude);
//        login_editor.putString(Constants.latitude, addressList.get(position).latitude);
//        login_editor.commit();
//    }


    public void storeAddress(AddressResponse.Address address) {
        login_editor.putBoolean(Constants.isAddressAdded,true);
        login_editor.putString(Constants.addressId, address.addressId);
        login_editor.putString(Constants.addressType, address.addressType);
        login_editor.putString(Constants.fullName, address.fullName);
        login_editor.putString(Constants.mobileNumber, address.mobileNumber);
        login_editor.putString(Constants.pinCode, address.pinCode);
        login_editor.putString(Constants.houseNo, address.houseNo);
        login_editor.putString(Constants.street, address.street);
        login_editor.putString(Constants.landmark, address.landmark);
        login_editor.putString(Constants.town,  address.town);
        login_editor.putString(Constants.state, address.state);
        login_editor.putString(Constants.longitude, address.longitude);
        login_editor.putString(Constants.latitude, address.latitude);
        login_editor.commit();
    }

   public String getAddress(){
       String address = "";
//               =pref_login.getString(Constants.fullName,"")+", "+
//               pref_login.getString(Constants.houseNo,"")+"("+ pref_login.getString(Constants.addressType,"")+"), "+
//               pref_login.getString(Constants.landmark,"")+", "+
//               pref_login.getString(Constants.street,"")+", "+
//               pref_login.getString(Constants.state,"");


       if (pref_login!=null){
           if (!pref_login.getString(Constants.fullName,"").equals("")){
               address+=pref_login.getString(Constants.fullName,"");
           }
           if (!pref_login.getString(Constants.houseNo,"").equals("")){
               address+= "\n"+ pref_login.getString(Constants.houseNo,"")+" ("+pref_login.getString(Constants.addressType,"")+")";
           }
           if (!pref_login.getString(Constants.landmark,"").equals("")){
               address+= ", "+pref_login.getString(Constants.landmark,"");
           }
           if (!pref_login.getString(Constants.street,"").equals("")){
               address+= ", "+ pref_login.getString(Constants.street,"");
           }
           if (!pref_login.getString(Constants.pinCode,"").equals("")){
               address+= ", "+pref_login.getString(Constants.pinCode,"");
           }
           if (!pref_login.getString(Constants.town,"").equals("")){
               address+= ", "+pref_login.getString(Constants.town,"");
           }
           if (!pref_login.getString(Constants.state,"").equals("")){
               address+= ", "+pref_login.getString(Constants.state,"");
           }
       }
       return address;
    }

    public void clearSharedPrefernce(){

        login_editor.putBoolean(Constants.isAddressAdded,false);
        login_editor.remove(Constants.addressId);
        login_editor.remove(Constants.addressType);
        login_editor.remove(Constants.fullName);
        login_editor.remove(Constants.mobileNumber);
        login_editor.remove(Constants.pinCode);
        login_editor.remove(Constants.houseNo);
        login_editor.remove(Constants.street);
        login_editor.remove(Constants.landmark);
        login_editor.remove(Constants.town);
        login_editor.remove(Constants.state);
        login_editor.remove(Constants.longitude);
        login_editor.remove(Constants.latitude);
        login_editor.commit();

    }

    public void storeAddress_id(String address_id) {
        login_editor.putString(Constants.addressId,address_id);
//        login_editor.putString(Constants.addressResponse,address_id);
        login_editor.commit();
    }

    public String getAddress(AddressResponse.Address address, boolean isWantName){
        String addressStr = "";
        if (address!=null){
            if (isWantName && address.fullName!=null && !address.fullName.trim().equals("")){
                addressStr+=address.fullName;
            }
            if (address.houseNo!=null && !address.houseNo.trim().equals("")){
                if (isWantName){
                    addressStr+= "\n";
                }
                addressStr += address.houseNo+" ("+address.addressType+")";
            }
            if (address.landmark!=null && !address.landmark.trim().equals("")){
                addressStr += "\n"+address.landmark;
            }
            if (address.street!=null && !address.street.trim().equals("")){
                addressStr += "\n"+address.street;
            }
            if (address.pinCode!=null && !address.pinCode.trim().equals("")){
                addressStr += "\n"+address.pinCode;
            }
            if (address.town!=null && !address.town.trim().equals("")){
                addressStr += "\n"+address.town;
            }
            if (address.state!=null && !address.state.trim().equals("")){
                addressStr += "\n"+address.state;
            }
        }
        return addressStr;
    }
}

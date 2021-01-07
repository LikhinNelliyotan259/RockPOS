package com.rockchipme.app.helpers;

/**
 * Created by Alisons on 9/18/2017.
 */
public class Validations {

    // Email validation
    public boolean isValidEmail(String email) {
        //TODO: Replace this with your own logic
        String emaiPatttern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.matches(emaiPatttern)){
            if (!email.contains("\t")){
                return true;
            }
            return false;
        }
        else {
            return false;
        }
//        return email.contains("@");
    }



    // phone number validation
    public boolean isValidPhone(String phoneNumber) {
//        String phonePattern = "[0-9]";//leng""th = 6 to 10
        if (phoneNumber.length() == Constants.phoneNo_length) {
//            if (phoneNumber.matches(phonePattern)) {
                return true;
//            }
        }
        return false;
    }


    // password validation
    public boolean isValidPassword(String password) {
        return password.length() > 4;
    }

    // password confirmations
    public boolean passwordMatch(String pass, String confirmPass) {
        return pass.equals(confirmPass);
    }




    public boolean isValidName(String name) {
        //TODO: Replace this with your own logic
        String emaiPatttern = "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}+";

        if (name.matches(emaiPatttern)){
//            if (!name.contains("\t")){
                return true;
//            }
//            return false;
        }
        else {
            return false;
        }
//        return email.contains("@");
    }





}

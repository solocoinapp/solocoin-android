package com.shimadove.coronago.app;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private SharedPreferences pref;
    private static SharedPref instance  = null;
    private SharedPreferences.Editor editor;

    private SharedPref(Context context) {
        pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SharedPref getInstance(Context context){
        if (null == instance) instance = new SharedPref(context);
        return instance;
    }

    private static final String TIMEOUT = "timeout";
    public void setTimeout(long timeout){
        editor.putLong(TIMEOUT, timeout);
        editor.commit();
    }
    public int getTimeout(){
        return pref.getInt(TIMEOUT, 0);
    }

    private static final String COUNTRY_CODE = "country_code";
    public void setCountryCode(String code){
        editor.putString(COUNTRY_CODE, code);
        editor.commit();
    }
    public String getCountryCode(){
        return pref.getString(COUNTRY_CODE, "");
    }

    private static final String PHONE_NUMBER = "phone_number";
    public void setPhoneNumber(String number){
        editor.putString(PHONE_NUMBER, number);
        editor.commit();
    }
    public String getPhoneNumber(){
        return pref.getString(PHONE_NUMBER, "");
    }


    private static final String wallet_balance = "wallet_balance";

    public float getWallet_balance(){
        return pref.getFloat(wallet_balance,0);
    }

    public void setWallet_balance(float balance){
        editor.putFloat(wallet_balance,balance);
        editor.commit();
    }

    private static final String HTTP_RESPONSE = "http_response"; //To check if a new or existing user
    public void setHttpResponse(int number){
        editor.putInt(HTTP_RESPONSE,number);
        editor.commit();
    }
    public int getHttpResponse(){
        return pref.getInt(HTTP_RESPONSE, 404);
    }

    /**
     * Method call when user log-out of application
     */
    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}

package app.solocoin.solocoin.app;

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

    private static final String isHomeLocationSet = "isHomeLocationSet";

    public void setIsHomeLocationSet(boolean isHomeLocationSetOrNot){
        editor.putBoolean(isHomeLocationSet,isHomeLocationSetOrNot);
        editor.commit();
    }

    public boolean getIsHomeLocationSet(){
        return pref.getBoolean(isHomeLocationSet,false);
    }

    private static final String USERNAME= "username";
    public void setUsername(String username){
        editor.putString(USERNAME,username);
        editor.commit();
    }
    public String getUsername(){return pref.getString(USERNAME,"");}

    private static final String AUTHTOKEN= "authtoken";
    public void setAuthtoken(String authtoken){
        editor.putString(AUTHTOKEN,authtoken);
        editor.commit();
    }
    public String getAuthtoken(){
        return pref.getString(AUTHTOKEN,null);
    }

    private static final String TIME= "time";
    public void setTime(String time){
        editor.putString(TIME,time);
        editor.commit();
    }

    public String getTime(){return pref.getString(TIME,"00:00:00");}

    private static final String SESSIONTYPE= "sessiontype";
    public void setSessiontype(String type){
        editor.putString(SESSIONTYPE,type);
        editor.commit();
    }

    public String getSessiontype(){return pref.getString(SESSIONTYPE,"");}

    //For Firebase Token generated
    private static final String ID_TOKEN="id_token";
    public void setIdToken(String idtoken){
        editor.putString(ID_TOKEN,idtoken);
        editor.commit();
    }
    public String getIdToken(){
        return pref.getString(ID_TOKEN,null);
    }

    //Since email and gender not required for profile right now.
//    private static final String EMAIL="email";
//    public void setEmail(String id){
//        editor.putString(EMAIL,id);
//        editor.commit();
//    }
//
//    public String getEmail(){ return pref.getString(EMAIL,"");}
//
//    private static final String GENDER="email";
//    public void setGender(String id){
//        editor.putString(GENDER,id);
//        editor.commit();
//    }
//
//    public String getGender(){ return pref.getString(GENDER,"");}

     /**
     * Method call when user log-out of application
     */
    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}

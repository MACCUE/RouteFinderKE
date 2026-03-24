package com.example.routefinderke;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Interface for REAL M-Pesa API Integration 💰
 */
public interface MpesaService {

    // 1. Get Access Token
    @POST("oauth/v1/generate?grant_type=client_credentials")
    Call<ResponseBody> getAccessToken(@Header("Authorization") String auth);

    // 2. Initiate STK Push
    @POST("mpesa/stkpush/v1/processrequest")
    Call<ResponseBody> sendStkPush(@Header("Authorization") String auth, @Body StkPushRequest request);
}

/**
 * Request Model for M-Pesa STK Push
 */
class StkPushRequest {
    public String BusinessShortCode;
    public String Password;
    public String Timestamp;
    public String TransactionType = "CustomerPayBillOnline";
    public String Amount;
    public String PartyA;
    public String PartyB;
    public String PhoneNumber;
    public String CallBackURL;
    public String AccountReference;
    public String TransactionDesc;

    public StkPushRequest(String amount, String phone, String till, String pass, String time, String callback, String desc) {
        this.BusinessShortCode = till;
        this.Password = pass;
        this.Timestamp = time;
        this.Amount = amount;
        this.PartyA = phone;
        this.PartyB = till;
        this.PhoneNumber = phone;
        this.CallBackURL = callback;
        this.AccountReference = "RouteFinderKE";
        this.TransactionDesc = desc;
    }
}

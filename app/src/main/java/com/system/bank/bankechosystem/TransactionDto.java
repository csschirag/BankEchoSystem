package com.system.bank.bankechosystem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guneet on 5/21/17.
 */

public class TransactionDto {
    String date;
    long time;
    String amount;
    String phone_number;
    String merchant_name;
    MerchantCategory merchant_category;
    int id;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public MerchantCategory getMerchant_category() {
        return merchant_category;
    }

    public void setMerchant_category(MerchantCategory merchant_category) {
        this.merchant_category = merchant_category;
    }

    public JSONObject toJsonString(){
        String str = "{\"amount\":"+ amount+",\"id\":"+ id +",\"merchant_category\":\""+  merchant_category+"\"," +
                "\"merchant_name\":\""+merchant_category+"\",\"phone_number\":"+ phone_number+"," +
                "\"timestamp\":"+time+"}";
        try {
            return new JSONObject(str);

        }catch (JSONException e){
            e.printStackTrace();
        }

        return null;
    }
}

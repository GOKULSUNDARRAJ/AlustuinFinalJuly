package com.gokulsundar4545.connectwithpeople;

import com.gokulsundar4545.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
                    "Content-Type:application/json",
                    "Authorization:key=AAAANwzbUKU:APA91bGg8t4booWobRWCdJJKGNSPAIe3NOMKCqYZnGih1ttv58k2zHavgUxoQOcHvP_ykKYLcydO0uWTWfeFNV2kjJihT8fs_3gutUy6brISW2bDm72mWCUJXKFBKfzfcqk2F6_SZLbt"
            })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

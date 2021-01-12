package com.example.week2;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import static com.example.week2.LogIn.login_s;

import org.json.JSONObject;

public class LoginCallback implements FacebookCallback<LoginResult> {

    // 로그인 성공 시 호출 됩니다. Access Token 발급 성공.
    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.e("Callback :: ", "onSuccess");
        requestMe(loginResult.getAccessToken());
        login_s = true;
    }
    // 로그인 창을 닫을 경우, 호출됩니다.
    @Override
    public void onCancel() {
        Log.e("Callback :: ", "onCancel");
        login_s = false;
    }
    // 로그인 실패 시에 호출됩니다.

    @Override
    public void onError(FacebookException error) {
        Log.e("Callback :: ", "onError : " + error.getMessage());
        login_s = false;
    }

    // 사용자 정보 요청
    public void requestMe(AccessToken token) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("result",object.toString());
                        try {
                            if(object.has("name")){
                                LogIn.user_name = object.getString("name");
                            }
                            if(object.has("email")){
                                LogIn.user_email = object.getString("email");
                                Log.e("facebook email", object.getString("email"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }
}
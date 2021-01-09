package com.example.week2;

import android.content.ContentValues;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.week2.ListViewAdapter.listViewItemList;
import static com.example.week2.MainActivity.testlist;
import static com.example.week2.Page1Fragment.adapter;
import static com.example.week2.Page1Fragment.listViewItem;
import static com.example.week2.Page1Fragment.listview;

public class Page1NetworkTask extends AsyncTask<Void, Void, String> {
    private String url;
    private ContentValues values;
    private String method;


    public Page1NetworkTask(String url, ContentValues values, String method) {
        this.url = url;
        this.values = values;
        this.method = method;
    }
    @Override
    protected String doInBackground(Void... params) {
        String result; // 요청 결과를 저장할 변수.
        RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
        if(method == "GET"){
            result = requestHttpURLConnection.request_get(url, values); // 해당 URL로 부터 결과물을 얻어온다.
        }
        else{
            result = requestHttpURLConnection.request_post(url, values); // 해당 URL로 POST 보내기.
        }
        return result;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //System.out.println(s);
        if (method == "GET"){
            try{
                //Json parsing
                JSONArray jsonArray = new JSONArray(s);
                adapter.clear();
                testlist = new ArrayList<User>();
                for(int i = 0; i< jsonArray.length();i++){
                    JSONObject userObject = jsonArray.getJSONObject(i);

                    User list = new User();
                    list.setName(userObject.getString("name"));
                    list.setEmail(userObject.getString("email"));
                    list.setPhone(userObject.getString("phone"));

                    if(LogIn.user_name.equals(list.getName())) {
                        testlist.add(0, list);
                        adapter.addFront(null, list.getName(), list.getPhone(), null, null);
                    }
                    else {
                        testlist.add(list);
                        adapter.addItem(null, list.getName(), list.getPhone(), null, null);
                    }

                }
                adapter.notifyDataSetChanged();
                System.out.println(listViewItemList);
                System.out.println("mu!");
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else if(method == "POST"){
            if(s == "fail"){
                //Log.e("fail","fail....");
            }
            else{
                //Log.e("success",s);
            }
        }
        listview.setAdapter(adapter);
    }
}
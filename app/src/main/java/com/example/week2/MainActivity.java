package com.example.week2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.week2.Page1Fragment.adapter;
import static com.example.week2.Page1Fragment.listview;
import static com.example.week2.ListViewAdapter.listViewItemList;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static String test= "";
    public static ArrayList<ListViewItem> testlist = new ArrayList<>();
    public static ArrayList<ContactItem> contactItems = new ArrayList<ContactItem>();
    private static boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContactList();

        new JSONTask().execute("http://192.249.18.241:27018/post");
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로 가기 버튼을 누를 때 표시
    private Toast toast;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 1초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 1초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 1000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "Press the back key again to exit the app", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 1초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 1초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 1000) {
            toast.cancel();
            finishAffinity();
        }
    }

    public static class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();

                for(int i=0; i<contactItems.size(); i++) {
                    jsonObject.accumulate("user_id", contactItems.get(i).getUser_name());
                    jsonObject.accumulate("name", contactItems.get(i).getPhNumberChanged());
                }

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버에 데이터 보내기
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로부터 받은 값을 리턴해줌

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            test = result;

            String name = "";
            String number = "";
            boolean flag = true;
            for(int i=0; i<test.length(); i++){
                if(flag && test.charAt(i) !='/') name = name + test.charAt(i);
                else if(flag) {
                    flag = false;
                }
                else if(test.charAt(i) != '*') number = number + test.charAt(i);
                else{
                    ListViewItem lvi = new ListViewItem();
                    lvi.setTitle(name);
                    lvi.setDesc(number);
                    name = "";
                    number= "";
                    flag = true;
                    testlist.add(lvi);
                    adapter.addItem(null ,lvi.getTitle(), lvi.getDesc(), null, null);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public ArrayList<ContactItem> getContactList() {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Uri uri1 = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        Uri uri2 = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        boolean flag_cursor1 = true;
        boolean flag_cursor2 = true;

        Cursor cursor = null;
        Cursor cursor1= null;
        Cursor cursor2 = null;
        ContentResolver contentResolver = getContentResolver();
        String sortorder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        try{
            cursor = contentResolver.query(uri, null,null,null, sortorder);
            cursor1 = contentResolver.query(uri1, null,null,null, sortorder);
            cursor2 = contentResolver.query(uri2, null,null,null, sortorder);
        } catch(Exception ex) {
            Log.e("Error on contact", ex.getMessage());
        }

        if(cursor.moveToFirst()) {
            cursor1.moveToFirst();
            cursor2.moveToFirst();

            do {
                ContactItem contactItem = new ContactItem();
/*
                String[] temp = cursor.getColumnNames();
                for(int i=0; i<temp.length; i++){
                    System.out.println(temp[i]);
                }
*/
                contactItem.setUser_name(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                ));
                contactItem.setUser_phNumber(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                ));
                contactItem.setPhoto_id(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)
                ));

                if(cursor1.moveToNext() && flag_cursor1){
                    cursor1.moveToPrevious();
                    contactItem.setMail(cursor1.getString(
                            cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
                    ));
                }
                else if(flag_cursor1){
                    cursor1.moveToPrevious();
                    contactItem.setMail(cursor1.getString(
                            cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
                    ));
                    flag_cursor1 = false;
                }

                if(cursor2.moveToNext() && flag_cursor2){
                    cursor2.moveToPrevious();
                    contactItem.setAddress(cursor2.getString(
                            cursor2.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA)
                    ));
                }
                else if(flag_cursor2){
                    cursor2.moveToPrevious();
                    contactItem.setAddress(cursor2.getString(
                            cursor2.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA)
                    ));
                    flag_cursor2 = false;
                }

                /*
                contactItem.setPerson_id(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID)
                ));*/

                contactItems.add(contactItem);
                cursor1.moveToNext();
                cursor2.moveToNext();
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactItems;
    }
}
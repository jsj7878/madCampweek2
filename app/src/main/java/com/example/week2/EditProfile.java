package com.example.week2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.week2.ListViewAdapter.listViewItemList;
import static com.example.week2.LogIn.user_name;
import static com.example.week2.MainActivity.testlist;
import static com.example.week2.Page1Fragment.adapter;

public class EditProfile extends AppCompatActivity {

    MyGridAdapter adapter2 = null;
    ArrayList<GalleryImage> img2 = new ArrayList<>();
    GridView gv;
    JSONArray posting_list;
    ArrayList<String> str = new ArrayList<String>();
    ImageView myphoto;

    ApiService apiService;
    Uri picUri;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int IMAGE_RESULT = 200;
    private final static int ALL_PERMISSIONS_RESULT = 107;

    static Photo data;
    static AlertDialog.Builder builder;
    static AlertDialog dlg;
    static ImageView d_profile;
    static TextView d_names;
    static TextView d_tagged;
    static TextView d_date;
    static TextView d_desc;
    static ImageView d_imageView;
    static ImageView imageView;

    Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        User tmp = listViewItemList.get(0);

        myphoto = (ImageView) findViewById(R.id.myphoto);
        Glide.with(getBaseContext())
                .asBitmap()
                .load(tmp.getUser_profile_photo())
                .circleCrop()
                .into(myphoto);

        Button btn = (Button) findViewById(R.id.profile_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
                //Toast.makeText(getBaseContext(), "Edit Profile Image", Toast.LENGTH_SHORT).show();
            }
        });

        TextView name = (TextView) findViewById(R.id.myname);
        name.setText(tmp.getName());
        EditText profile = (EditText) findViewById(R.id.editprofile);
        profile.setText(tmp.getUser_profile());
        EditText phone = (EditText) findViewById(R.id.editnumber);
        phone.setText(tmp.getPhone());
        EditText mail = (EditText) findViewById(R.id.editmail);
        mail.setText(tmp.getEmail());
        gv = (GridView) this.findViewById(R.id.myposting);

        img2.clear();
        posting_list = listViewItemList.get(0).posting;

        str.clear();
        for(int i=0; i<posting_list.length(); i++) {
            try {
                str.add(posting_list.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter2 = new MyGridAdapter(
                getApplicationContext(),
                R.layout.row_in_contact,       // GridView 항목의 레이아웃 row.xml
                str);    // 데이터
        adapter2.notifyDataSetChanged();

        gv.setAdapter(adapter2);  // 커스텀 아답타를 GridView 에 적용
    }

    public class MyGridAdapter extends BaseAdapter {
        Context context;
        int layout;
        ArrayList<String> str;
        LayoutInflater inf;

        public MyGridAdapter(Context context, int layout, ArrayList<String> str) {
            this.context = context;
            this.layout = layout;
            this.str = str;
            inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return str.size();
        }

        public Object getItem(int position) {
            return str.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            imageView = new ImageView(context);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size); // or getSize(size)
            int width = size.x;

            width /= 4;
            width -= 20;
            Glide.with(gv)
                    .asBitmap()
                    .load("http://192.249.18.249:3000/" + str.get(position))
                    .centerCrop()
                    .into(imageView);

            imageView.setLayoutParams(new GridView.LayoutParams(width, width));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(7, 7, 7, 7);

            //imageView.setImageBitmap(img.get(position).getD());
            final int pos = position;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = new Photo();
                    forDialog2(context, str.get(position));
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    str.remove(pos);
                    adapter2.notifyDataSetChanged();
                    return true;
                }
            });
            return imageView;
        }

    }


    public void forDialog2(Context context, String path){
        builder = new AlertDialog.Builder(this);
//        dlg = new Dialog(context);
//        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dlg.setContentView(R.layout.recyclerview_item);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.recyclerview_item, null);
        builder.setView(view);

        d_profile = (ImageView) view.findViewById(R.id.myprofile);
        d_names = (TextView) view.findViewById(R.id.names);
        d_tagged = (TextView) view.findViewById(R.id.names2);
        d_date = (TextView) view.findViewById(R.id.posted_date);
        d_desc = (TextView) view.findViewById(R.id.textView2);
        d_imageView = (ImageView) view.findViewById(R.id.imageView);

        data = new Photo();
        String Url = "http://192.249.18.249:3000/getphoto/";
        NetworkTask networkTask = new NetworkTask(Url, null, "GET", path, context, view);
        networkTask.execute();

    }

    public static class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        private String method;
        private String path;
        private Context context;
        private View view;

        public NetworkTask(String url, ContentValues values, String method, String path, Context context, View view) {
            this.url = url;
            this.values = values;
            this.method = method;
            this.path = path;
            this.context = context;
            this.view = view;
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
            String getImageurl = "";
            //System.out.println(s);

            if (method == "GET"){
                ArrayList<Photo> tmp = new ArrayList<Photo>();
                try{
                    //Json parsing
                    JSONArray jsonArray = new JSONArray(s);
                    for(int i = 0; i< jsonArray.length();i++) {
                        JSONObject photoObject = jsonArray.getJSONObject(i);
                        Photo posting = new Photo();

                        posting.setExplain(photoObject.getString("explain"));

                        JSONArray userList = new JSONArray();
                        posting.setUserList(userList);

                        getImageurl = "http://192.249.18.249:3000/" + photoObject.getString("server_place");
                        posting.setServer_place(getImageurl);

                        String from = photoObject.getString("time");
                        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date time = transFormat.parse(from);
                        posting.setTime(time);

                        posting.setUserList(photoObject.getJSONArray("userList"));

                        if (posting.getServer_place().equals("http://192.249.18.249:3000/" + path)) {
                            data = posting;
                            break;
                        }
                    }

                    JSONArray ja = data.getUserList();
                    String namestr = "";
                    for (int i = 1; i < ja.length(); i++) {
                        try {
                            namestr = namestr + ja.getString(i);
                            if (i < ja.length() - 1) namestr = namestr + " ";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        String stmp = ja.getString(0);
                        d_names.setText(stmp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (namestr.length() > 0) d_tagged.setText(namestr);
                    else d_tagged.setText("None");

                    Date from2 = data.getTime();
                    SimpleDateFormat transFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String to = transFormat2.format(from2);
                    d_date.setText(to);

                    d_desc.setText(data.getExplain());
                    Glide.with(context).load(data.getServer_place())
                            .centerCrop()
                            .into(d_imageView);

                    String path = "";
                    for (int i = 0; i < testlist.size(); i++) {
                        try {
                            if (testlist.get(i).getName().equals(ja.getString(0))) {
                                path = testlist.get(i).getUser_profile_photo();
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Glide.with(context).load(path)
                            .circleCrop()
                            .into(d_profile);


                }catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
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
            dlg = builder.create();
            dlg.show();
        }

    }

    private void askPermissions() {
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        apiService = new Retrofit.Builder().baseUrl("http://192.249.18.249:3000/").client(client).build().create(ApiService.class);
    }

    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<Intent>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == IMAGE_RESULT) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    mBitmap = BitmapFactory.decodeFile(filePath);

                    Glide.with(getBaseContext())
                            .asBitmap()
                            .load(mBitmap)
                            .circleCrop()
                            .into(myphoto);
                }
            }
        }
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;
        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());
    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }
}

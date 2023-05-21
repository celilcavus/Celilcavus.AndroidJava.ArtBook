package celilcavus.javaandroid.celilcavusartbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.sql.Blob;
import java.sql.Statement;

import celilcavus.javaandroid.celilcavusartbook.databinding.ActivityArtBinding;

public class ArtActivity extends AppCompatActivity {
    private ActivityArtBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    private Bitmap selectedImage;

    private SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        RegisterLauncher();

        try {
            database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (" +
                    "id INTEGER PRIMARY KEY," +
                    "name VARCHAR(100) ," +
                    "artisName VARCHAR(100)," +
                    "year VARCHAR(10)," +
                    "image BLOB)");
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Intent intent = getIntent();
        String info = intent.getStringExtra("key");
        if(info.matches("new"))
        {
            binding.txtArtName.setText("");
            binding.txtDescription.setText("");
            binding.txtYearText.setText("");
            binding.imageView.setImageResource(0);
            binding.btnKaydet.setVisibility(View.VISIBLE);
        }
        else{
            int Artid = intent.getIntExtra("value",1);
            binding.btnKaydet.setVisibility(View.INVISIBLE);

            try{
                Cursor cursor  = database.rawQuery("SELECT * FROM arts Where id = " + Artid,null);

                int name = cursor.getColumnIndex("name");
                int artisName = cursor.getColumnIndex("artisName");
                int year = cursor.getColumnIndex("year");
                int image = cursor.getColumnIndex("image");

                while(cursor.moveToNext())
                {
                    binding.txtArtName.setText(cursor.getString(name));
                    binding.txtDescription.setText(cursor.getString(artisName));
                    binding.txtYearText.setText(cursor.getString(year));

                    byte[] bytes = cursor.getBlob(image);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
                }

                Toast.makeText(this, "id " + Artid, Toast.LENGTH_SHORT).show();
                cursor.close();
            }catch (Exception ex)
            {
                System.out.println("message ============== " + ex.getMessage());
                ex.printStackTrace();
            }
        }

    }
    public void onClickSave(View view) {
        String name = binding.txtArtName.getText().toString();
        String artisName = binding.txtDescription.getText().toString();
        String year = binding.txtYearText.getText().toString();
        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] array = outputStream.toByteArray();

        String sql = "INSERT INTO arts (name,artisName,year,image) values (?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1,name);
        statement.bindString(2,artisName);
        statement.bindString(3,year);
        statement.bindBlob(4,array);
        statement.execute();


        Intent intent = new Intent(ArtActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }
    public Bitmap makeSmallerImage(Bitmap image,int maxsize){
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = ((float)width / (float)height);
        Bitmap img = null;
        if (bitmapRatio > 1)
        {
            //
            width = maxsize;
            height = (int)(width / bitmapRatio);
            img = image.createScaledBitmap(image,width,height,true);
        }
        else {
            //porte
            height = maxsize;
            width = (int)(height * bitmapRatio);
            img = image.createScaledBitmap(image,width,height,true);
        }

        return img;
    }
    public void onClickSelectedImage(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //Android 33+ -> READ_MEDIA_IMAGES
            if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission nedded for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //REQUEST PERSMMİSON
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
            else{
                //Go To Gallery
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
        else {
            if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission nedded for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //REQUEST PERSMMİSON
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }
                else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
            else{
                //Go To Gallery
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
    }

    private void RegisterLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK)
                {
                    Intent resultGetData = result.getData();
                    if(resultGetData != null){
                        try {
                            Uri uriImageData = resultGetData.getData();
                            //binding.imageView.setImageURI(uriImageData);
                            if (Build.VERSION.SDK_INT > 28)
                            {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),uriImageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                            else{
                                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(),uriImageData);
                            }

                        }catch (Exception ex)
                        {
                            ex.printStackTrace();
                            System.out.println(ex.getMessage());
                        }
                    }
                    else{return;}
                }
                else {return;}
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result)
                {
                    //permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
                else {
                    //permission denied
                    Toast.makeText(ArtActivity.this,"Permission Nedded!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
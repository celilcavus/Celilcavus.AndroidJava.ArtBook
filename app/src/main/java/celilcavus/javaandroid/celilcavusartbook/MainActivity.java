package celilcavus.javaandroid.celilcavusartbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import celilcavus.javaandroid.celilcavusartbook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SQLiteDatabase database;

    ArrayList<Art> artArrayList;
    private ArtAadapter artArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        artArrayList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artArrayAdapter = new ArtAadapter(artArrayList);
        binding.recyclerView.setAdapter(artArrayAdapter);

        GetData();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.art_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_art){
            Intent intent = new Intent(MainActivity.this, ArtActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickArtActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ArtActivity.class);
        intent.putExtra("key","new");
        startActivity(intent);
    }

    private void GetData(){
        try {
            database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT * FROM arts",null);
            int idix = cursor.getColumnIndex("id");
            int nameix = cursor.getColumnIndex("name");

            while(cursor.moveToNext())
            {
                int id = cursor.getInt(idix);
                String name = cursor.getString(nameix);

                Art art = new Art(id,name);
                artArrayList.add(art);
            }
            artArrayAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
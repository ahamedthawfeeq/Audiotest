package com.thawfeeqstudios.sahamedthawfeeq.audiotest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button bb;
    private Button cc;
    ImageView album_art;
    TextView album, artist, genre;
    VideoView vv;
    MediaMetadataRetriever metaRetriver;
    String rsp;
    byte[] art;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bb = (Button)findViewById(R.id.bb);
        cc = (Button)findViewById(R.id.cc);
        vv = (VideoView)findViewById(R.id.vv);
        album_art = (ImageView) findViewById(R.id.album_art);
        album = (TextView) findViewById(R.id.Album);
        artist = (TextView) findViewById(R.id.artist_name);
        genre = (TextView) findViewById(R.id.genre);
        // Ablum_art reterival code //
        final String pp=System.getenv("SECONDARY_STORAGE");
        File fileList[] = new File("/storage/").listFiles();
        for (File file :fileList ){
            if(!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())&& file.isDirectory() && file.canRead()){
                rsp = file.getAbsolutePath();
            }
        }
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 23){
                    new MaterialFilePicker()
                            .withActivity(MainActivity.this)
                            .withPath(rsp)
                            .withRootPath(rsp)
                            .withRequestCode(1000)
                            .withHiddenFiles(true) // Show hidden files and folders
                            .start();
                }
                else {
                    new MaterialFilePicker()
                            .withActivity(MainActivity.this)
                            .withPath(pp)
                            .withRootPath(pp)
                            .withRequestCode(1000)
                            .withHiddenFiles(true) // Show hidden files and folders
                            .start();
                }

            }
        });
        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(MainActivity.this)
                        .withRequestCode(1000)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file
            metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(filePath);
            if (filePath.toString().endsWith(".mp4") || filePath.toString().endsWith(".3gp") || filePath.toString().endsWith(".webm") || filePath.toString().endsWith(".mkv")) {
                Toast.makeText(MainActivity.this, "sorry, please choose audio formats supported by android..", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    art = metaRetriver.getEmbeddedPicture();
                    Bitmap songImage = BitmapFactory
                            .decodeByteArray(art, 0, art.length);
                    album_art.setImageBitmap(songImage);
                    album.setText(metaRetriver
                            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                    artist.setText(metaRetriver
                            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                    genre.setText(metaRetriver
                            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
                    vv.setVideoURI(Uri.parse(filePath));
                    vv.setMediaController(new MediaController(MainActivity.this));
                    vv.start();
                } catch (Exception e) {
                    album_art.setImageResource(R.drawable.ic_music_player);
                    album.setText("Unknown Album");
                    artist.setText("Unknown Artist");
                    genre.setText("Unknown Genre");
                    vv.setVideoURI(Uri.parse(filePath));
                    vv.setMediaController(new MediaController(MainActivity.this));
                    vv.start();
                }

            }

        }
    }
}

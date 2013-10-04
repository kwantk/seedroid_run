package com.example.my.first.app;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayAlbumActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_album);
		
		// Get the message from the intent
        Intent intent = getIntent();
        ArrayList<Album> albumList = intent.getParcelableArrayListExtra(MainActivity.EXTRA_MESSAGE);
        Album data[] = new Album[albumList.size()];
        albumList.toArray(data);
        
        ListView lv = (ListView) findViewById(R.id.lvAlbum);
        AlbumAdapter adapter = new AlbumAdapter(this, R.layout.activity_albumlist_item, data);
        
        lv.setAdapter(adapter);
                
        lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Album selectedItem = (Album) parent.getAdapter().getItem(position);
				
				Toast toast = Toast.makeText(getApplicationContext(), 
						selectedItem.Name + " has " + selectedItem.NumberOfPictures + " picture(s).", 						
						Toast.LENGTH_SHORT);
				toast.show();
				
			}
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_album, menu);
		return true;
	}

}



/**
 * @name SRemember
 * @date 16/07/14
 * @description Secure notepad for Android systems Based on: http://developer.android.com/training/notepad/
 * @filename SRemember.java
 */
package es.rffsystems;

import es.rffsystems.sremember.R;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

// Al tenir una llista a l'acivity principal ha d'extendre de ListActivity
public class SRemember extends ListActivity {
	
	// Constants per a la db
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    // Constants usades al menu
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    // a partir d'aquesta propietat usarem la db
    private NotesDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        
        // creem una instancia i obrir una connexio a la db
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        
        // anem a buscar les dades i retornem una llista de titols que 
        // sera el que mostrem
        fillData();
        
        // registrem la llista per a poder usar el menu contextual i 
        // poder esborrar
        registerForContextMenu(getListView());
    }

    /*
     * Anem a buscar totes les notes, i filtrem per titol. 
     * Retornem nomes aquestes i les ensenyem a la llista usant
     * el notes_row 
     */
    private void fillData() {
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        setListAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
        
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	/*
    	 * Un cop seleccionem un element del menu el seleccionem 
    	 * amb un switch
    	 */

    	int i = item.getItemId();
    	
        switch(i) {
       
            case  R.id.add:
            {
                createNote();
                return true;
            }
            default: 
            	Toast.makeText(getApplicationContext(), "Not any option", 
            			   Toast.LENGTH_LONG).show();

        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete); // Afegim el delete al menu de contexte de la llista
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	/*
    	 * Quan seleccionem un element del menu contextual comprobem quin es el 
    	 * element seleccionat.
    	 */
        switch(item.getItemId()) {
            case DELETE_ID:
            	/*
            	 * Si es delete, obtenim quin es el element que volem esborrar, 
            	 * l'esborrem i tornem a pintar les dades
            	 */
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /*
     * Per crear una activitat creem un intent i cridem al start Activity create 
     * amb un retorn. 
     */
    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	/*
    	 * Quan clickem un element de la llista creem un nou intent i fem un start 
    	 * amb un return del activity edit. 
    	 * Tb afegim un element extra. el id
    	 */
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	/* 
    	 * Quan retornem d'alguna activitat sempre repintem la llista amb un fillData
    	 */
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
package com.code.kawakuti.phonepharmacy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 0;
    private static final int SELECT_FILE = 1;
    private static final String TAG = "PHARMACY";
    private TextView med_name, med_description;
    private DatePicker expiration_date_picker;
    private String img_source;
    private Button bt_chooseFile, bt_save, bt_cancel;
    private Calendar mCalendar;
    private int day, month, mYear;
    private ListView medicineListView;
    private Date expiration_date;
    private MedAdapter medAdapter;


    private List<Med> listMeds = new ArrayList<Med>();
    private DataBaseHandler db;
    private ImageLoader loaderImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DataBaseHandler(getApplicationContext());
        medicineListView = (ListView) findViewById(R.id.list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCalendar();
        loaderImg = new ImageLoader();
        listMeds = db.getAllMedsList();
        medAdapter = new MedAdapter(this, listMeds, loaderImg);
        medicineListView.setAdapter(medAdapter);
        registerForContextMenu(medicineListView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedDialog().show();

            }
        });
    }

    public void initCalendar() {

        mCalendar = new GregorianCalendar();
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        month = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);

    }

    public Dialog addMedDialog() {

        /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);*/
        final Dialog builder = new Dialog(MainActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        builder.setTitle(R.string.dialog_add);
        builder.setContentView(inflater.inflate(R.layout.dialog_addmed, null));

        med_name = (TextView) builder.findViewById(R.id.name);
        med_description = (TextView) builder.findViewById(R.id.description);
        expiration_date_picker = (DatePicker) builder.findViewById(R.id.expiration_date);
        bt_chooseFile = (Button) builder.findViewById(R.id.imgSrc);
        bt_save = (Button) builder.findViewById(R.id.save);
        bt_cancel = (Button) builder.findViewById(R.id.cancel);


        expiration_date_picker.init(mYear, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(year, monthOfYear + 1, dayOfMonth);
                Log.d(TAG, mCalendar.get(Calendar.DAY_OF_MONTH) + " "
                        + mCalendar.get(Calendar.MONTH) + " " + mCalendar.get(Calendar.YEAR));
            }
        });
        bt_chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(getApplicationContext(), "Let's  Save ", Toast.LENGTH_SHORT).show();
                if (checkFields(med_name)) {

                    Med tmp = new Med();
                    tmp.setName(med_name.getText().toString());
                    tmp.setDescription(med_description.getText().toString());
                    tmp.setExpireDate(mCalendar.getTime());
                    tmp.setSrcImage(img_source);

                    if (db.addMed(tmp) > 0) {
                        builder.dismiss();
                        Toast.makeText(getApplicationContext(), "Inserted with Sucess", Toast.LENGTH_SHORT).show();
                        listMeds(db.getAllMedsList());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "CHECK FIELDS", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cancel Dialog ", Toast.LENGTH_SHORT).show();
                builder.cancel();
            }
        });


        return builder;
    }

    public boolean checkFields(TextView textView) {
        return textView.getText().length() != 0;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        img_source = destination.getAbsolutePath();
        changeButtonText(img_source, bt_chooseFile);
        // ivImage.setImageBitmap(thumbnail);
    }


    public void changeButtonText(String path, Button choose) {

        if (path.length() != 0) {
            String[] path_parts = path.split("/");
            choose.setText(path_parts[path_parts.length - 1]);
        }

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);
        img_source = selectedImagePath;
        changeButtonText(img_source, bt_chooseFile);
        //  to be used after;


    }

    public void listMeds(List<Med> medicine) {
        for (Med med : medicine) {
            Log.d(TAG, med.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(listMeds.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu);
       /* String menuItemName = menuItems[menuItemIndex];
        String listItemName = listMeds.get(info.position).getName();

        TextView text = (TextView)findViewById(R.id.footer);
        text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));*/
        return true;
    }
}

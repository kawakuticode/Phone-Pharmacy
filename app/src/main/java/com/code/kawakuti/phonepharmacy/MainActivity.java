package com.code.kawakuti.phonepharmacy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.view.MenuInflater;
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
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 0;
    private static final int SELECT_FILE = 1;
    private static final String TAG = "PHARMACY";
    private TextView med_name, med_description, edit_name, edit_description;
    private DatePicker expiration_date_picker, edit_expiration_date_picker;
    private String img_source;
    private Button bt_chooseFile, bt_save, bt_cancel,
            edit_bt_cancel, edit_bt_update;
    private Calendar mCalendar;
    private int day, month, mYear;
    private ListView medicineListView;
    private MedAdapter medAdapter;
    private List<Med> listMeds = new ArrayList<Med>();
    private DataBaseHandler db;
    private ImageLoader loaderImg;
    private String options[] = new String[]{"Update", "Delete", "Cancel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DataBaseHandler(getApplicationContext());
        medicineListView = (ListView) findViewById(R.id.list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //initCalendar();
        loaderImg = new ImageLoader();

        listMeds = db.getAllMedsList();
        medAdapter = new MedAdapter(this, listMeds, loaderImg);
        medicineListView.setAdapter(medAdapter);
        Log.d("list size begin", listMeds.size() + "");
        registerForContextMenu(medicineListView);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        medicineListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Med tmpMed = (Med) parent.getItemAtPosition(position);

                builder.setTitle(tmpMed.getName());

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (options[which]) {
                            case "Update":
                                Log.d(TAG, "UPDATE _----xxx-> " + tmpMed.toString());
                                editMed(tmpMed).show();

                                break;
                            case "Delete":
                                deleteMed(tmpMed);
                                Log.d(TAG, "DELETE  ----//> " + tmpMed.toString());
                                break;
                            case "Cancel":
                                Log.d(TAG, "CANCEL ----+> " + tmpMed.toString());
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }

        });
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


    public void initCalendarEdit(Med med) {
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(med.getExpireDate());
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        month = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);

    }

    public Dialog addMedDialog() {

        /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);*/
        final Dialog builder = new Dialog(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle(R.string.dialog_add);
        builder.setContentView(inflater.inflate(R.layout.dialog_addmed, null));

        med_name = (TextView) builder.findViewById(R.id.name);
        med_description = (TextView) builder.findViewById(R.id.description);
        expiration_date_picker = (DatePicker) builder.findViewById(R.id.expiration_date);
        bt_chooseFile = (Button) builder.findViewById(R.id.imgSrc);
        bt_save = (Button) builder.findViewById(R.id.save);
        bt_cancel = (Button) builder.findViewById(R.id.cancel);

        initCalendar();
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

                if (checkFields(med_name)) {

                    Med tmp = new Med();
                    tmp.setName(med_name.getText().toString());
                    tmp.setDescription(med_description.getText().toString());
                    tmp.setExpireDate(mCalendar.getTime());
                    tmp.setSrcImage(img_source);

                    if (db.addMed(tmp) > 0) {

                        builder.dismiss();
                        Toast.makeText(getApplicationContext(), "Inserted with Sucess", Toast.LENGTH_SHORT).show();
                        displayMeds(listMeds);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                onCaptureImageResult(data, bt_chooseFile);
        }
    }

    private void onCaptureImageResult(Intent data, Button bt) {
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
        changeButtonText(img_source, bt);
        // ivImage.setImageBitmap(thumbnail);
    }

    public void displayMeds(List<Med> medTemp) {
        medTemp.clear();
        medTemp.addAll(db.getAllMedsList());
        if (medAdapter == null) {
            medAdapter = new MedAdapter(this, medTemp, loaderImg);
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(medAdapter);
        } else {
            medAdapter.notifyDataSetChanged();
        }
    }


    public void changeButtonText(String path, Button choose) {


        if (path != null) {
            Log.d(TAG, "=====> path  is not null  ");
            String[] path_parts = path.split("/");
            choose.setText(path_parts[path_parts.length - 1]);
        } else if (path == null) {
            Log.d(TAG, "=====> path  is NULL ");

            choose.setText("select photo");
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


    }

    public void listMyMeds(List<Med> medicine) {
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
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(listMeds.get(info.position).getName());
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_update_options, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        //  String[] menuItems = getResources().getStringArray(R.menu.menu_update_options);
        //   String menuItemName = menuItems[menuItemIndex];
        // boolean status = false;
        switch (item.getItemId()) {

            case R.id.update:
                return true;

            case R.id.delete:
                return true;

            case R.id.cancel_update:
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    public Dialog editMed(final Med med) {

        final Dialog builderEdit = new Dialog(this);

        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builderEdit.setTitle(R.string.dialog_edit);
        builderEdit.setContentView(inflater.inflate(R.layout.dialog_editmed, null));


        edit_name = (TextView) builderEdit.findViewById(R.id.edit_name);
        edit_description = (TextView) builderEdit.findViewById(R.id.edit_desc);
        edit_expiration_date_picker = (DatePicker) builderEdit.findViewById(R.id.edit_date);
        bt_chooseFile = (Button) builderEdit.findViewById(R.id.edit_imgSrc);
        edit_bt_cancel = (Button) builderEdit.findViewById(R.id.edit_cancel);
        edit_bt_update = (Button) builderEdit.findViewById(R.id.edit_save);


        edit_name.setText(med.getName());
        edit_description.setText(medChangeDesc(med.getDescription()));
        changeButtonText(med.getSrcImage(), bt_chooseFile);
        initCalendarEdit(med);

        edit_expiration_date_picker.init(mYear, month,
                day, new DatePicker.OnDateChangedListener() {
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

        edit_bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                med.setName(edit_name.getText().toString());
                med.setDescription(edit_description.getText().toString());
                med.setExpireDate(mCalendar.getTime());
                med.setSrcImage(img_source);

                if (db.updateEntry(med) > 0) {

                    builderEdit.dismiss();
                    Toast.makeText(getApplicationContext(), "Updated with Sucess", Toast.LENGTH_SHORT).show();
                    displayMeds(listMeds);
                }
            }
        });
        edit_bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cancel Dialog ", Toast.LENGTH_SHORT).show();
                builderEdit.cancel();
            }
        });
        return builderEdit;


    }

    private String medChangeDesc(String description) {

        if (description != null)
            return description;
        else return "";
    }

    public void deleteMed(Med med) {
        db.deleteEntry(med.getId());
        displayMeds(listMeds);

    }
}

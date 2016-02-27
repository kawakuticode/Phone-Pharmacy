package com.code.kawakuti.phonepharmacy.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.database.DataBaseMedsHandler;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;


public class AddMedicineActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "ADD MEDICINE ACTIVITY" ;
    private TextView medicine_name, medicine_descr;
    private TextInputLayout inputLayoutMed, inputLayoutDesc;
    private static final int REQUEST_CAMERA = 0;
    private static final int SELECT_FILE = 1;

    private Button btn_selectPhoto, btn_save, btn_cancel;
    private DatePicker expiration_date_picker;
    private Calendar mCalendar;
    private int day, month, mYear;
    private DataBaseMedsHandler db;
    private String img_source;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_medicine_);
        initCalendar();
        initFields();
    }

    public void initCalendar() {
        mCalendar = Calendar.getInstance();
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        month = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);
    }

    public void initFields() {

        db = new DataBaseMedsHandler(this.getBaseContext());
        inputLayoutMed = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutDesc = (TextInputLayout) findViewById(R.id.input_layout_description);
        medicine_name = (TextView) findViewById(R.id.input_name_med);
        medicine_descr = (TextView) findViewById(R.id.input_description);
        medicine_name.addTextChangedListener(new MyTextWatcher(medicine_name));
        medicine_descr.addTextChangedListener(new MyTextWatcher(medicine_descr));

        btn_selectPhoto = (Button) findViewById(R.id.btn_add_photo);
        btn_selectPhoto.setOnClickListener(this);
        btn_save = (Button) findViewById(R.id.save);
        btn_save.setOnClickListener(this);
        btn_cancel = (Button) findViewById(R.id.cancel);
        btn_cancel.setOnClickListener(this);
        expiration_date_picker = (DatePicker) findViewById(R.id.expiration_date);
        expiration_date_picker.init(mYear, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(year, monthOfYear + 1, dayOfMonth);
                // Log.d(TAG, mCalendar.get(Calendar.DAY_OF_MONTH) + " "
                //       + mCalendar.get(Calendar.MONTH) + " " + mCalendar.get(Calendar.YEAR));
            }
        });
    }

    private boolean validateMedName() {
        if (medicine_name.getText().toString().trim().isEmpty()) {
            inputLayoutMed.setError(getString(R.string.err_msg_name));
            requestFocus(medicine_name);
            return false;
        } else {
            inputLayoutMed.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void submitForm() {


        if (validateMedName()) {

            Med tmp = new Med();
            tmp.setName(medicine_name.getText().toString());
            tmp.setDescription(medicine_descr.getText().toString());
            tmp.setExpireDate(mCalendar.getTime());
            tmp.setSrcImage(img_source);

            if (db.addMed(tmp) > 0) {
                Toast.makeText(this, "Inserted with Sucess", Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }

    private void imageOptions() {
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

            if (requestCode == SELECT_FILE) {
                onSelectFromGallery(data);
            }
            else if (requestCode == REQUEST_CAMERA) {
                onCaptureImage(data);
            }
        }
    }

    private void onCaptureImage(Intent data) {

        String log = "---> Capture Image " ;
       // thumbnail.toString();
        dumpIntent(data , log);
    }

    private void onSelectFromGallery(Intent data) {

        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor =this.getContentResolver().query(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);
        img_source = selectedImagePath;

    }

    public static void dumpIntent(Intent i , String log){

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e(LOG_TAG + log, "Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.e(LOG_TAG + log,"[" + key + "=" + bundle.get(key)+"]");
            }
            Log.e(LOG_TAG + log,"Dumping Intent end");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_photo:

                imageOptions();

                break;
            case R.id.save:
                submitForm();
                finishAfterTransition();
                break;

            case R.id.cancel:
                finish();
                break;


        }

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        public MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            switch (view.getId()) {
                case R.id.input_name_med:
                    validateMedName();
                    break;
              /*  case R.id.input_description:
                    validateDescription();
                    break;
                case R.id.expiration_date:
                    validateDate();
                    break;*/
            }

        }
    }
}


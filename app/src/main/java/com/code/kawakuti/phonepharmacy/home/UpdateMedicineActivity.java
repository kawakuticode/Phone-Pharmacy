package com.code.kawakuti.phonepharmacy.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.database.DataBaseMedsHandler;
import com.code.kawakuti.phonepharmacy.models.Med;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateMedicineActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String FILENAME = "IMG_";
    private static final String FILETYPE = ".JPG";
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Phone Pharmacy";
    private static final int SELECT_FILE = 1;


    private Med med;
    private Button updt_save, updt_cancel, updt_slt_photo;
    private DatePicker updt_expiration_date_picker;
    private TextInputLayout inputLayoutMed, inputLayoutDesc;
    private Calendar mCalendar;
    private int day, month, mYear;
    private DataBaseMedsHandler db;
    private String img_source;
    private Uri fileUri; // file url to store image/video

    private TextView updt_name, updt_desc;

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //  Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + FILENAME + timeStamp + FILETYPE);
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_medicine);
        DataBaseMedsHandler.init(this.getBaseContext());
        db = new DataBaseMedsHandler(this.getBaseContext());


        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("medicine")) {
            setMed((Med) bundle.getParcelable("medicine"));
        }

        initCalendar();
        initFields();


    }

    public void initCalendar() {
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(getMed().getExpireDate());
        day = mCalendar.get(Calendar.DAY_OF_MONTH);
        month = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);
    }

    public void initFields() {

        inputLayoutMed = (TextInputLayout) findViewById(R.id.update_layout_name);
        inputLayoutDesc = (TextInputLayout) findViewById(R.id.update_layout_description);

        updt_name = (TextView) findViewById(R.id.update_name_med);
        updt_name.setText(getMed().getName());
        updt_desc = (TextView) findViewById(R.id.update_description);
        updt_desc.setText(getMed().getDescription());
        updt_name.addTextChangedListener(new MyTextWatcher(updt_name));
        updt_desc.addTextChangedListener(new MyTextWatcher(updt_desc));
        updt_slt_photo = (Button) findViewById(R.id.update_btn_add_photo);
        updt_slt_photo.setText(changeButtonTextToPath(getMed().getSrcImage()));
        img_source = getMed().getSrcImage();
        updt_slt_photo.setOnClickListener(this);
        updt_save = (Button) findViewById(R.id.save_upate);
        updt_save.setOnClickListener(this);
        updt_cancel = (Button) findViewById(R.id.cancel_update);
        updt_cancel.setOnClickListener(this);
        updt_expiration_date_picker = (DatePicker) findViewById(R.id.update_expiration_date);
        updt_expiration_date_picker.init(mYear, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(year, monthOfYear + 1, dayOfMonth);
            }
        });
    }

    private boolean validateMedName() {
        if (updt_name.getText().toString().trim().isEmpty()) {
            inputLayoutMed.setError(getString(R.string.err_msg_name));
            requestFocus(updt_name);
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
            getMed().setName(updt_name.getText().toString());
            getMed().setDescription(updt_desc.getText().toString());
            getMed().setExpireDate(mCalendar.getTime());
            if (getMed().getSrcImage() == null) {
                getMed().setSrcImage("");
            }
            db.updateEntry(getMed());
            db.close();
            Toast.makeText(this, "Updated with Sucess", Toast.LENGTH_SHORT).show();
        }
    }

    private void imageOptions() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    captureImage();
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
            } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                previewCapturedImage();
            }
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void previewCapturedImage() {
        try {
            img_source = fileUri.getPath();
            updt_slt_photo.setText(changeButtonTextToPath(img_source));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void onSelectFromGallery(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = this.getContentResolver().query(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);
        img_source = selectedImagePath;
        updt_slt_photo.setText(changeButtonTextToPath(img_source));

    }

    public String changeButtonTextToPath(String path) {
        String result = "";
        if (path != null) {
            result = !path.isEmpty() ? splitedPath(path)[splitedPath(path).length - 1] : " photo ";
        } else {
            result = " photo ";
        }
        return result;
    }

    public String[] splitedPath(String input) {
        return input.split("/");
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
    * Here we store the file url as it will be null after returning from camera
    * app
    */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes

        outState.putParcelable("file_uri", fileUri);
        outState.putSerializable("calendar_state", mCalendar);
        outState.putParcelable("medicine", getMed());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
        mCalendar = (Calendar) savedInstanceState.getSerializable("calendar_state");
        med = (Med) savedInstanceState.getSerializable("medicine");
    }


    public Med getMed() {
        return med;
    }

    public void setMed(Med med) {
        this.med = med;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.update_btn_add_photo:
                imageOptions();
                break;
            case R.id.save_upate:
                submitForm();
                finish();
                break;

            case R.id.cancel_update:
                db.close();
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
                case R.id.update_name_med:
                    validateMedName();
                    break;
            }

        }
    }
}

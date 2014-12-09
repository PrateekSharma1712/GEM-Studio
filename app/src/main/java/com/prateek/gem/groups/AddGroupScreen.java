package com.prateek.gem.groups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.prateek.gem.AppConstants;
import com.prateek.gem.MainActivity;
import com.prateek.gem.R;
import com.prateek.gem.expenses.AddExpenseActivity;
import com.prateek.gem.expenses.ExpensesActivity;
import com.prateek.gem.helper.AppDialog;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.utility.AppDataManager;
import com.prateek.gem.utility.Utils;
import com.prateek.gem.widgets.ConfirmationDialog;
import com.prateek.gem.widgets.FloatingActionButtonWithText;
import com.prateek.gem.widgets.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.prateek.gem.helper.AppDialog.DialogClickListener;

public class AddGroupScreen extends MainActivity implements DialogClickListener{


    private ImageView vGroupImage = null;
    private EditText vGroupName = null;
    private String[] imageCaptureOptions = new String[]{"Camera", "Gallery"};
    private String filepathFromCamera = null;
    private boolean isNew = false;
    private Intent selfIntent = null;
    private CardView vExpenseCardView = null;
    private CardView vMemberCardView = null;
    private CardView vItemCardView = null;
    private FloatingActionsMenu vAddOptions = null;
    private FloatingActionButtonWithText vAddExpenseButton = null;
    private Intent mExenseScreenIntent = null, mAddExpenseScreenIntent = null;
    private Intent mMemberScreenIntent = null;
    private Intent mItemScreenIntent = null;
    private MenuItem vEditGroup = null;
    private boolean isEditMode = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_group_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selfIntent = getIntent();
        if(selfIntent != null) {
            isNew = selfIntent.getBooleanExtra(AppConstants.GROUP_SOURCE, Boolean.FALSE);
            DebugLogger.message("isNew"+isNew);
        }

        vGroupName = (EditText) findViewById(R.id.vGroupName);
        vAddOptions = (FloatingActionsMenu) findViewById(R.id.vAddOptions);
        vAddExpenseButton = (FloatingActionButtonWithText) findViewById(R.id.vAddExpenseButton);
        vExpenseCardView = (CardView) findViewById(R.id.vExpenseBrief);
        vMemberCardView = (CardView) findViewById(R.id.vMembersBrief);
        vItemCardView = (CardView) findViewById(R.id.vItemsBrief);
        vExpenseCardView.startAnimation(Utils.inFromBottomAnimation(400));
        vMemberCardView.startAnimation(Utils.inFromBottomAnimation(400));
        vItemCardView.startAnimation(Utils.inFromBottomAnimation(400));


        if(isNew) {
            isEditMode = true;
            vExpenseCardView.setVisibility(View.GONE);
            vMemberCardView.setVisibility(View.GONE);
            vItemCardView.setVisibility(View.GONE);
            vAddOptions.setVisibility(View.GONE);
            vGroupName.clearFocus();
            AppDataManager.hideKeyboard(vGroupName);
        }


        vGroupImage = (ImageView) findViewById(R.id.vGroupIcon);
        vGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDialog.init(AppDataManager.currentScreen, getString(R.string.takephoto), null, imageCaptureOptions);
            }
        });

        vExpenseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExenseScreenIntent = new Intent(AddGroupScreen.this, ExpensesActivity.class);
                startActivity(mExenseScreenIntent);
            }
        });

        vAddExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddExpenseScreenIntent = new Intent(AddGroupScreen.this, AddExpenseActivity.class);
                startActivity(mAddExpenseScreenIntent);
            }
        });

        toggleEditMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugLogger.message("toolbar"+mToolBar);
        if(isNew) {
            setToolbar(R.string.title_activity_add_group_screen, R.drawable.ic_group_add);
        } else {
            if(AppDataManager.getCurrentGroup() != null) {
                setToolbar(AppDataManager.getCurrentGroup().getGroupName(), R.drawable.ic_group_add);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_group_screen, menu);
        vEditGroup = menu.findItem(R.id.editGroup);

        if(isNew) {
            isEditMode = true;
            vEditGroup.setIcon(R.drawable.ic_action_done);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.editGroup:
                if(isEditMode) {
                    //save/update group
                    saveGroup();

                    //finish();
                } else {
                    isEditMode = true;
                    vEditGroup.setIcon(R.drawable.ic_action_done);
                    //make image, text field enabled
                }
                toggleEditMode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveGroup() {
        if(!Utils.stringify(vGroupName.getText()).isEmpty()){
            if(!Group.contains(AppDataManager.getGroups(),Utils.stringify(vGroupName.getText()))){
                ConfirmationDialog mcd = new ConfirmationDialog();
                Bundle bundle = new Bundle();
                bundle.putString(ConfirmationDialog.TITLE, getString(R.string.addGroup));
                bundle.putInt(AppConstants.ConfirmConstants.CONFIRM_KEY, AppConstants.ConfirmConstants.FROM_ADD_GROUP);
                bundle.putString(ConfirmationDialog.BUTTON1, getString(R.string.button_cancel));
                bundle.putString(ConfirmationDialog.BUTTON2, getString(R.string.button_add));
                bundle.putString(ConfirmationDialog.MESSAGE, "Are you sure to add "+Utils.stringify(vGroupName.getText())+"?");
                mcd.setArguments(bundle);
                mcd.show(getSupportFragmentManager(), "ComfirmationDialog");
            }else{
                Utils.showError(vGroupName,this,getString(R.string.existing_group));
            }
        }
        else{
            Utils.showError(vGroupName,this);
        }
    }

    /**
     *
     * method to toggle visibility in case of edit mode and read mode
     */
    private void toggleEditMode() {
        if(isEditMode) {
            vGroupName.setEnabled(true);
        } else {
            vGroupName.setEnabled(false);
        }
    }

    @Override
    public void onItemClick(int position) {
        DebugLogger.message(imageCaptureOptions[position]+" used for taking photo");
        if(isEditMode) {
            switch (position) {
                case 0: //Camera
                    Intent getCameraImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String appName = AppDataManager.currentScreen.getResources().getString(R.string.app_name);

                    File cameraFolder;
                    if (Utils.isSDPresent())
                        cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(), appName + "/");
                    else
                        cameraFolder = AppDataManager.currentScreen.getCacheDir();

                    if (!cameraFolder.exists())
                        cameraFolder.mkdirs();

                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        DebugLogger.message("error creating file" + ex.getMessage());
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        getCameraImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                        if (getCameraImageIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(getCameraImageIntent, AppConstants.REQUEST_CODE_CLICK_IMAGE);
                        }
                    }
                    break;
                case 1: //Gallery
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_FROM_GALLERY);
                    break;
            }
        } else {
            Utils.showToast(this, "Click on edit button to edit");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        filepathFromCamera = "file:" + image.getAbsolutePath();
        return image;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLogger.message("AddGroupScreen :: onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        Uri originalUri = null;
        if (requestCode == AppConstants.REQUEST_CODE_FROM_GALLERY) {
            if(data != null) {
                originalUri = data.getData();
                String path = Utils.getPath(AppDataManager.currentScreen, originalUri);
                Drawable d = Drawable.createFromPath(path);
                vGroupImage.setImageDrawable(d);
            }
        } else if(requestCode == AppConstants.REQUEST_CODE_CLICK_IMAGE) {
            DebugLogger.message("filepathFromCamera"+filepathFromCamera);
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            vGroupImage.setImageBitmap(imageBitmap);*/
            //setPic();
            Drawable d = Drawable.createFromPath(filepathFromCamera);
            DebugLogger.message("drawable" + d);
            vGroupImage.setImageDrawable(d);
        }

    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = vGroupImage.getWidth();
        int targetH = vGroupImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepathFromCamera, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(filepathFromCamera, bmOptions);
        vGroupImage.setImageBitmap(bitmap);

    }

    @Override
    public void modeConfirmed() {
       DebugLogger.message("Confirmed");
        isEditMode = false;
        vEditGroup.setIcon(R.drawable.ic_content_create);
    }
}

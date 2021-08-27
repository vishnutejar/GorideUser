package com.quickzetuser.ui.main.sidemenu.profile;

import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.imagePicker.FileInformation;
import com.imagePicker.ProfilePicDialog;
import com.medy.retrofitwrapper.WebRequest;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.model.CountryModel;
import com.quickzetuser.model.UserModel;
import com.quickzetuser.model.request_model.UpdateProfileRequestModel;
import com.quickzetuser.model.web_response.CountriesResponseModel;
import com.quickzetuser.model.web_response.UserResponseModel;
import com.quickzetuser.ui.main.MainActivity;
import com.quickzetuser.ui.signup.dialog.DataDialog;
import com.quickzetuser.ui.utilities.S3BucketHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sunil kumar yadav on 14/3/18.
 */

public class EditProfileFragment extends AppBaseFragment {

    ProfileUpdate profileUpdate;
    private ImageView iv_user_image;
    private ImageView iv_camera;
    private TextView tv_user_name;
    private TextView tv_user_email;
    private EditText et_email;
    private EditText et_firstname;
    private EditText et_lastname;
    private EditText et_password;
    private TextView tv_mobile_code;
    private TextView tv_mobile;
    private CardView cv_layout;
    private SwitchCompat sw_notification;
    private List<CountryModel> countryList;

    public void setProfileUpdate(ProfileUpdate profileUpdate) {
        this.profileUpdate = profileUpdate;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_edit_profile;
    }

    @Override
    public void initializeComponent() {
        iv_user_image = getView().findViewById(R.id.iv_user_image);
        iv_camera = getView().findViewById(R.id.iv_camera);
        tv_user_name = getView().findViewById(R.id.tv_user_name);
        tv_user_email = getView().findViewById(R.id.tv_user_email);
        et_email = getView().findViewById(R.id.et_email);
        et_firstname = getView().findViewById(R.id.et_firstname);
        et_lastname = getView().findViewById(R.id.et_lastname);
        et_password = getView().findViewById(R.id.et_password);
        tv_mobile_code = getView().findViewById(R.id.tv_mobile_code);
        tv_mobile = getView().findViewById(R.id.tv_mobile);
        cv_layout = getView().findViewById(R.id.cv_layout);
        sw_notification = getView().findViewById(R.id.sw_notification);

        countryList = new ArrayList<>();
        iv_camera.setOnClickListener(this);
//        tv_mobile_code.setOnClickListener(this);
        getCountries();
        setUserData();
    }

    private void setUserData() {
        UserModel userModel = ((MainActivity) getActivity()).getUserModel();
        if (userModel == null) return;
        if (!userModel.getImage().isEmpty()) {
            Picasso.get()
                    .load(userModel.getImage())
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)
                    .into(iv_user_image);
        }
        tv_user_name.setText(userModel.getFullName());
        tv_user_email.setText(userModel.getEmail());
        et_email.setText(userModel.getEmail());
        et_firstname.setText(userModel.getFirstname());
        et_lastname.setText(userModel.getLastname());
        tv_mobile_code.setText(userModel.getMobile_code());
        tv_mobile_code.setTag(userModel.getMobile_code());
        tv_mobile.setText(userModel.getPhone());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_right_menu:
                onSubmit();
                break;

            case R.id.iv_camera:
                showImagePickerDialog();
                break;

            case R.id.tv_mobile_code:
//                showDataDialog("Select Country", tv_mobile_code, countryList);
                break;
        }
    }

    private void getCountries() {
        displayProgressBar(false);
        getWebRequestHelper().countries(this);
    }

    @Override
    public void onWebRequestCall(WebRequest webRequest) {
        super.onWebRequestCall(webRequest);
        ((MainActivity) getActivity()).onWebRequestCall(webRequest);
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);

        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_COUNTRIES:
                    handleCountryResponse(webRequest);
                    break;

                case ID_UPDATE_PROFILE:
                    handleUpdateUserProfileResponse(webRequest);
                    break;
            }
        } else {
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                showErrorMessage(msg);
            }
        }
    }


    private void showImagePickerDialog() {
        final ProfilePicDialog dialog = ProfilePicDialog.getNewInstance(false);
        dialog.setProfilePicDialogListner(new ProfilePicDialog.ProfilePicDialogListner() {
            @Override
            public void onProfilePicSelected(FileInformation fileInformation) {
                dialog.dismiss();
                String imageName = "IMG_" + System.currentTimeMillis();

                String large_file_path = fileInformation.getBitmapPathForUpload(getContext(),
                        FileInformation.IMAGE_SIZE_LARGE,
                        FileInformation.IMAGE_SIZE_LARGE, "large/" + imageName);

                String thumb_file_path = fileInformation.getBitmapPathForUpload(getContext(),
                        FileInformation.IMAGE_SIZE_THUMB,
                        FileInformation.IMAGE_SIZE_THUMB, "thumb/" + imageName);

                iv_user_image.setTag(R.id.image_path_tag, large_file_path);
                iv_user_image.setTag(R.id.image_path_thumb_tag, thumb_file_path);
                if (thumb_file_path!=null){
                    iv_user_image.setImageURI(Uri.parse(thumb_file_path));
                }else {
                    iv_user_image.setImageURI(null);
                }

            }

            @Override
            public void onProfilePicRemoved() {

            }
        });
        dialog.showDialog(getContext(), getChildFm());
    }

    private void showDataDialog(String title, final TextView textView, final List list) {
        if (list.size() == 0) return;
        final DataDialog dialog = new DataDialog();
        dialog.setDataList(list);
        dialog.setTitle(title);
        dialog.setOnItemSelectedListeners(new DataDialog.OnItemSelectedListener() {
            @Override
            public void onItemSelectedListener(int position) {
                dialog.dismiss();
                textView.setText(list.get(position).toString());
                textView.setTag(list.get(position));

                switch (textView.getId()) {
                    case R.id.tv_mobile_code:
                        String mobile_code = ((CountryModel) list.get(position)).getCountry_mobile_code();
                        tv_mobile_code.setText(mobile_code);
                        break;
                }
            }
        });
        dialog.show(getFragmentManager(), DataDialog.class.getSimpleName());
    }

    private void onSubmit() {
      /*  if (et_email.getText().toString().length() > 0) {
            if (!getValidate().validEmailPattern(et_email)) {
                return;
            }
        }*/
        if (!getValidate().validEmailAddress(et_email)) {
            return;
        }
        if (!getValidate().validFirstName(et_firstname)) {
            return;
        }
        if (!getValidate().validLastName(et_lastname)) {
            return;
        }
        if (et_password.getText().toString().length() > 0) {
            if (!getValidate().validPasswordLength(et_password)) {
                return;
            }
        }

        String mobile_code = (String) tv_mobile_code.getTag();


        String email = et_email.getText().toString().trim();
        String fname = et_firstname.getText().toString().trim();
        String lname = et_lastname.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String mobileno = tv_mobile.getText().toString().trim();

        UpdateProfileRequestModel requestModel = new UpdateProfileRequestModel();
        requestModel.email = email;
        requestModel.firstname = fname;
        requestModel.lastname = lname;
        if (password.length() > 0) {
            requestModel.password = password;
        }
        requestModel.phone = mobileno;
        requestModel.mobile_code = mobile_code;

        Object object = iv_user_image.getTag(R.id.image_path_tag);

        if (object != null) {
            if (APP_FILE_UPLOAD_METHOD.equals(FILE_UPLOAD_METHOD_LOCAL)) {
                File file = new File((String) object);
                requestModel.profile_image = file;
            } else {
                uploadImageToS3(requestModel);
                return;
            }
        }

        submitToServer(requestModel);


    }

    private void uploadImageToS3(final UpdateProfileRequestModel requestModel) {

        try {
            UserModel userModel = getMainActivity().getUserModel();
            String large_img = (String) iv_user_image.getTag(R.id.image_path_tag);
            String thumb_img = (String) iv_user_image.getTag(R.id.image_path_thumb_tag);
            if (large_img == null) {
                if (((MainActivity) getActivity()).getUserModel().getImage() == null) {
                    showErrorMessage("Please select a photo of your Profile photo.");
                    return;
                }
            }
            displayProgressBar(false);

            S3BucketHelper.S3BucketFile s3BucketFile = new S3BucketHelper.
                    S3BucketFile(userModel.getFullBucketPath(BUCKET_PATH_PROFILE_IMAGE), thumb_img, large_img);
            s3BucketFile.setOnS3BucketHelperListener(new S3BucketHelper.OnS3BucketHelperListener() {
                @Override
                public void onUploadComplete(S3BucketHelper.S3BucketFile s3BucketFile) {
                    printLog(s3BucketFile.toString());
                    dismissProgressBar();
                    if (s3BucketFile.isSuccess()) {
                        requestModel.profile_image = s3BucketFile.getBucket_large_url();
                        submitToServer(requestModel);
                    } else {
                        showErrorMessage("Error in file upload. Please try again.");
                    }
                }
            });
            new S3BucketHelper(getContext(), userModel).uploadFile(s3BucketFile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void submitToServer(UpdateProfileRequestModel requestModel) {
        displayProgressBar(false);
        getWebRequestHelper().updateprofile(requestModel, this);
    }

    private void handleCountryResponse(WebRequest webRequest) {
        CountriesResponseModel countries = webRequest.getResponsePojo(CountriesResponseModel.class);
        countryList.addAll(countries.getData());
    }

    private void handleUpdateUserProfileResponse(WebRequest webRequest) {
        UserResponseModel newDriver = webRequest.getResponsePojo(UserResponseModel.class);
        showCustomToast(newDriver.getMessage());
        try {
            getMainActivity().updateRiderProfile(newDriver.getData());
            getMainActivity().getSideMenuHelper().initSetUserData();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        profileUpdate.success();
        getActivity().onBackPressed();
    }

    interface ProfileUpdate {
        void success();
    }
}

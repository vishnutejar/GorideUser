package com.quickzetuser.ui.utilities;

import android.content.Context;
import android.os.Bundle;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.medy.retrofitwrapper.BaseModel;
import com.quickzetuser.model.UserModel;

import java.io.File;


/**
 * Created by ubuntu on 6/4/18.
 */

public class S3BucketHelper {


    private static final String TAG = "S3BucketHelper";
    private TransferUtility transferUtility;

    private Context context;

    private OnS3BucketHelperListener onS3BucketHelperListener;

    public S3BucketHelper(Context context, UserModel userModel) {
        this.context = context;
        intializeAws(userModel);
    }

    private void intializeAws(UserModel userModel) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(userModel.getAWS_KEY(),
                userModel.getAWS_SECRET());
        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(userModel.getAWS_REGION()));
        transferUtility = TransferUtility.builder().s3Client(s3Client).context(context).build();
    }


    public boolean uploadFile(S3BucketFile s3BucketFile) {
        return s3BucketFile.upload(transferUtility);
    }

    public OnS3BucketHelperListener getOnS3BucketHelperListener() {
        return onS3BucketHelperListener;
    }

    public void setOnS3BucketHelperListener(OnS3BucketHelperListener onS3BucketHelperListener) {
        this.onS3BucketHelperListener = onS3BucketHelperListener;
    }

    public interface OnS3BucketHelperListener {
        void onUploadComplete(S3BucketFile s3BucketFile);
    }

    public static class S3BucketFile extends BaseModel implements TransferListener {

        private static final String TAG = "S3BucketFile";

        String thumb_file_path;
        String large_file_path;
        String bucket_thumb_url;
        String bucket_large_url;
        String bucket_path;
        TransferObserver transferObserverLarge;
        TransferObserver transferObserverThumb;
        Bundle bundle;
        Exception largeFileException;
        Exception thumbFileException;
        OnS3BucketHelperListener onS3BucketHelperListener;

        public S3BucketFile(String bucket_path, String thumb_file_path, String large_file_path) {
            this.bucket_path = bucket_path;
            this.thumb_file_path = thumb_file_path;
            this.large_file_path = large_file_path;
        }

        public boolean isSuccess() {
            if (thumbFileException != null) return false;
            if (largeFileException != null) return false;
            if (isValidString(thumb_file_path)) {
                if (!isValidString(bucket_thumb_url)) return false;
            }
            if (isValidString(large_file_path)) {
                if (!isValidString(bucket_large_url)) return false;
            }
            return true;

        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            if (isSuccess()) {
                if (isValidString(thumb_file_path)) {
                    stringBuilder.append("bucket_thumb_url: ").append(bucket_thumb_url).append("\n");
                }
                if (isValidString(large_file_path)) {
                    stringBuilder.append("bucket_large_url: ").append(bucket_large_url).append("\n");
                }

            } else {
                if (thumbFileException != null) {
                    stringBuilder.append("thumbFileException: ").
                            append(thumbFileException.getMessage()).append("\n");
                }
                if (largeFileException != null) {
                    stringBuilder.append("largeFileException: ").
                            append(largeFileException.getMessage()).append("\n");
                }
            }
            return stringBuilder.toString();
        }

        public void setBucket_path(String bucket_path) {
            this.bucket_path = bucket_path;
        }

        public String getBucket_large_url() {
            return bucket_large_url;
        }

        public String getBucket_thumb_url() {
            return bucket_thumb_url;
        }

        public void putExtras(Bundle bundle) {
            this.bundle = bundle;
        }

        public void setOnS3BucketHelperListener(OnS3BucketHelperListener onS3BucketHelperListener) {
            this.onS3BucketHelperListener = onS3BucketHelperListener;
        }

        public boolean isValidFile() {

            if ((!isValidString(thumb_file_path) && !isValidString(large_file_path)) ||
                    !isValidString(bucket_path)) {
                printLog("thumb_file_path, large_file_path, bucket_path should not be null or blank");
                return false;
            }
            if (isValidString(thumb_file_path)) {
                File file = new File(thumb_file_path);
                if (!file.exists()) {
                    printLog("thumb_file_path not exists");

                    return false;
                }
            }
            if (isValidString(large_file_path)) {
                File file = new File(large_file_path);
                if (!file.exists()) {
                    printLog("large_file_path not exists");
                    return false;
                }
            }
            return true;
        }

        public void printLog(String message) {
            // Log.e(TAG,  message );
        }

        public boolean upload(TransferUtility transferUtility) {
            if (!isValidFile()) {
                return false;
            }
            if (large_file_path != null) {
                String bucket_path = this.bucket_path + "/large";
                File file = new File(large_file_path);
                transferObserverLarge = transferUtility.upload(
                        bucket_path,
                        file.getName(),
                        file,
                        CannedAccessControlList.PublicRead);
                transferObserverLarge.setTransferListener(this);
                printLog("large_file_path uploading start.");
                printLog("large_file_path : " + large_file_path);
                printLog("large_bucket_path : " + bucket_path);
            }

            if (thumb_file_path != null) {
                String bucket_path = this.bucket_path + "/thumbnail";
                File file = new File(thumb_file_path);
                transferObserverThumb = transferUtility.upload(
                        bucket_path,
                        file.getName(),
                        file,
                        CannedAccessControlList.PublicRead);
                transferObserverThumb.setTransferListener(this);
                printLog("thumb_file_path uploading start.");
                printLog("thumb_file_path : " + thumb_file_path);
                printLog("thumb_bucket_path : " + bucket_path);
            }

            return true;
        }


        @Override
        public void onStateChanged(int id, TransferState state) {
            if (transferObserverThumb != null && transferObserverThumb.getId() == id) {
                printLog("thumb file onStateChanged: " + transferObserverThumb.getState());

                if (state.COMPLETED.equals(transferObserverThumb.getState())) {
                    transferObserverThumb.setTransferListener(null);
                    bucket_thumb_url = transferObserverThumb.getBucket()
                            + "/" + transferObserverThumb.getKey();
                    printLog("thumb file url: " + bucket_thumb_url);

                } else if (state.FAILED == transferObserverThumb.getState() ||
                        state.CANCELED == transferObserverThumb.getState() ||
                        state.PENDING_CANCEL == transferObserverThumb.getState() ||
                        state.UNKNOWN == transferObserverThumb.getState()) {
                    transferObserverThumb.setTransferListener(null);

                    thumbFileException = new RuntimeException("File upload fail. State: " +
                            transferObserverThumb.getState().name());

                }
                if (transferObserverLarge != null) {
                    if (largeFileException != null || transferObserverLarge.getState() == state.COMPLETED) {
                        if (onS3BucketHelperListener != null) {
                            printLog("file uploadComplete from thumb onStateChanged");
                            onS3BucketHelperListener.onUploadComplete(this);
                        }
                    }
                }
                return;
            }
            if (transferObserverLarge != null && transferObserverLarge.getId() == id) {
                printLog("large file onStateChanged: " + transferObserverLarge.getState());

                if (state.COMPLETED.equals(transferObserverLarge.getState())) {
                    transferObserverLarge.setTransferListener(null);

                    bucket_large_url = transferObserverLarge.getBucket() + "/" +
                            transferObserverLarge.getKey();
                    printLog("large file url: " + bucket_large_url);


                } else if (state.FAILED == transferObserverLarge.getState() ||
                        state.CANCELED == transferObserverLarge.getState() ||
                        state.PENDING_CANCEL == transferObserverLarge.getState() ||
                        state.UNKNOWN == transferObserverLarge.getState()) {
                    transferObserverLarge.setTransferListener(null);
                    largeFileException = new RuntimeException("File upload fail. State: " +
                            transferObserverLarge.getState().name());

                }
                if (transferObserverThumb != null) {
                    if (thumbFileException != null || transferObserverThumb.getState() == state.COMPLETED) {
                        if (onS3BucketHelperListener != null) {
                            printLog("file uploadComplete from large onStateChanged");
                            onS3BucketHelperListener.onUploadComplete(this);
                        }
                    }
                }
                return;
            }

        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            printLog("onProgressChanged: " + id);
        }

        @Override
        public void onError(int id, Exception ex) {
            if (transferObserverThumb != null && transferObserverThumb.getId() == id) {
                thumbFileException = ex;
                printLog("thumb file uploading onError: " + ex.getMessage());

            } else if (transferObserverLarge != null && transferObserverLarge.getId() == id) {
                largeFileException = ex;
                printLog("large file uploading onError: " + ex.getMessage());


            }
            if (thumbFileException != null || largeFileException != null) {
                if (transferObserverThumb != null) {
                    transferObserverThumb.setTransferListener(null);
                }
                if (transferObserverLarge != null) {
                    transferObserverLarge.setTransferListener(null);
                }

                if (onS3BucketHelperListener != null) {
                    printLog("file uploadComplete onError: " + ex.getMessage());
                    onS3BucketHelperListener.onUploadComplete(this);
                }
            }

        }
    }
}

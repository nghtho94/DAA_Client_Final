package com.example.tho.daa_moblie_client.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.tho.daa_moblie_client.Interfaces.IdentityDownload;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.IdentityData;
import com.example.tho.daa_moblie_client.Models.Utils.Config;
import com.example.tho.daa_moblie_client.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }


    private void DownLoadIdentiy(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.URL_ISSUER)
                .build();

        IdentityDownload downloadService = retrofit.create(IdentityDownload.class);

        Integer appID = 1;

        Call<IdentityData> call = downloadService.downloadFile(appID);

        call.enqueue(new Callback<IdentityData>() {
            @Override
            public void onResponse(Call<IdentityData> call, Response<IdentityData> response) {
                if (response.isSuccessful()) {
                    Log.d("Download Identity", "server contacted and has file");
                    //boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                   // Log.d("Download Identity", "file download was a success? " + writtenToDisk);

                }else {
                    Log.d("Download Identity", "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<IdentityData> call, Throwable t) {
                Log.d("Download Identity", "Error");
            }
        });







    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("writeFileToDish", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}

/*
 * Copyright (C) 2010 David Pettersson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.aptly.client.ui;

import android.content.DialogInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import mobi.aptly.client.R;
import mobi.aptly.client.download.DownloadAgent;
import mobi.aptly.client.download.Result;
import mobi.aptly.client.model.Download;
import mobi.aptly.client.model.DownloadRef;
import mobi.aptly.client.model.DownloadRepository;
import mobi.aptly.client.model.RestDownloadRepository;
import mobi.aptly.client.util.Sanity;

public class DownloadActivity extends Activity {

    private static final String TAG = "DownloadActivity";
    private static final String REPOSITORY_URL_STRING = "http://aptly.mobi/v1/repo/";
    private final URL REPOSITORY_URL;
    private ViewSwitcher viewSwitcher;
    private ProgressBar progressBar;
    private TextView progressText;
    private TextView titleText;
    private DownloadTask downloadTask;

    private class PreparationTask extends AsyncTask<DownloadRef, Void, Download> {

        @Override
        protected Download doInBackground(DownloadRef... params) {
            DownloadRepository repository = new RestDownloadRepository(REPOSITORY_URL);
            return repository.lookup(params[0]);
        }

        @Override
        protected void onPostExecute(Download download) {
            startDownload(download);
        }
    }

    private class DownloadTask extends AsyncTask<Download, Integer, Result> {

        @Override
        protected Result doInBackground(Download... params) {
            OutputStream outputStream = null;

            try {
                File file = File.createTempFile(
                        "aptly-tmp_",
                        ".apk",
                        Environment.getExternalStorageDirectory());

                outputStream = new FileOutputStream(file);

                Download download = params[0];
                DownloadAgent downloadAgent = new DownloadAgent(
                        download.getURL(), download.getSize(), outputStream);

                while (!downloadAgent.isCompleted()) {
                    downloadAgent.grabMore();
                    publishProgress(downloadAgent.getProgress());
                }

                outputStream.close();
                return new Result(Result.Status.SUCCESS, file);
            } catch (Exception e) {
                Log.e(TAG, "Error occurred when downloading", e);
                return new Result(Result.Status.FAILURE);
            }
        }

        @Override
        protected void onCancelled() {
            Log.w(TAG, "Download was interrupted");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            progressText.setText(formatProgress(values[0], progressBar.getMax()));
        }

        @Override
        protected void onPostExecute(Result result) {
            startInstall(result);
        }

        private String formatProgress(int progress, int max) {
            return String.format("%d K / %d K", progress / 1024, max / 1024);
        }
    }

    public DownloadActivity() {
        super();
        URL url = null;
        try {
            url = new URL(REPOSITORY_URL_STRING);
        }
        catch (MalformedURLException e) {
            Log.e(TAG, "Unable to parse " + REPOSITORY_URL_STRING, e);
        }
        REPOSITORY_URL = url;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_activity);

        viewSwitcher = (ViewSwitcher) findViewById(R.id.layout_switcher);

        titleText = (TextView) findViewById(R.id.title_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressText = (TextView) findViewById(R.id.progress_text);

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                downloadTask.cancel(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Log.d(TAG, "Got data " + intent.getDataString());
        new PreparationTask().execute(new DownloadRef(intent.getDataString()));
    }

    private void startDownload(Download download) {
        titleText.setText(download.getName());
        progressBar.setMax(download.getSize());
        viewSwitcher.setDisplayedChild(1);
        downloadTask = new DownloadTask();
        downloadTask.execute(download);
    }

    private void startInstall(Result result) {
        Log.d(TAG, "Result status = " + result.getStatus());

        switch (result.getStatus()) {
            case SUCCESS:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(
                        Uri.fromFile(result.getFile()),
                        "application/vnd.android.package-archive");
                startActivity(intent);
                finish();
                break;
            case FAILURE:
                showDownloadFailedDialog();
                break;
            default:
                Sanity.fail();
                break;
        }

    }

    private void showDownloadFailedDialog() {
        Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.failure_dialog_title);
        builder.setMessage(R.string.failure_dialog_message);
        builder.setPositiveButton(R.string.failure_dialog_button,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                        finish();
                    }
                });
        builder.create().show();
    }
}

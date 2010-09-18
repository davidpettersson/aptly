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
package mobi.aptly.client.download;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class DownloadAgent {

    private final static String TAG = "DownloadAgent";
    private final static int BUFFER_SIZE = 64 * 1024;
    private final int size;
    private int progress = 0;
    private final URL url;
    private InputStream inputStream;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private final OutputStream outputStream;

    public DownloadAgent(String urlString, int size, OutputStream outputStream) throws IOException {
        this.url = new URL(urlString);
        this.size = size;
        this.outputStream = outputStream;

        HttpClient client = new DefaultHttpClient();
        HttpHost host = new HttpHost(url.getHost());
        HttpGet get = new HttpGet(url.getPath());

        HttpResponse response = client.execute(host, get);

        int status = response.getStatusLine().getStatusCode();

        if (status == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
        }
    }

    public boolean isCompleted() {
        return progress == size;
    }

    public void grabMore() throws IOException {
        int read = inputStream.read(buffer);

        if (read > 0) {
            Log.d(TAG, "Read " + read + " bytes");
            progress += read;
            outputStream.write(buffer, 0, read);
        } else {
            Log.d(TAG, "EOF reached");
        }
    }

    public int getProgress() {
        return progress;
    }
}

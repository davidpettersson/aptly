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
package mobi.aptly.client.model;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class RestDownloadRepository implements DownloadRepository {

    private final static String TAG = "RestDownloadRepository";
    private final URL base;

    public RestDownloadRepository(URL url) {
        this.base = url;
    }

    public Download lookup(DownloadRef reference) {
        try {
            URL url = new URL(this.base.toString() + reference.getIdentifier());
            Log.d(TAG, "Looking at " + url.toString());

            HttpClient client = new DefaultHttpClient();
            HttpHost host = new HttpHost(url.getHost());
            HttpGet get = new HttpGet(url.getPath());

	    Log.d(TAG, "Grabbing " + url.getPath());

            HttpResponse response = client.execute(host, get);

            int status = response.getStatusLine().getStatusCode();

            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                Properties properties = new Properties();
                properties.load(inputStream);
                inputStream.close();

                Download download = new Download(
                        properties.getProperty("Application-Name"),
                        Integer.parseInt(properties.getProperty("Application-FileSize")),
                        properties.getProperty("Application-DownloadURL"));
                return download;
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Unexpected exception during lookup", e);
            return null;
        }
    }
}

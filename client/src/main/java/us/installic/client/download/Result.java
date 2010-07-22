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
package us.installic.client.download;

import java.io.File;

public class Result {

    private final Status status;
    private final File file;

    public enum Status {

        SUCCESS, FAILURE,
    }

    public Result(Status status) {
        this(status, null);
    }

    public Result(Status status, File file) {
        this.status = status;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public Status getStatus() {
        return status;
    }
}

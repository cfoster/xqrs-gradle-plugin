/**
 * Copyright 2019 XML London Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xmllondon.xqrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static com.xmllondon.xqrs.Constants.MODULES_ROOT;

public class CopyFiles
{
  private File baseDirectory;

  private Logger log = LoggerFactory.getLogger("CopyFiles");

  public CopyFiles(File baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  public void copy() {
    ensureExists("src/main/ml-config");
    ensureExists(MODULES_ROOT + "/xqrs/optional-libraries");

    final String ROOT = "src/main/ml-modules/root";

    log.info("Copying XQRS Server Config file.");
    copyResource("/ml-config/servers/xqrs-server.json", "src/main/ml-config/servers/xqrs-server.json");

    log.info("Copying Core XQRS Library Modules");
    copyResource("/xqrs/xqrs.xqy", ROOT + "/xqrs/xqrs.xqy");
    copyResource("/xqrs/optional-libraries/restxq-function-module.xq", ROOT + "/xqrs/optional-libraries/restxq-function-module.xq");
    copyResource("/xqrs/optional-libraries/session.xq", ROOT + "/xqrs/optional-libraries/session.xq");
    copyResource("/xqrs/optional-libraries/transaction.xq", ROOT + "/xqrs/optional-libraries/transaction.xq");
    copyResource("/xqrs/optional-libraries/xqrs-error.xsl", ROOT + "/xqrs/optional-libraries/xqrs-error.xsl");
    copyResource("/xqrs/optional-libraries/xqrs-functions.xq", ROOT + "/xqrs/optional-libraries/xqrs-functions.xq");

    log.info("Copying Example RESTXQ Modules");
    copyResource("/ml-modules/root/example.xqy", ROOT + "/example.xqy");
    copyResource("/ml-modules/root/example-guestbook.xqy", ROOT + "/example-guestbook.xqy");
    copyResource("/ml-modules/root/guestbook.css", ROOT + "/guestbook.css");
  }

  private void copyResource(String inputPath, String outputPath)
  {
    File file = new File(baseDirectory, outputPath);
    if(!file.exists()) // don't overwrite what's there
      copyResource(inputPath, file);
  }

  private void copyResource(String inputPath, File outputPath)
  {
    outputPath.getParentFile().mkdirs();
    InputStream inputStream = null;
    OutputStream outputStream = null;
    try
    {
      inputStream = getClass().getResourceAsStream(inputPath);
      outputStream = new FileOutputStream(outputPath);

      byte[] buf = new byte[1024];
      int bytesRead;
      while ((bytesRead = inputStream.read(buf)) > 0) {
        outputStream.write(buf, 0, bytesRead);
      }
    } catch(IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        inputStream.close();
        outputStream.close();
      } catch(Throwable e) {

      }
    }
  }

/*
  private void copy(String fromPath, String toPath) {
    try {
      JarUtils.copyResourcesToDirectory(
        JarUtils.jarForClass(CopyFiles.class, null),
        fromPath,
        baseDirectory + toPath
      );
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
*/
  private File ensureExists(String relativePath) {
    File f = new File(baseDirectory, relativePath);

    if(!f.exists()) {
      log.info("Making directory: " + relativePath);
      f.mkdirs();
    }
    return f;
  }

}

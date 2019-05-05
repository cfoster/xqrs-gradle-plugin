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

import java.io.File;
import java.io.IOException;

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

    log.info("Copying XQRS Server Config file.");
    copy("ml-config", "/src/main/ml-config");
    log.info("Copying Core XQRS Library Modules");
    copy("xqrs", MODULES_ROOT + "/xqrs");
    log.info("Copying Example RESTXQ Modules");
    copy("ml-modules", "/src/main/ml-modules");
  }

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

  private File ensureExists(String relativePath) {
    File f = new File(relativePath);

    if(!f.exists()) {
      log.info("Making directory: " + relativePath);
      f.mkdirs();
    }
    return f;
  }

}

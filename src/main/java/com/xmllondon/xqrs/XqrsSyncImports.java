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

import com.xmllondon.xqrs.io.UTF8Writer;
import com.xmllondon.xqrs.pojo.XQueryFileMetadata;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class XqrsSyncImports extends DefaultTask {

  private Logger log = LoggerFactory.getLogger("XqrsSyncImports");

  @TaskAction
  public void sync() {
    File root = new File(
      getProject().getRootDir(),
      "src/main/ml-modules/root"
    );

    try {
      files(root);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  Pattern startFinishPattern = Pattern.compile(
    "\\(: Import RESTXQ Modules - start.+Import RESTXQ Modules - finish :\\)",
    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
  );

  public void files(File baseDirectory) throws IOException {
    log.info("Searching for candidate XQuery files to include");
    long t1 = System.currentTimeMillis();
    List<XQueryFileMetadata> md =
      Files.walk(Paths.get(baseDirectory.toURI()))
        .filter(p -> {
          return Files.isRegularFile(p) &&
            (p.toFile().getName().endsWith(".xqy") ||
              p.toFile().getName().endsWith(".xq"));
        })
        .map(this::inspect).collect(Collectors.toList());
    long t2 = System.currentTimeMillis();
    log.info("Completed search, operation took "+(t2-t1)+" ms.");

    StringBuilder buff = new StringBuilder();
    HashSet<String> usedPrefixes = new HashSet<String>();

    String[] predefined = new String[] {
      "cts", "dav", "dbg", "dir", "err", "error", "fn", "local", "lock",
      "map", "math", "prof", "prop", "sec", "sem", "spell", "xdmp", "xml",
      "xmlns", "xqe", "xqterr", "xs", "rest"
    };
    usedPrefixes.addAll(Arrays.asList(predefined));

    buff.append("(: Import RESTXQ Modules - start :)\n");
    buff.append("(: Last Sync - " + new Date().toString() + " :)\n");

    for(XQueryFileMetadata item : md) {
      if(item.isXQRSModule()) {
        String preferredPrefix = item.getPrefix();
        String namespaceUri = item.getNamespaceURI();
        String prefix = getPrefix(preferredPrefix, usedPrefixes, 0);

        String path =
          item.getFile().getAbsolutePath().replace('\\','/').replaceAll(
            "^.*src/main/ml-modules/root", ""
          );

        buff.append(
          "import module namespace "+prefix+" = \""+namespaceUri+"\" \n" +
          "  at \""+path+"\";\n");

        usedPrefixes.add(prefix);
      }
    }
    buff.append("(: Import RESTXQ Modules - finish :)");

    File xqrsRewriterFile =
      new File(getProject().getRootDir(), Constants.XQRS_REWRITER_PATH);

    log.info("Reading existing xqrs.xqy file on disk");
    String xqrsRewriterCode = Util.string(xqrsRewriterFile);

    log.info("Performing RegEx replace to update the module imports");

    String imports = buff.toString();
    log.info("New Import XQuery code is as follows");
    log.info(imports);

    xqrsRewriterCode =
      xqrsRewriterCode.replace(Constants.XQRS_REPLACE_COMMENT, imports);
    xqrsRewriterCode =
      startFinishPattern.matcher(xqrsRewriterCode).replaceAll(imports);

    log.info("Writing updated xqrs.xqy file.");
    FileOutputStream out = new FileOutputStream(xqrsRewriterFile);
    UTF8Writer writer = new UTF8Writer(out);
    writer.write(xqrsRewriterCode.toCharArray());
    writer.flush();
    out.close();
  }

  private String getPrefix(
    String preferredPrefix,
    HashSet<String> usedPrefixes,
    int count
  )
  {
    if(count == 0 && !usedPrefixes.contains(preferredPrefix)) {
      return preferredPrefix;
    }
    else
    {
      count++;
      String prefixAttempt = preferredPrefix + "-" + count;
      if(!usedPrefixes.contains(prefixAttempt)) {
        return prefixAttempt;
      } else {
        return getPrefix(preferredPrefix, usedPrefixes, count);
      }
    }
  }

  public XQueryFileMetadata inspect(Path path) {
    XQueryFileMetadata md = Util.inspectXQueryFile(path.toFile());
    return md;

  }


}

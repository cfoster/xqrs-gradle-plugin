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

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

import static com.xmllondon.xqrs.Constants.XQRS_DEFAULT_PORT;

public class XqrsInitTask extends DefaultTask {

  private Logger log = LoggerFactory.getLogger("XqrsInitTask");

  @TaskAction
  public void initialize() {
    ensureXqrsPort();
    CopyFiles cf = new CopyFiles(getProject().getRootDir());
    cf.copy();
  }

  private void ensureXqrsPort() {

    Map prop = getProject().getProperties();

    if(!prop.containsKey("mlXqrsPort")) {

      System.out.println();
      System.out.println(
        "What is the HTTP Port you would like the XQRS / " +
          "RESTXQ Server to listen on?"
      );

      Scanner scanner = new Scanner(System.in);
      System.out.print("XQRS Server Port [default: "+XQRS_DEFAULT_PORT+"]: ");
      System.out.flush();
      String port = scanner.nextLine();
      if(port == null || port.trim().isEmpty())
        port = XQRS_DEFAULT_PORT;
      System.out.println();
      prop.put("mlXqrsPort", port);

      try
      {
        File file = new File(getProject().getRootDir(), "gradle.properties");

        FileWriter fr = new FileWriter(file, true);
        BufferedWriter br = new BufferedWriter(fr);
        PrintWriter pr = new PrintWriter(br);
        pr.println();
        pr.println("# XQRS (RESTXQ Server)");
        pr.println("mlXqrsPort=" + port);
        pr.close();
        br.close();
        fr.close();
      } catch(IOException e) {
        log.error(
          "Problem appending mlXqrsPort value to gradle.properties file.", e
        );
        throw new RuntimeException(e);
      }
    }
  }

}

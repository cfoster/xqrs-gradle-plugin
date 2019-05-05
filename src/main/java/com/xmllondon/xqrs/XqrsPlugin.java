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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class XqrsPlugin implements Plugin<Project> {

  private Logger log = LoggerFactory.getLogger("XqrsPlugin");

  @Override
  public void apply(Project project) {

    project.getTasks().create(
      options(
        "xqrsInit",
        "Initialize and install XQRS code (never overwrites any of your files)",
        XqrsInitTask.class
      )
    );
    project.getTasks().create(
      options(
        "xqrsSyncImports",
        "Syncs RESTXQ Resource Modules you've written so that they're used by XQRS",
        XqrsSyncImports.class
      )
    );

    try {
      Task mlPrepareBundles =
        project.getTasks().getByName("mlPrepareBundles");

      if (mlPrepareBundles != null) {
        mlPrepareBundles.dependsOn("xqrsSyncImports");
      }
    } catch(UnknownTaskException e) {
      log.warn("Task 'mlPrepareBundles' is not around," +
        " do you have the ml-gradle plugin installed?");
    }
  }

  private Map<String,Object> options(
    String name,
    String description,
    Class clazz) {
    HashMap<String,Object> map = new HashMap();

    map.put("name", name);
    map.put("description", description);
    map.put("type", clazz);
    map.put("group", "XQRS (RESTXQ)");

    return map;

  }

}

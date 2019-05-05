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

package com.xmllondon.xqrs.pojo;

import java.io.File;

public class XQueryFileMetadata
{
  private final boolean isXQRSModule;
  private final String prefix;
  private final String namespaceURI;
  private final File file;

  public XQueryFileMetadata(
    boolean isXQRSModule,
    String prefix,
    String namespaceURI,
    File file)
  {
    this.isXQRSModule = isXQRSModule;
    this.prefix = prefix;
    this.namespaceURI = namespaceURI;
    this.file = file;
  }

  public boolean isXQRSModule() {
    return isXQRSModule;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getNamespaceURI() {
    return namespaceURI;
  }

  public File getFile() {
    return file;
  }

  @Override
  public String toString() {
    return "[\n" +
        "  file = " + getFile().getAbsolutePath() + "\n" +
        "  is XQRS Module = " + isXQRSModule() + "\n" +
        "  Module Prefix = " + getPrefix() + "\n" +
        "  Module URI = " + getNamespaceURI() + "\n" +
      "]";

  }

}

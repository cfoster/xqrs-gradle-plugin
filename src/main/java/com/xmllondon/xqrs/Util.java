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

import com.xmllondon.xqrs.io.UTF8Reader;
import com.xmllondon.xqrs.pojo.XQueryFileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Util
{
  private static Logger log = LoggerFactory.getLogger("XqrsUtil");

  public static XQueryFileMetadata inspectXQueryFile(File file)
  {
    Parser p=null;

    try {
      String xqueryString = string(file);
      XQueryEventHandler handler = new XQueryEventHandler(xqueryString, file);
      p = new Parser(xqueryString, handler);

      p.parse_XQuery();
      return handler.getXQueryFileMetadata();
    }
    catch(Parser.ParseException e) {
      log.warn(
        "There may be an XQuery Syntax Error in '"+file.getAbsolutePath()+"'.");
      log.warn(p.getErrorMessage(e));

      return new XQueryFileMetadata(
        false, null, null, file
      );
    }
    catch(IOException e) {
      log.error("Problem reading XQuery file '"+file.getAbsolutePath()+"'.", e);
      throw new RuntimeException(e);
    }
  }


  public static String string(File file) throws IOException {
    FileInputStream in = new FileInputStream(file);
    CharSequence ret = charSequence(in);
    in.close();
    return ret.toString();
  }

  public static CharSequence charSequence(File file) throws IOException {
    FileInputStream in = new FileInputStream(file);
    CharSequence ret = charSequence(in);
    in.close();
    return ret;
  }

  public static CharSequence charSequence(InputStream utf8in) throws IOException {
    StringBuilder buffer = new StringBuilder();

    UTF8Reader reader = new UTF8Reader(1024);
    reader.setInput(utf8in);
    char[] cbuff = new char[1024];

    int i=0;
    while((i = reader.read(cbuff)) > -1)
      buffer.append(cbuff, 0, i);

    return buffer;
  }

}

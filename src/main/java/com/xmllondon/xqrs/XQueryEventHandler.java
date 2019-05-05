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

import com.xmllondon.xqrs.pojo.XQueryFileMetadata;

import java.io.File;
import java.util.HashMap;

import static com.xmllondon.xqrs.Constants.RESTXQ_NAMESPACE_URI;

public class XQueryEventHandler implements Parser.EventHandler {

  private final String original;
  private final File originalFile;

  /** prefix => uri **/
  private HashMap<String, String> namespaceDeclarations
    = new HashMap<String, String>();

  private boolean hasPathAnnotation = false;
  private boolean hasErrorAnnotation = false;

  private String modulePrefix = null;
  private String moduleUri = null;

  private int state = 0;
  private boolean parseComplete = false;

  private static final int STATE_NAMESPACE_DECLARATION = 1;
  private static final int STATE_NAMESPACE_PREFIX = 2;
  private static final int STATE_NAMESPACE_URI = 3;

  private static final int STATE_FUNCTION_ANNOTATION = 4;
  private static final int STATE_FUNCTION_ANNOTATION_EQNAME = 5;

  private static final int STATE_MODULE_DECLARATION = 6;
  private static final int STATE_MODULE_PREFIX = 7;
  private static final int STATE_MODULE_URI = 8;
  // rest:path, rest:error

  private static final boolean DEBUG_MODE = false;

  private boolean isXQRSModule() {
    return
      /** must be a Library Module and not a Main Module **/
      (modulePrefix != null && moduleUri != null) ||
      /** Module URI of the library is in the RESTXQ Namespace **/
      moduleUri == RESTXQ_NAMESPACE_URI &&
      /** must contain at least one %path:rest or %path:error annotation **/
      (hasPathAnnotation || hasErrorAnnotation);
  }

  public XQueryFileMetadata getXQueryFileMetadata() {
    if(!parseComplete)
      throw new IllegalStateException("XQuery parsing is not complete.");

    return new
      XQueryFileMetadata(
        isXQRSModule(), modulePrefix, moduleUri, originalFile
      );
  }

  public XQueryEventHandler(
    /* streamWriter: XMLStreamWriter, */
    String original,
    File originalFile) {
    this.original = original;
    this.originalFile = originalFile;
  }

  @Override
  public void reset(CharSequence string) {
    if(DEBUG_MODE)
      System.out.println("START DOCUMENT");
  }

  public void startNonterminal(String name, int begin) {
    //streamWriter.writeStartElement(name)
    if(DEBUG_MODE)
      System.out.println("start " + name);

    if(state == 0 && name.equals("NamespaceDecl")) {
      state = STATE_NAMESPACE_DECLARATION;
    }
    else if(state == STATE_NAMESPACE_DECLARATION && name.equals("NCName")) {
      state = STATE_NAMESPACE_PREFIX;
    }
    else if(state == STATE_NAMESPACE_DECLARATION && name.equals("URILiteral")) {
      state = STATE_NAMESPACE_URI;
    }

    if(state == 0 && name.equals("ModuleDecl")) {
      state = STATE_MODULE_DECLARATION;
    }
    else if(state == STATE_MODULE_DECLARATION && name.equals("NCName")) {
      state = STATE_MODULE_PREFIX;
    }
    else if(state == STATE_MODULE_DECLARATION && name.equals("URILiteral")) {
      state = STATE_MODULE_URI;
    }

    if(state == 0 && name.equals("Annotation")) {
      state = STATE_FUNCTION_ANNOTATION;
    }
    else if(state == STATE_FUNCTION_ANNOTATION && name.equals("FunctionName")) {
      state = STATE_FUNCTION_ANNOTATION_EQNAME;
    }
  }

  public void endNonterminal(String name, int end) {
    if(DEBUG_MODE)
      System.out.println("end " + name);

    if(state == STATE_NAMESPACE_PREFIX && name.equals("NCName")) {
      state = STATE_NAMESPACE_DECLARATION;
    }
    else if (state == STATE_NAMESPACE_URI && name.equals("URILiteral")) {
     state = STATE_NAMESPACE_DECLARATION;
    }
    else if(state == STATE_NAMESPACE_DECLARATION && name.equals("NamespaceDecl"))
    {
      state = 0;
      namespaceDeclarations.put(_prefix, _uri);
      _prefix = null;
      _uri = null;
    }
    else if(state == STATE_FUNCTION_ANNOTATION_EQNAME && name.equals("FunctionName")) {
      state = STATE_FUNCTION_ANNOTATION;
    }
    else if(state == STATE_FUNCTION_ANNOTATION && name.equals("Annotation")) {
      state = 0;

      String[] QNameSplit = _annotationQName.split(":");
      String associatedUriOfPrefix = null, annotationLocalName = null;

      if(QNameSplit.length >= 1)
        associatedUriOfPrefix = namespaceDeclarations.get(QNameSplit[0]);
      if(QNameSplit.length >= 2)
        annotationLocalName = QNameSplit[1];

      if(RESTXQ_NAMESPACE_URI.equals(associatedUriOfPrefix)) {
        if("path".equals(annotationLocalName)) {
          hasPathAnnotation = true;
        }
        else if("error".equals(annotationLocalName)) {
          hasErrorAnnotation = true;
        }
      }
      _annotationQName = null;
    }

    else if(state == STATE_MODULE_DECLARATION) {
      state = 0;
    }
    else if(state == STATE_MODULE_PREFIX && name.equals("NCName")) {
      state = STATE_MODULE_DECLARATION;
    }
    else if(state == STATE_MODULE_URI && name.equals("URILiteral")) {
      state = STATE_MODULE_DECLARATION;
    }

    if (name == "XQuery") {
      parseComplete = true;
      if(DEBUG_MODE)
        System.out.println("END DOCUMENT");
    }
  }

  private String _prefix = null;
  private String _uri = null;
  private String _annotationQName = null;

  public void terminal(String name, int begin, int end) {
    String value = original.substring(begin, end);

    if(DEBUG_MODE)
      System.out.println("terminal '" + value + "'");

    if(state == STATE_NAMESPACE_PREFIX) {
      _prefix = value;
    }
    else if(state == STATE_NAMESPACE_URI) {
      _uri = value.substring(1, value.length() - 1);
    }
    else if(state == STATE_FUNCTION_ANNOTATION_EQNAME) {
      _annotationQName = value;
    }
    else if(state == STATE_MODULE_PREFIX) {
      modulePrefix = value;
    }
    else if(state == STATE_MODULE_URI) {
      moduleUri = value.substring(1, value.length() - 1);
    }
  }

  public void whitespace(int begin, int end) {
    if(DEBUG_MODE)
      System.out.println("white space " + begin + ", " + end);
  }
}
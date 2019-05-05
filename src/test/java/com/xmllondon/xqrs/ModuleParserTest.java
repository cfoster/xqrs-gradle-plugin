package com.xmllondon.xqrs;


import com.xmllondon.xqrs.pojo.XQueryFileMetadata;
import com.xmllondon.xqrs.Util;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class ModuleParserTest {

  @Test
  public void testExampleFile() throws IOException {
    XQueryFileMetadata md =
      Util.inspectXQueryFile(
        new File("src/main/resources/ml-modules/root/example.xqy")
      );

    assertEquals("example", md.getPrefix());
    assertEquals("http://www.example.org/example", md.getNamespaceURI());
    assertTrue(md.isXQRSModule());
  }
}

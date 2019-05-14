package com.xmllondon.xqrs;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertTrue;

public class CopyFilesTest
{

  private static final File targetDir = new File(
    System.getProperty("java.io.tmpdir"),
    "copy-files-test"
  );

  @BeforeClass
  public static void setup() {
    targetDir.mkdirs();
  }

  @Test
  public void testExtract() {

    CopyFiles cf = new CopyFiles(targetDir);
    cf.copy();

    final String ROOT = "src/main/ml-modules/root";

    assertFileExists(
      new File(targetDir, "src/main/ml-config/servers/xqrs-server.json")
    );

    assertFileExists(
      new File(targetDir, ROOT + "/example.xqy")
    );

    assertFileExists(
      new File(targetDir, ROOT + "/example-guestbook.xqy")
    );

    assertFileExists(
      new File(targetDir, ROOT + "/guestbook.css")
    );

    assertFileExists(
      new File(targetDir, ROOT + "/xqrs/xqrs.xqy")
    );

    assertFileExists(
      new File(targetDir, ROOT + "/xqrs/optional-libraries/restxq-function-module.xq")
    );

    assertFileExists(
      new File(targetDir, ROOT + "/xqrs/optional-libraries/session.xq")
    );

    assertFileExists(
      new File(targetDir, ROOT + "/xqrs/optional-libraries/transaction.xq")
    );

    assertFileExists(
      new File(targetDir, ROOT + "/xqrs/optional-libraries/xqrs-error.xsl")
    );

    assertFileExists(
      new File(targetDir, ROOT + "/xqrs/optional-libraries/xqrs-functions.xq")
    );



  }

  public void assertFileExists(File file) {
    assertTrue(
      "File " + file.getAbsolutePath() + " should exist.",
      file.exists()
    );
    assertTrue(
      "File " + file.getAbsolutePath() + " should be a file.",
      file.isFile()
    );
    assertTrue(
      "File " + file.getAbsolutePath() + " should not be not empty.",
      file.length() > 0
    );
  }


  @AfterClass
  public static void teardown() {
    targetDir.delete();
  }

}

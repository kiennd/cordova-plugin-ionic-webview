package com.ionicframework.cordova.webview;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.LOG;
import org.apache.cordova.CordovaPreferences;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class DecryptResource {

  private static final String TAG = "DecryptResource";

  private static final String CRYPT_KEY = "";
  private static final String CRYPT_IV = "";

  private static final String[] CRYPT_FILES = {
    ".htm",
    ".html",
    ".js",
    ".css",
  };

  public static InputStream decryptInputStream(InputStream inputStream) throws IOException {

    BufferedReader br  = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder strb = new StringBuilder();
    String line = null;
    while ((line = br.readLine()) != null) {
      strb.append(line);
    }
    br.close();

    byte[] bytes = Base64.decode(strb.toString(), Base64.DEFAULT);

    ByteArrayInputStream byteInputStream = null;
    try {
      SecretKey skey = new SecretKeySpec(CRYPT_KEY.getBytes("UTF-8"), "AES");
      Cipher cipher  = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(CRYPT_IV.getBytes("UTF-8")));

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bos.write(cipher.doFinal(bytes));
      byteInputStream = new ByteArrayInputStream(bos.toByteArray());
    } catch (Exception ex) {
      LOG.e(TAG, ex.getMessage());

    }
    return byteInputStream;

  }

  public static boolean isCryptFiles(String uri) {
    for (String ext: CRYPT_FILES) {
      if (uri.endsWith(ext)) {
        return true;
      }
    }
    return false;
  }
}

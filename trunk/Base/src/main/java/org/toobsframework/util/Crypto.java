package org.toobsframework.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Crypto {
  private static final String ALGORITHM = "DES";
  private static Crypto instance = null;
  private Cipher encipher = null;
  private Cipher decipher = null;
  private BASE64Encoder encoder = new BASE64Encoder();  
  private BASE64Decoder decoder = new BASE64Decoder();  
  
  private static byte[] keyData = {
    (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
    (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99/*,
    (byte)0x33, (byte)0xa4, (byte)0xff, (byte)0x9f,
    (byte)0x11, (byte)0xa7, (byte)0xe4, (byte)0x44*/
  };
  
  protected Crypto() {
    try {
      DESKeySpec keySpec = new DESKeySpec(keyData); 
      SecretKey key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec);
      initEncryptCipher(key);
      initDecryptCipher(key);
    } catch (InvalidKeyException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvalidKeySpecException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  public static Crypto getInstance() {
    if (instance == null) {
      instance = new Crypto();
    }
    return instance;
  }
  
  private void initEncryptCipher(Key key) {
    try {
      encipher = Cipher.getInstance(ALGORITHM);
      encipher.init(Cipher.ENCRYPT_MODE, key);
    } catch (InvalidKeyException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void initDecryptCipher(Key key) {
    try {
      decipher = Cipher.getInstance(ALGORITHM);
      decipher.init(Cipher.DECRYPT_MODE, key);
    } catch (InvalidKeyException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public String encryptForURI(String input) {
    String output = encrypt(input);
    if (output != null) {
      output = output.replaceAll("\\+", "_");
      output = output.replaceAll("=", "X");
    }
    return output;
  }
  
  public String encrypt(String input) {
    String output = input;
    try {
      byte[] enc = encipher.doFinal(input.getBytes("UTF-8"));
      output = encoder.encode(enc);
    } catch (IllegalBlockSizeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (BadPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return output;
  }

  public String decrypt(String input) {
    String output = input;
    try {
      byte[] dec = decoder.decodeBuffer(input);
      byte[] utf8 = decipher.doFinal(dec);
      output = new String(utf8, "UTF8");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (BadPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return output;
  }
  
  public static void main(String args[]) {
    String theString = "tikitus@gmail.com";
    String crypted = Crypto.getInstance().encrypt(theString);
    System.out.println("I: " + crypted);
    Crypto crypto = null;
    for (int i=0; i<50; i++) {
      crypto = new Crypto();
      crypted = crypto.encrypt(theString);
      System.out.println(i + ": " + crypted);
    }
  }
}

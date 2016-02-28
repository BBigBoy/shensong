package com.fuyang.shensong.util;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {
    /*
        String rsaPublicKey = "<RSAKeyValue><Modulus>kHncFKRQ4fI7TN2Y+P1uoGR43iepe9vleIJ0Yuu4Hwu5lH0dckTJzkenFc+nnpLbpSDZmhYhCuFVpXN2Cwxt1WRwZMj6Rc/FXR0QCG619QEy3BeIVck5U5yMMcEj9Y/VoW4oQfHU4DrWqntyFd6xorz2LaahN/1LmuifPQrvwe8=</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>";
    */
    public static String modulus = "kHncFKRQ4fI7TN2Y+P1uoGR43iepe9vleIJ0Yuu4Hwu5lH0dckTJzkenFc+nnpLbpSDZmhYhCuFVpXN2Cwxt1WRwZMj6Rc/FXR0QCG619QEy3BeIVck5U5yMMcEj9Y/VoW4oQfHU4DrWqntyFd6xorz2LaahN/1LmuifPQrvwe8=";
    public static String exponent = "AQAB";
    static int MAXENCRYPTSIZE = 117;

    /**
     * @param modulus
     * @param exponent
     * @return
     */
    public static PublicKey getPublicKey(String modulus, String exponent) {
        try {
            byte[] m = Base64.decode(modulus, Base64.NO_WRAP);
            byte[] e = Base64.decode(exponent, Base64.NO_WRAP);
            BigInteger b1 = new BigInteger(1, m);
            BigInteger b2 = new BigInteger(1, e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt(byte[] source, PublicKey publicKey) throws Exception {
        String encryptData = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int length = source.length;
            int offset = 0;
            byte[] cache;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int i = 0;
            while (length - offset > 0) {
                if (length - offset > MAXENCRYPTSIZE) {
                    cache = cipher.doFinal(source, offset, MAXENCRYPTSIZE);
                } else {
                    cache = cipher.doFinal(source, offset, length - offset);
                }
                outStream.write(cache, 0, cache.length);
                i++;
                offset = i * MAXENCRYPTSIZE;
            }
            return new String(Base64.encode(outStream.toByteArray(), Base64.NO_WRAP), "utf-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return encryptData;
    }
}
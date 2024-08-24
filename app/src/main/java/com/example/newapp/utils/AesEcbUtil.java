package com.example.newapp.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import android.util.Log;

public class AesEcbUtil {
    static String sKey="Axuej@Client2020";

    // 加密
    public static String Encrypt(String sSrc) throws Exception {
        Log.d("AesEcbUtil", "加密内容:"+sSrc);
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        String res= Base64.encodeToString(encrypted, Base64.DEFAULT);
//        return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        Log.d("AesEcbUtil", "加密结束:"+res);
        return res;
    }

    // 解密
    public static String Decrypt(String sSrc) throws Exception {
        Log.d("AesEcbUtil", "解密内容:"+sSrc);
        try {
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            byte[] encrypted1 = Base64.decode(sSrc.getBytes("UTF-8"), Base64.DEFAULT);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                Log.d("AesEcbUtil", "解密结束:"+originalString);
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

}


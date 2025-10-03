package run.ice.zero.common.util.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * @author DaoDao
 */
public class AesUtil {

    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final int SIZE = 128;

    public enum Encode {
        HEX,
        BASE64,
    }

    private static String encode(Encode encode, byte[] bytes) {
        return switch (encode) {
            case HEX -> HexFormat.of().withUpperCase().formatHex(bytes);
            case BASE64 -> Base64.getEncoder().encodeToString(bytes);
        };
    }

    private static byte[] decode(Encode encode, String string) {
        return switch (encode) {
            case HEX -> HexFormat.of().parseHex(string);
            case BASE64 -> Base64.getDecoder().decode(string.getBytes(StandardCharsets.UTF_8));
        };
    }

    private static SecretKeySpec generate() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(SIZE, new SecureRandom());
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] bytes = secretKey.getEncoded();
        return new SecretKeySpec(bytes, ALGORITHM);
    }

    public static String key() throws NoSuchAlgorithmException {
        SecretKeySpec secretKeySpec = generate();
        byte[] bytes = secretKeySpec.getEncoded();
        return encode(Encode.HEX, bytes);
    }

    public static String encrypt(String key, String iv, String data) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return encrypt(key, iv, data, Encode.HEX);
    }

    public static String encrypt(String key, String iv, String data, Encode encode) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (null == data || data.isEmpty()) {
            return data;
        }
        int mode = Cipher.ENCRYPT_MODE;
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = doCipher(mode, keyBytes, ivBytes, dataBytes);
        return encode(encode, bytes);
    }

    public static String decrypt(String key, String iv, String data) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return decrypt(key, iv, data, Encode.HEX);
    }

    public static String decrypt(String key, String iv, String data, Encode encode) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (null == data || data.isEmpty()) {
            return data;
        }
        int mode = Cipher.DECRYPT_MODE;
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = decode(encode, data);
        byte[] bytes = doCipher(mode, keyBytes, ivBytes, dataBytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static byte[] doCipher(int mode, byte[] keyBytes, byte[] ivBytes, byte[] dataBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(mode, keySpec, ivSpec);
        return cipher.doFinal(dataBytes);
    }

}

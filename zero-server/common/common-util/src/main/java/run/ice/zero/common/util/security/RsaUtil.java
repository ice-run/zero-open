package run.ice.zero.common.util.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Base64;

/**
 * RSA 工具类
 *
 * @author DaoDao
 */
public class RsaUtil {

    public static final String ALGORITHM = "RSA";
    private static final String RSA_ECB_PADDING = "RSA/ECB/PKCS1Padding";
    private static final String SIGNATURE = "SHA256withRSA";

    /**
     * RSA算法规定：待加密的字节数不能超过密钥的长度值除以8再减去11。
     * 而加密后得到密文的字节数，正好是密钥的长度值除以 8。
     */
    private static final int RESERVE_BYTES = 11;
    private static final int BIT = 8;

    /**
     * 生成密钥对
     *
     * @throws NoSuchAlgorithmException e
     */
    public static KeyPair keyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(keySize, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    public static PublicKey publicKey(String text) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String key = extract(text);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey privateKey(String text) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String key = extract(text);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey publicKey(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
        String algorithm = rsaPrivateKey.getAlgorithm();
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateKeySpec.getModulus(), BigInteger.valueOf(65537), privateKeySpec.getParams());
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static String publicKey(PublicKey publicKey) throws IOException {
        String type = "PUBLIC KEY";
        byte[] bytes = publicKey.getEncoded();
        return format(type, bytes);
    }

    public static String privateKey(PrivateKey privateKey) throws IOException {
        String type = "PRIVATE KEY";
        byte[] bytes = privateKey.getEncoded();
        return format(type, bytes);
    }

    public static String format(String type, byte[] bytes) throws IOException {
        String base64Encoded = Base64.getEncoder().encodeToString(bytes);
        StringBuilder result = new StringBuilder();
        result.append("-----BEGIN ").append(type).append("-----\n");

        // 每行64个字符
        int lineLength = 64;
        for (int i = 0; i < base64Encoded.length(); i += lineLength) {
            result.append(base64Encoded, i, Math.min(i + lineLength, base64Encoded.length())).append('\n');
        }

        result.append("-----END ").append(type).append("-----");
        return result.toString();
    }

    public static String extract(String key) throws IOException {
        if (!key.contains("-----")) {
            return key;
        }
        // 去除PEM头尾和所有空白字符
        // 内容已经是Base64编码
        return key.replaceAll("-----BEGIN [^-]+-----", "")
                .replaceAll("-----END [^-]+-----", "")
                .replaceAll("\\s+", "");
    }

    public static String encrypt(String publicKey, String plains) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return encrypt(publicKey(publicKey), plains);
    }

    /**
     * 公钥加密
     *
     * @param publicKey 公钥
     * @param plains    明文
     * @return ciphers 密文
     */
    public static String encrypt(PublicKey publicKey, String plains) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        RSAPublicKey key = (RSAPublicKey) publicKey;
        BigInteger modulus = key.getModulus();
        int bitLength = modulus.bitLength();
        int block = (bitLength / BIT) - RESERVE_BYTES;
        Cipher cipher = Cipher.getInstance(RSA_ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = cipher(cipher, block, plains.getBytes());
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String decrypt(String privateKey, String ciphers) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return decrypt(privateKey(privateKey), ciphers);
    }

    /**
     * 私钥解密
     *
     * @param privateKey 私钥
     * @param ciphers    密文
     * @return plains 明文
     */
    public static String decrypt(PrivateKey privateKey, String ciphers) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        RSAPrivateKey key = (RSAPrivateKey) privateKey;
        BigInteger modulus = key.getModulus();
        int bitLength = modulus.bitLength();
        int block = (bitLength / BIT);
        Cipher cipher = Cipher.getInstance(RSA_ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes = cipher(cipher, block, Base64.getDecoder().decode(ciphers));
        return new String(bytes);
    }

    public static String sign(String privateKey, String data) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, SignatureException {
        return sign(privateKey(privateKey), data);
    }

    /**
     * 签名
     *
     * @param privateKey 私钥
     * @param data       待签名数据
     * @return 签名
     */
    public static String sign(PrivateKey privateKey, String data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(SIGNATURE);
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        return new String(Base64.getEncoder().encode(signature.sign()));
    }

    public static boolean verify(String publicKey, String data, String sign) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, SignatureException {
        return verify(publicKey(publicKey), data, sign);
    }

    /**
     * 验签
     *
     * @param publicKey 公钥
     * @param data      原始字符串
     * @param sign      签名
     * @return 是否验签通过
     */
    public static boolean verify(PublicKey publicKey, String data, String sign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(SIGNATURE);
        signature.initVerify(publicKey);
        signature.update(data.getBytes());
        return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
    }

    /**
     * 分段加密解密
     *
     * @param cipher 加密器
     * @param input  原始数据
     * @return 分段加密或解密后的结果
     * @throws BadPaddingException       e
     * @throws IllegalBlockSizeException e
     */
    private static byte[] cipher(Cipher cipher, int block, byte[] input) throws BadPaddingException, IllegalBlockSizeException {
        int length = input.length;
        int offset = 0;
        byte[] bytes = new byte[0];
        byte[] cache;
        while (length - offset > 0) {
            if (length - offset > block) {
                cache = cipher.doFinal(input, offset, block);
                offset += block;
            } else {
                cache = cipher.doFinal(input, offset, length - offset);
                offset = length;
            }
            bytes = Arrays.copyOf(bytes, bytes.length + cache.length);
            System.arraycopy(cache, 0, bytes, bytes.length - cache.length, cache.length);
        }
        return bytes;
    }

}

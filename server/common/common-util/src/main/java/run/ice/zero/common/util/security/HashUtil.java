package run.ice.zero.common.util.security;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * @author DaoDao
 */
@Slf4j
public class HashUtil {

    /**
     * SHA-256 加密
     * 使用 jdk 原生加密算法，转化成十六进制字符串
     *
     * @param string String
     * @return String
     */
    public static String sha256(String string) {
        if (null == string) {
            return null;
        }
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(string.getBytes());
            byte[] bytes = digest.digest();
            hash = HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return hash;
    }

    public static String sha256(File file) {
        if (null == file) {
            return null;
        }
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        String hash = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[1024];
            int sizeRead = -1;
            while ((sizeRead = bis.read(buffer)) != -1) {
                digest.update(buffer, 0, sizeRead);
            }
            bis.close();
            byte[] bytes = digest.digest();
            hash = HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return hash;
    }

    public static String md5(String origin) {
        if (null == origin || origin.isEmpty()) {
            return origin;
        }
        byte[] bytes = origin.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            byte[] hashBytes = digest.digest();
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String md5(File file) {
        if (null == file) {
            return null;
        }
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        String hash = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int sizeRead = -1;
            while ((sizeRead = bis.read(buffer)) != -1) {
                digest.update(buffer, 0, sizeRead);
            }
            bis.close();
            byte[] bytes = digest.digest();
            hash = HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return hash;
    }

}

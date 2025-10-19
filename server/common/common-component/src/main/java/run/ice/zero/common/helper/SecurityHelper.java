package run.ice.zero.common.helper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.ice.zero.common.annotation.Cipher;
import run.ice.zero.common.annotation.Hash;
import run.ice.zero.common.annotation.Mask;
import run.ice.zero.common.config.CommonConfig;
import run.ice.zero.common.model.Serializer;
import run.ice.zero.common.util.security.AesUtil;
import run.ice.zero.common.util.security.HashUtil;
import run.ice.zero.common.util.security.MaskUtil;

import java.lang.reflect.Field;

@Slf4j
@Component
public class SecurityHelper {

    @Resource
    private CommonConfig commonConfig;

    public String aesEncrypt(String plains) {
        String aesKey = commonConfig.getAesKey();
        String aesIv = commonConfig.getAesIv();
        String cipher = null;
        try {
            cipher = AesUtil.encrypt(aesKey, aesIv, plains);
        } catch (Exception e) {
            log.error("AES encrypt error", e);
            // throw new RuntimeException(e);
        }
        return cipher;
    }

    public String aesDecrypt(String cipher) {
        String aesKey = commonConfig.getAesKey();
        String aesIv = commonConfig.getAesIv();
        String plains = null;
        try {
            plains = AesUtil.decrypt(aesKey, aesIv, cipher);
        } catch (Exception e) {
            log.error("AES decrypt error", e);
            // throw new RuntimeException(e);
        }
        return plains;
    }

    public <T extends Serializer> T encode(T t) {
        Class<? extends Serializer> clazz = t.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            Cipher cipher = field.getDeclaredAnnotation(Cipher.class);
            Hash hash = field.getDeclaredAnnotation(Hash.class);
            Mask mask = field.getDeclaredAnnotation(Mask.class);
            if (cipher == null && hash == null && mask == null) {
                continue;
            } else {
                field.setAccessible(true);
            }
            String plainFiledName;
            if (cipher != null) {
                String cipherFieldName = field.getName();
                String property = cipher.property();
                Cipher.Algorithm algorithm = cipher.algorithm();
                if (property.isEmpty()) {
                    /*
                     * 约定 cipherFieldName = plainFieldName + "Cipher"
                     */
                    plainFiledName = cipherFieldName.substring(0, cipherFieldName.length() - 6);
                } else {
                    plainFiledName = property;
                }
                /*
                 * 判断是否有对应的属性
                 */
                Field plainField = null;
                try {
                    plainField = clazz.getDeclaredField(plainFiledName);
                } catch (NoSuchFieldException e) {
                    log.error("Encode error", e);
                    // throw new RuntimeException(e);
                }
                if (plainField == null) {
                    continue;
                }
                plainField.setAccessible(true);
                String plainFieldValue = null;
                try {
                    Object o = plainField.get(t);
                    if (o != null) {
                        plainFieldValue = (String) o;
                    }
                } catch (IllegalAccessException e) {
                    log.error("Encode error", e);
                    // throw new RuntimeException(e);
                }
                if (plainFieldValue == null || plainFieldValue.isEmpty()) {
                    continue;
                }
                if (algorithm == null) {
                    algorithm = Cipher.Algorithm.AES;
                }
                if (algorithm == Cipher.Algorithm.AES) {
                    String cipherFieldValue = aesEncrypt(plainFieldValue);
                    try {
                        field.set(t, cipherFieldValue);
                    } catch (IllegalAccessException e) {
                        log.error("Encode error", e);
                        // throw new RuntimeException(e);
                    }
                }
            } else if (hash != null) {
                String hashFieldName = field.getName();
                String property = hash.property();
                Hash.Algorithm algorithm = hash.algorithm();
                if (property.isEmpty()) {
                    /*
                     * 约定 hashFieldName = plainFieldName + "Hash"
                     */
                    plainFiledName = hashFieldName.substring(0, hashFieldName.length() - 4);
                } else {
                    plainFiledName = property;
                }
                /*
                 * 判断是否有对应的属性
                 */
                Field plainField = null;
                try {
                    plainField = clazz.getDeclaredField(plainFiledName);
                } catch (NoSuchFieldException e) {
                    log.error("Encode error", e);
                    // throw new RuntimeException(e);
                }
                if (plainField == null) {
                    continue;
                }
                plainField.setAccessible(true);
                String plainFieldValue = null;
                try {
                    Object o = plainField.get(t);
                    if (o != null) {
                        plainFieldValue = (String) o;
                    }
                } catch (IllegalAccessException e) {
                    log.error("Encode error", e);
                    // throw new RuntimeException(e);
                }
                if (plainFieldValue == null || plainFieldValue.isEmpty()) {
                    continue;
                }
                if (algorithm == null) {
                    algorithm = Hash.Algorithm.SHA_256;
                }
                if (algorithm == Hash.Algorithm.SHA_256) {
                    String hashFieldValue = HashUtil.sha256(plainFieldValue);
                    try {
                        field.set(t, hashFieldValue);
                    } catch (IllegalAccessException e) {
                        log.error("Encode error", e);
                        // throw new RuntimeException(e);
                    }
                }
            } else if (mask != null) {
                String maskFieldName = field.getName();
                String property = mask.property();
                Mask.Type type = mask.type();
                if (property.isEmpty()) {
                    /*
                     * 约定 maskFieldName = plainFieldName + "Mask"
                     */
                    plainFiledName = maskFieldName.substring(0, maskFieldName.length() - 4);
                } else {
                    plainFiledName = property;
                }
                /*
                 * 判断是否有对应的属性
                 */
                Field plainField = null;
                try {
                    plainField = clazz.getDeclaredField(plainFiledName);
                } catch (NoSuchFieldException e) {
                    log.error("Encode error", e);
                    // throw new RuntimeException(e);
                }
                if (plainField == null) {
                    continue;
                }
                plainField.setAccessible(true);
                String plainFieldValue = null;
                try {
                    Object o = plainField.get(t);
                    if (o != null) {
                        plainFieldValue = (String) o;
                    }
                } catch (IllegalAccessException e) {
                    log.error("Encode error", e);
                    // throw new RuntimeException(e);
                }
                if (plainFieldValue == null || plainFieldValue.isEmpty()) {
                    continue;
                }
                if (type == null) {
                    type = Mask.Type.NONE;
                }
                String maskFieldValue = MaskUtil.mask(type, plainFieldValue);
                try {
                    field.set(t, maskFieldValue);
                } catch (IllegalAccessException e) {
                    log.error("Encode error", e);
                    // throw new RuntimeException(e);
                }
            }

        }
        return t;
    }

    public <T extends Serializer> T decode(T t) {
        Class<? extends Serializer> clazz = t.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            Cipher cipher = field.getDeclaredAnnotation(Cipher.class);
            if (cipher == null) {
                continue;
            } else {
                field.setAccessible(true);
            }
            String plainFiledName;
            String cipherFieldName = field.getName();
            String property = cipher.property();
            Cipher.Algorithm algorithm = cipher.algorithm();
            if (property.isEmpty()) {
                /*
                 * 约定 cipherFieldName = plainFieldName + "Cipher"
                 */
                plainFiledName = cipherFieldName.substring(0, cipherFieldName.length() - 6);
            } else {
                plainFiledName = property;
            }
            /*
             * 判断是否有对应的属性
             */
            Field plainField = null;
            try {
                plainField = clazz.getDeclaredField(plainFiledName);
            } catch (NoSuchFieldException e) {
                log.error("Encode error", e);
                // throw new RuntimeException(e);
            }
            if (plainField == null) {
                continue;
            }
            plainField.setAccessible(true);
            String cipherFieldValue = null;
            try {
                Object o = field.get(t);
                if (o != null) {
                    cipherFieldValue = (String) o;
                }
            } catch (IllegalAccessException e) {
                log.error("Encode error", e);
                // throw new RuntimeException(e);
            }
            if (cipherFieldValue == null || cipherFieldValue.isEmpty()) {
                continue;
            }
            if (algorithm == null) {
                algorithm = Cipher.Algorithm.AES;
            }
            if (algorithm == Cipher.Algorithm.AES) {
                String plainFieldValue = aesDecrypt(cipherFieldValue);
                try {
                    plainField.set(t, plainFieldValue);
                } catch (IllegalAccessException e) {
                    log.error("Encode error", e);
                    // throw new RuntimeException(e);
                }
            }
        }
        return t;
    }

}

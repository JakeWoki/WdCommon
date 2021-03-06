package cm.android.codec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AESCoder {

    private static final int KEY_SIZE = 256;

    public static final String KEY_ALGORITHM = "AES";

    public static final String C_AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";

    private AESCoder() {
    }

    public static SecretKey generateKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom sr = SecureUtil.getSecureRandom();
        sr.setSeed(seed);
        kgen.init(KEY_SIZE, sr); //256 bits or 128 bits,192bits
        SecretKey secretKey = kgen.generateKey();
        return secretKey;
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Do *not* seed secureRandom! Automatically seeded from system entropy
        final SecureRandom random = new SecureRandom();

        // Use the largest AES key length which is supported by the OS
        final KeyGenerator generator = KeyGenerator.getInstance(KEY_ALGORITHM);
        try {
            generator.init(KEY_SIZE, random);
        } catch (Exception e) {
            try {
                generator.init(192, random);
            } catch (Exception e1) {
                generator.init(128, random);
            }
        }

        return generator.generateKey();
        //return SecureUtil.encode(generator.generateKey().getEncoded());
    }

    public static SecretKey generateKey(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKey tmp = HashUtil.generateHash(password, salt);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), C_AES_CBC_PKCS5PADDING);
        return secret;
    }

    public static byte[] encryptBySeed(byte[] seed, byte[] iv, byte[] data) throws Exception {
        byte[] rawKey = generateKey(seed).getEncoded();
        byte[] result = encrypt(rawKey, iv, data);
        return result;
    }

    public static byte[] decryptBySeed(byte[] seed, byte[] iv, byte[] encrypted) throws Exception {
        byte[] rawKey = generateKey(seed).getEncoded();
        byte[] result = decrypt(rawKey, iv, encrypted);
        return result;
    }

    public static byte[] encrypt(byte[] key, byte[] iv, byte[] src) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, C_AES_CBC_PKCS5PADDING);
        return encrypt(skeySpec, iv, src);
    }

    public static byte[] decrypt(byte[] key, byte[] iv, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, C_AES_CBC_PKCS5PADDING);
        return decrypt(skeySpec, iv, encrypted);
    }

    public static byte[] encrypt(SecretKey secretKey, byte[] iv, byte[] src) throws Exception {
        IvParameterSpec ivSpec = SecureUtil.getIv(iv);

        Cipher cipher = Cipher.getInstance(C_AES_CBC_PKCS5PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(src);
        return encrypted;
    }

    public static byte[] decrypt(SecretKey secretKey, byte[] iv, byte[] encrypted)
            throws Exception {
        IvParameterSpec ivSpec = SecureUtil.getIv(iv);

        Cipher cipher = Cipher.getInstance(C_AES_CBC_PKCS5PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

}

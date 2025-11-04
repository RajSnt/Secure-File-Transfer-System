package Utils;

public final class Constants {
    public static final int RSA_KEY_SIZE = 2048;
    public static final int AES_KEY_SIZE = 256; // in bits
    public static final int IV_SIZE = 16; // bytes for AES-CBC
    public static final String AES_ALGORITHM = "AES";
    public static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    public static final int BUFFER_SIZE = 8192; // 8 KB
    public static final int SERVER_PORT = 5000;
    private Constants() {}
}

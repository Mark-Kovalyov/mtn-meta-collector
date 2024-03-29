package mayton.semantic.media;

import jcifs.util.MD4;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

import static java.lang.String.format;

public class P2PLinkProcessor {

    static Logger logger = LoggerFactory.getLogger(P2PLinkProcessor.class);

    private MessageDigest sha1digest = null;
    private MessageDigest md5digest = null;
    private MD4 md4digest = null;
    private CRC32 crc32 = null;

    private byte[] buf = new byte[16 * 1024 * 1024];

    private static P2PLinkProcessor instance = null;

    private P2PLinkProcessor() {
        md4digest = new MD4();
        crc32 = new CRC32();
        try {
            sha1digest = MessageDigest.getInstance("SHA1");
            md5digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
    }

    public static P2PLinkProcessor createInstance() {
        if (instance == null) {
            instance = new P2PLinkProcessor();
        }
        return instance;
    }

    public P2PLinks extractP2Plinks(File file) {
        try(InputStream is = new FileInputStream(file)) {
            int size = 0;
            sha1digest.reset();
            md4digest.reset();
            while ((size = is.read(buf)) >= 0) {
                sha1digest.update(buf, 0, size);
                md4digest.update(buf, 0, size);
                md5digest.update(buf, 0, size);
            }
            String md4string  = Base64.encodeBase64String(md4digest.digest());
            String sha1string = Hex.encodeHexString(sha1digest.digest());
            String md5string  = Hex.encodeHexString(md5digest.digest());

            P2PLinks res = new P2PLinks(md4string, md5string, sha1string, "", "");

            logger.debug("{}", res);
            return res;
        } catch (FileNotFoundException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        }
        return new P2PLinks("","","","","");
    }

}

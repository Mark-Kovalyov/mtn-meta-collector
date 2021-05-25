package mayton.semantic.media;

/**
 * https://ru.wikipedia.org/wiki/Magnet-%D1%81%D1%81%D1%8B%D0%BB%D0%BA%D0%B0
 */
public final class P2PLinks {

    public final String md4;
    public final String md5;
    public final String sha1;
    public final String tth;
    public final String aich;


    public P2PLinks(String md4, String md5, String sha1, String tth, String aich) {
        this.md4 = md4;
        this.md5 = md5;
        this.sha1 = sha1;
        this.tth = tth;
        this.aich = aich;
    }

    @Override
    public String toString() {
        return "P2PLinks{" +
                "md4='" + md4 + '\'' +
                ", md5='" + md5 + '\'' +
                ", sha1='" + sha1 + '\'' +
                ", tth='" + tth + '\'' +
                ", aich='" + aich + '\'' +
                '}';
    }
}

package mayton.semantic;

/**
 * https://ru.wikipedia.org/wiki/Magnet-%D1%81%D1%81%D1%8B%D0%BB%D0%BA%D0%B0
 */
public final class P2PLinks {

    public final String ed2k;
    public final String tigerTreeHash; // xt=urn:tree:tiger:[ TTH хеш  файла (Base32) ]
    public final String bitTorrentInfoHash; // xt=urn:btih:[HEX]
    public final String gnutella2;   //   xt=urn:md5:[Hex] (Gnutella2)
    public final String crc32; //  xt=urn:crc32:[Base10]

    public P2PLinks(String ed2k, String tigerTreeHash, String bitTorrentInfoHash, String gnutella2, String crc32) {
        this.ed2k = ed2k;
        this.tigerTreeHash = tigerTreeHash;
        this.bitTorrentInfoHash = bitTorrentInfoHash;
        this.gnutella2 = gnutella2;
        this.crc32 = crc32;
    }

    @Override
    public String toString() {
        return "P2PLinks{" +
                "ed2k='" + ed2k + '\'' +
                ", tigerTreeHash='" + tigerTreeHash + '\'' +
                ", bitTorrentInfoHash='" + bitTorrentInfoHash + '\'' +
                ", md5='" + gnutella2 + '\'' +
                ", crc32='" + crc32 + '\'' +
                '}';
    }
}

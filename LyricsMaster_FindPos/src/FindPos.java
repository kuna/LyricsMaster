import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;


public class FindPos {
	public static final int HASHSIZE = 0x28000;
	private byte[] filedata;
	private int len;
	private String hash;
	
	public FindPos(String path, String hash) {
		try {
			File f = new File(path);
			RandomAccessFile is = new RandomAccessFile(path, "r");
			len = (int)f.length();
			filedata = new byte[len];
			is.read(filedata, 0, len);
			this.hash = hash;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		int pos = 0;
		String MD5Hash = "";
		while (pos+HASHSIZE <= len) {
			MD5Hash = "";
			try {
				int size = HASHSIZE;
				MessageDigest m = MessageDigest.getInstance("MD5");
				m.update(filedata, pos, HASHSIZE);
				MD5Hash = new BigInteger(1, m.digest()).toString(16);
				while (MD5Hash.length() < 32) {
					MD5Hash = "0" + MD5Hash;
				}
				if (MD5Hash.compareToIgnoreCase(hash) == 0) {
					System.out.println(pos);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (pos % 1024 == 0) {
				System.out.println(Integer.toString((int)(pos/1024))+"kb");
			}
			pos++;
		}
	}
}

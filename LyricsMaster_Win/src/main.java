import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.kuna.lyricsmaster.lyrics.*;
import com.kuna.lyricsmaster.tag.TagModifier;

public class main {
	public static boolean writeLyrics2File = true;
	public static void main(String[] args) {
		BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
		LyricsData arrLyrics = new LyricsData();
		LyricsParser lp = new LyricsParser();
		
		try {
			System.out.println("Enter Title");
			String title = buf.readLine();
			
			System.out.println("Enter Artist");
			String artist = buf.readLine();
			
			if (title.length() == 0) {
				title = "Time is running out";
				artist = "Muse";
				
				// TEST
				//System.out.println("������ �Է��� �ּ���.");
				//return;
			}
			lp.SearchAlsongServer(title, artist);
			if (lp.parseLyricsResult(arrLyrics)) {
				arrLyrics.saveLRC( title + ".lrc" );
				System.out.println("���縦 ã�ҽ��ϴ�.");
			} else {
				System.out.println("���縦 ã�� ���߽��ϴ�.");
			}
			
			System.out.println("Enter Path to download lrc file");
			String path = buf.readLine();
			if (path.length() == 0) {
				path = "C:\\Users\\Public\\Music\\Sample Music"; // TEST
				//System.out.println("��θ� �Է��� �ּ���.");
				//return;
			}
			ArrayList<File> arrFile = new ArrayList<File>();
			getFileList(path, arrFile);
			
			MusicMD5Hash mMD5Hash = new MusicMD5Hash();
			for (File f : arrFile) {
				mMD5Hash.LoadFile(f.getAbsolutePath());

				System.out.println(f.getAbsolutePath());
				System.out.println(mMD5Hash.MD5Hash);

				lp.RequestAlsongServer(mMD5Hash.MD5Hash);
				if (lp.parseLyricsResult(arrLyrics)) {
					arrLyrics.saveLRC( LyricsData.convertExtension2LRC(f.getAbsolutePath()) );
					System.out.println("���� ���� �Ϸ�.");
					
					if (writeLyrics2File) {
						TagModifier.writeUSLTTag(f.getAbsolutePath(), arrLyrics.getOnlyLyrics());
						System.out.println("���Ͽ� �±� �Ϸ�.");
					}
				} else {
					System.out.println("���縦 ã�� ���߽��ϴ�.");
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void getFileList(String dir, ArrayList<File> f) {
	    File directory = new File(dir);

	    // get all the files from a directory
	    // before adding, check extension
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile() && isMusicExtension(file)) {
	            f.add(file);
	        } else if (file.isDirectory()) {
	        	getFileList(file.getAbsolutePath(), f);
	        }
	    }
	}
	
	private static boolean isMusicExtension(File f) {
		if (f.getName().toLowerCase().endsWith(".mp3")) {
			return true;
		}

		if (f.getName().toLowerCase().endsWith(".flac")) {
			return true;
		}

		if (f.getName().toLowerCase().endsWith(".wav")) {
			return true;
		}

		if (f.getName().toLowerCase().endsWith(".wma")) {
			return true;
		}
		
		return false;
	}
}

package com.kuna.lyricsmaster.lyrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.http.message.BufferedHeader;

// https://github.com/dlunch/foo_alsong_lyric/blob/master/src/foo_alsong_lyric/LyricSourceAlsong.cpp
// bit conversion: http://emflant.tistory.com/133

public class MusicMD5Hash {
	public static final int FILE_MP3v1 = 1;
	public static final int FILE_MP3v2 = 2;
	public static final int FILE_WMA = 3;
	public static final int FILE_OGG = 4;
	public static final int FILE_FLAC = 5;
	public static final int FILE_WAV = 6;
	public static final int FILE_UNSUPPORT = -1;
	public static final int FILE_UNKNOWN = 0;
	
	public static final int MAXSIGSIZE = 128;
	public static final int HASHSIZE = 0x28000;//163840;
	
	private static final char WMASIG[] = new char[] {0x30, 0x26, 0xB2, 0x75, 0x8E, 0x66, 0xCF, 0x11, 0xA6, 0xD9, 0x00, 0xAA, 0x00, 0x62, 0xCE, 0x6C}; 
	
	public int FileType;
	public String MD5Hash;
	public long FileSize;
	private long RawdataSize;
	private int RawdataPos;
	
	public boolean LoadFile(String path) {
		try {
			File f = new File(path);
			FileSize = f.length();
			RawdataSize = FileSize;
			
			RandomAccessFile rf = new RandomAccessFile(f, "r");
			
			RawdataPos = 0;
			FileType = FILE_UNKNOWN;
			MD5Hash = "";
			DetectFile(rf);
			RawdataSize -= RawdataPos;
			
			if (FileType > 0) {
				if (!GetMD5Hash(rf)) {
					FileType = FILE_UNSUPPORT;
				}
			}
			
			rf.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private void DetectFile(RandomAccessFile rf) {
		try {
			if (FileSize < MAXSIGSIZE)
				return ;
			
			byte signature[] = new byte[MAXSIGSIZE];
			String sig_str;
			
			// detect ID3v1 first
			rf.seek(FileSize-128);
			rf.read(signature, 0, 3);
			sig_str = new String(signature);
			if (sig_str.startsWith("TAG")) {
				FileType = FILE_MP3v1;
				RawdataSize -= 128;
			}

			// detect other signature
			rf.seek(0);
			rf.read(signature, 0, MAXSIGSIZE);
			sig_str = new String(signature);
			
			if (sig_str.startsWith("ID3")) {
				FileType = FILE_MP3v2;
			} else if (sig_str.startsWith("OggS")) {
				FileType = FILE_OGG;
			} else if (sig_str.startsWith("RIFF")) {
				FileType = FILE_WAV;
			} else if (sig_str.startsWith("fLaC")) {
				FileType = FILE_FLAC;
			} else if (cmpByteArrWithChar(signature, WMASIG)) {
				FileType = FILE_WMA;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean GetMD5Hash(RandomAccessFile rf) {
		switch (FileType) {
		case FILE_MP3v2:
			return GetMP3MD5Hash(rf);	// default processing for ID3v1
		case FILE_WMA:
			return GetWMAMD5Hash(rf);
		case FILE_OGG:
			return GetOGGMD5Hash(rf);
		case FILE_FLAC:
			return GetFLACMD5Hash(rf);
		case FILE_WAV:
			return GetWAVMD5Hash(rf);
		case FILE_UNSUPPORT:
			return GetMD5HashFromOffset(rf, 0);
		}
		
		// for unknown case
		return GetMD5HashFromOffset(rf, 0);
	}
	
	private boolean GetMP3MD5Hash(RandomAccessFile rf) {
		try {
			// there can be multiple ID3 frame.
			while (true) {
				byte buffer[] = new byte[8];
				rf.seek(RawdataPos);
				rf.read(buffer, 0, 3);
				if (!new String(buffer).substring(0,3).equals("ID3")) {
					break;
				}
				
				rf.seek(RawdataPos + 3+2+1);
				rf.read(buffer, 0, 4);
				buffer[0] = (byte) ((buffer[0] << 1) >> 1);
				buffer[1] = (byte) ((buffer[1] << 1) >> 1);
				buffer[2] = (byte) ((buffer[2] << 1) >> 1);
				buffer[3] = (byte) ((buffer[3] << 1) >> 1);
				RawdataPos = (buffer[3]&0xff) + ((buffer[2]&0xff) << 7) + ((buffer[1]&0xff) << 14) + ((buffer[0]&0xff) << 21);
				RawdataPos += 10;
			}
			
			// search to MP3 Header
			rf.seek(RawdataPos);
			for (;;RawdataPos++) {
				if (rf.read() == 0xFF) break;
			}
			
			//System.out.println(RawdataPos);
			GetMD5HashFromOffset(rf, RawdataPos);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean GetWMAMD5Hash(RandomAccessFile rf) {
		return false;	// unsupported
	}
	
	private boolean GetOGGMD5Hash(RandomAccessFile rf) {
		// total header size is given by = number_page_segments+27 bytes
		// total page size: page_size = header_size+sum(lacing_values: 1....number_page_segments)
		
		/**
		 * even tag is included as DATA!
		 * 
		
		try {
			byte buf[] = new byte[128];
			fis.read(buf, 27, 1);
			int segmentCnt = (int) (buf[0] & 0xFF);
			int segmentSize = 0;
			for (int i=0; i<segmentCnt; i++) {
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;*/
		
		// signatures
		char vorbis[] = new char[] {0x05, 0x76, 0x6F, 0x72, 0x62, 0x69, 0x73};
		char BCV[] = new char[] {0x42, 0x43, 0x56};
		byte buf[] = new byte[256];
		
		int ind = 0;
		while (ind < FileSize) {
			try {
				if (cmpByteArrWithChar(buf, vorbis)) {
					ind += 7+1;
					
					if (cmpByteArrWithChar(buf, BCV)) {
						RawdataPos = ind;
						GetMD5HashFromOffset(rf, RawdataPos);
						return true;
					}
				}
			} catch (Exception e) {
				return false;
			}
			
			ind ++;
		}
		
		return false;
	}
	
	private boolean GetFLACMD5Hash(RandomAccessFile rf) {
		/*
		char sig[] = new char[] {0xFF, 0xF8, 0xCE, 0xAC};
		byte buf[] = new byte[256];
		
		int ind = 0;
		while (ind < FileSize) {
			try {
				if (cmpByteArrWithChar(buf, sig)) {
					RawdataPos = ind;
					GetMD5HashFromOffset(rf, RawdataPos);
					return true;
				}
			} catch (Exception e) {
				return false;
			}
			
			ind ++;
		}*/

		RawdataPos = 0;
		GetMD5HashFromOffset(rf, RawdataPos);
		return true;
	}
	
	private boolean GetWAVMD5Hash(RandomAccessFile rf) {
		/*try {
			byte chunkID[] = new byte[256];
			byte chunkSize[] = new byte[256];
			int ind = 12;
			while (ind < FileSize) {
				rf.seek(ind);
				rf.read(chunkID, 0, 4);
				ind += 4;
				rf.read(chunkSize, 0, 4);
				ind += 4;
				String tag = new String(chunkID);
				if (tag.substring(0, 4).compareTo("data") == 0) {
					RawdataPos = ind;
					GetMD5HashFromOffset(rf, RawdataPos);
					return true;
				} else {
					int chunkSize_int = (chunkSize[3]<<24)&0xff000000|(chunkSize[2]<<16)&0xff0000|
							(chunkSize[1]<<8)&0xff00|(chunkSize[0]<<0)&0xff; // LE
					ind += chunkSize_int;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		RawdataPos = 0;
		GetMD5HashFromOffset(rf, RawdataPos);
		return true;
	}
	
	private boolean GetMD5HashFromOffset(RandomAccessFile rf, int offset) {
		if (FileSize < offset+HASHSIZE) {
			return false;
		} else {
			try {
				int size = HASHSIZE;
				if (RawdataSize-offset < size) size = (int) (RawdataSize-offset);
				byte pureData[] = new byte[size];
				rf.seek(offset);
				rf.read(pureData, 0, size);
				MessageDigest m = MessageDigest.getInstance("MD5");
				m.update(pureData);
				MD5Hash = new BigInteger(1, m.digest()).toString(16);
				while (MD5Hash.length() < 32) {
					MD5Hash = "0" + MD5Hash;
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
		}
	}
	
	private boolean cmpByteArrWithChar(byte[] a, char[] b) {
		if (a.length < b.length) return false;
		
		for (int i=0; i<b.length; i++) {
			if ((char)(a[i] & 0xFF) != b[i])
				return false;
		}
		return true;
	}
}

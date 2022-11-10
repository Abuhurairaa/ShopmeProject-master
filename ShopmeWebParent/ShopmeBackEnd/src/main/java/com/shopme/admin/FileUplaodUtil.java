package com.shopme.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;



public class FileUplaodUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUplaodUtil.class);
	
	public static void saveFile(String uplaodDir, String fileName,
			MultipartFile multipartFile) throws IOException {
		Path uplaodPath = Paths.get(uplaodDir);
		
		if (!Files.exists(uplaodPath)) {
			Files.createDirectories(uplaodPath);
		}
		
		try(InputStream inputStream = multipartFile.getInputStream()){
			Path filePath = uplaodPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		}catch (IOException e) {
			throw new IOException("Could not save file: "+ fileName, e);
		}
	}
	
	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);
		
		try {
			Files.list(dirPath).forEach(file ->{
				if(!Files.isDirectory(file)) {
					try {
					Files.delete(file);
					}catch (IOException e) {
						
						LOGGER.error("Could not save file: "+ file);
						//System.out.println("Could not delete file: "+ file);
					}
				}
			});
		}catch (IOException e) {
			LOGGER.error("Could not list directory: "+ dirPath);
			//System.out.println("Could not list directory: "+ dirPath);
		}
	}

	public static void removeDir(String dir) {
		cleanDir(dir);
		
		try {
			Files.delete(Paths.get(dir));
		} catch (IOException e) {
			LOGGER.error("Could not remove directory: "+dir);
		}
		
	}

}

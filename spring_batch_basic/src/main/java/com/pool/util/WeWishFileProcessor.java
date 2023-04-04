package com.pool.util;

import java.io.IOException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.pool.model.exception.NoDataFoundException;

@Component
public class WeWishFileProcessor {

	public String byteToBase64Converter(MultipartFile fileByteData) {

		String encodedString = "";
		try {
			byte[] fileContent = fileByteData.getBytes();
			if (fileContent.length > 0) {
				encodedString =Base64.encodeBase64String(fileContent);
			} else {
				throw new NoDataFoundException("FILE IS NOT UPLOADED");
			}

		} catch (IOException e) {
			throw new NoDataFoundException("FILE FORMATE ISSUE");
		}
		return encodedString;
	}
}

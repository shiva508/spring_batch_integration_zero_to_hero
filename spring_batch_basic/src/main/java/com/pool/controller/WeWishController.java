package com.pool.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pool.domin.Laptop;
import com.pool.domin.WeWish;
import com.pool.service.WeWishBatchService;
import com.pool.service.WeWishService;
import com.pool.util.WeWishFileProcessor;

@RestController
@RequestMapping("/wish")
public class WeWishController {

	@Autowired
	private WeWishFileProcessor weWishFileProcessor;

	@Autowired
	private WeWishService weWishService;

	@Autowired
	private WeWishBatchService weWishBatchService;

	@PostMapping(value = "/createwish")
	public ResponseEntity<?> createWeWish(@ModelAttribute WeWish weWish,
			@RequestParam(value = "file", required = false) MultipartFile file) {

		// System.out.println(file.getOriginalFilename());
		// String encodedString = weWishFileProcessor.byteToBase64Converter(file);
		// System.out.println(encodedString);
		weWish = weWishService.createWeWish(weWish);
		return new ResponseEntity<>(weWish, HttpStatus.CREATED);
	}

	@PostMapping(value = "/createwishrest")
	public ResponseEntity<?> createWeWishRest(@RequestBody WeWish weWish) {

		// System.out.println(file.getOriginalFilename());
		// String encodedString = weWishFileProcessor.byteToBase64Converter(file);
		// System.out.println(encodedString);
		weWish = weWishService.createWeWish(weWish);
		return new ResponseEntity<>(weWish, HttpStatus.CREATED);
	}

	@GetMapping("/allwishes")
	public ResponseEntity<?> getWishList() {
		List<WeWish> weWishs = weWishService.getAllWeWishes();
		return new ResponseEntity<>(weWishs, HttpStatus.CREATED);
	}

	@PostMapping(value = "/records", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public WeWish records(@RequestBody WeWish records) {
		return records;
	}

}

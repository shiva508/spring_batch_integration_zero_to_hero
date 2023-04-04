package com.pool.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pool.domin.WeWish;
import com.pool.domin.WishFriend;
import com.pool.modal.CommonResponse;
import com.pool.service.WeWishBatchService;
import com.pool.service.WishFriendService;
import com.pool.util.WeWishFileProcessor;

@RestController
@RequestMapping("/wishfriend")
public class WishAFriendController {

	@Autowired
	private WishFriendService wishFriendService;

	@Autowired
	private WeWishFileProcessor weWishFileProcessor;

	@Autowired
	private WeWishBatchService weWishBatchService;

	@PostMapping("/createwishfriend")
	public ResponseEntity<?> wishAFriend(@ModelAttribute WishFriend wishFriend,
			@RequestParam("wishImages") MultipartFile wishIamges) {
		String imagebase64 = weWishFileProcessor.byteToBase64Converter(wishIamges);
		wishFriend.setWisherFriendMemory(imagebase64);
		;
		wishFriend = wishFriendService.createWishFriend(wishFriend);
		return new ResponseEntity<>(wishFriend, HttpStatus.CREATED);
	}

	@PostMapping("/createwishfriendrest")
	public ResponseEntity<?> wishAFriendRest(@RequestBody WishFriend wishFriend) {
		wishFriend = wishFriendService.createWishFriend(wishFriend);
		List<WishFriend> wishFriends = wishFriendService.getfriendswishes(wishFriend.getWeWishId());
		return new ResponseEntity<>(wishFriends, HttpStatus.CREATED);
	}

	@PutMapping("/updatewishfriend")
	public ResponseEntity<?> updateWishFriend(WishFriend wishFriend) {
		WishFriend updatedWishFriend = wishFriendService.updateWishFriend(wishFriend);
		return new ResponseEntity<>(updatedWishFriend, HttpStatus.CREATED);
	}

	@GetMapping("/getwishfriendbyid/{wishFriendId}")
	public ResponseEntity<?> getWishFriendNameById(Long wishFriendId) {
		WishFriend wishFriend = wishFriendService.getWishFriendNameById(wishFriendId);
		return new ResponseEntity<>(wishFriend, HttpStatus.CREATED);
	}

	@GetMapping("/getallwishfriends")
	public ResponseEntity<?> getAllWishFriends() {
		List<WishFriend> wishFriends = wishFriendService.getAllWishFriends();
		return new ResponseEntity<>(wishFriends, HttpStatus.CREATED);
	}

	@DeleteMapping("/deletebyid/{wishFriendId}")
	public ResponseEntity<?> deleteWishFriendById(Long wishFriendId) {
		CommonResponse commonResponse = wishFriendService.deleteWishFriendById(wishFriendId);
		return new ResponseEntity<>(commonResponse, HttpStatus.OK);
	}

	@GetMapping("/getfriendswishes/{weWishId}")
	public ResponseEntity<?> getfriendswishes(@PathVariable("weWishId") Long weWishId) {
		List<WishFriend> wishFriend = wishFriendService.getfriendswishes(weWishId);
		return new ResponseEntity<>(wishFriend, HttpStatus.CREATED);
	}

	@PostMapping("/batchprocessing")
	public String batchData(@RequestBody List<WeWish> weWishs) {
		/* weWishBatchService.testBatch(weWishs); */
		return "SUCCESS";
	}
}

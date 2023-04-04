package com.pool.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pool.domin.WishFriend;
import com.pool.modal.CommonResponse;
import com.pool.repository.WishFriendRepository;

@Service
public class WishFriendServiceImpl implements WishFriendService {
	@Autowired
	private WishFriendRepository wishFriendRepository;

	@Override
	@Transactional
	public WishFriend createWishFriend(WishFriend wishFriend) {
		return wishFriendRepository.save(wishFriend);
	}

	@Override
	@Transactional
	public WishFriend updateWishFriend(WishFriend wishFriend) {
		return wishFriendRepository.save(wishFriend);
	}

	@Override
	@Transactional
	public WishFriend getWishFriendNameById(Long wishFriendId) {
		Optional<WishFriend>optionalWishFriend=wishFriendRepository.findById(wishFriendId);
		return null;
	}

	@Override
	@Transactional
	public List<WishFriend> getAllWishFriends() {
		return wishFriendRepository.findAll();
	}

	@Override
	@Transactional
	public CommonResponse deleteWishFriendById(Long wishFriendId) {
		Optional<WishFriend>optionalWishFriend=wishFriendRepository.findById(wishFriendId);
		return null;
	}

	@Override
	@Transactional
	public List<WishFriend> getfriendswishes(Long weWishId) {
		List<WishFriend> wishFriends=wishFriendRepository.findByweWishId(weWishId);
		return wishFriends;
	}

}

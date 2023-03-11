package com.pool.service;

import java.util.List;

import com.pool.domin.WishFriend;
import com.pool.modal.CommonResponse;

public interface WishFriendService {
	public WishFriend createWishFriend(WishFriend wishFriend);

	public WishFriend updateWishFriend(WishFriend wishFriend);

	public WishFriend getWishFriendNameById(Long wishFriendId);

	public List<WishFriend> getAllWishFriends();

	public CommonResponse deleteWishFriendById(Long wishFriendId);

	public List<WishFriend> getfriendswishes(Long weWishId);
}

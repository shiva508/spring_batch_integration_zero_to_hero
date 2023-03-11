package com.pool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pool.domin.WishFriend;

@Repository
public interface WishFriendRepository extends JpaRepository<WishFriend, Long> {

	public List<WishFriend> findByweWishId(Long weWishId);
}

package com.pool.service;

import java.util.List;
import com.pool.domin.WeWish;
import com.pool.modal.CommonResponse;

public interface WeWishService {
	public WeWish createWeWish(WeWish weWish);
	public WeWish updateWeWish(WeWish weWish);
	public WeWish getWeWishNameById(Long weWishId);
	public List<WeWish> getAllWeWishes();
	public CommonResponse deleteWeWishById(Long weWishId);
}

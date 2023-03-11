package com.pool.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pool.configuration.exception.NoRecardsFoundException;
import com.pool.domin.WeWish;
import com.pool.modal.CommonResponse;
import com.pool.model.exception.NoDataFoundException;
import com.pool.repository.WeWishRepository;

@Service
public class WeWishServiceImpl implements WeWishService {
	
	@Autowired
	private WeWishRepository weWishRepository;

	@Override
	@Transactional
	public WeWish createWeWish(WeWish weWish) {
		return weWishRepository.save(weWish);
	}

	@Override
	@Transactional
	public WeWish updateWeWish(WeWish weWish) {
		return weWishRepository.save(weWish);
	}

	@Override
	@Transactional
	public WeWish getWeWishNameById(Long weWishId) {
	Optional<WeWish> optionalWeWish=weWishRepository.findById(weWishId);
	if(optionalWeWish.isPresent()) {
		return optionalWeWish.get();
	}else {
		throw new NoRecardsFoundException(" No wish found with ID:"+weWishId);
	}
	}

	@Override
	@Transactional
	public List<WeWish> getAllWeWishes() {
		List<WeWish> weWishs=weWishRepository.findAll();
		if(null !=weWishs && weWishs.size()>0) {
			return weWishs;
		}else {
			throw new NoDataFoundException("NO DATA FOUND");
		}
	}

	@Override
	@Transactional
	public CommonResponse deleteWeWishById(Long weWishId) {
		Optional<WeWish> optionalWeWish=weWishRepository.findById(weWishId);
		if(optionalWeWish.isPresent()) {
			weWishRepository.deleteById(weWishId);
			return new CommonResponse();
		}else {
			throw new NoRecardsFoundException(" No wish found with ID:"+weWishId);
		}
	}

}

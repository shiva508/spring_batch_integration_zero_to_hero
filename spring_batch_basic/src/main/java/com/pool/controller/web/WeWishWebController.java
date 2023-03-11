package com.pool.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.pool.domin.WeWish;
import com.pool.service.WeWishService;

@Controller
public class WeWishWebController {
	@Autowired
	private WeWishService weWishService;

	@GetMapping("/")
	public String wishHomePage(Model model) {
		return "index"; // view
	}

	@GetMapping("/wewish")
	public String createWishPage() {
		return "createwish";
	}

	@GetMapping("/wewish/{weWishId}")
	public String wishPageWithId(@PathVariable("weWishId") Long weWishId, Model model) {
		WeWish weWish = weWishService.getWeWishNameById(weWishId);
		model.addAttribute("weWishId", weWishId);
		model.addAttribute("weWish", weWish);
		return "wewishfriend";
	}
	@GetMapping("/user508")
	public String wishHomePage508(Model model) {
		return "408"; // view
	}
}

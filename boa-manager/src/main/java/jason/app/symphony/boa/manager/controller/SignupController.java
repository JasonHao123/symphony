package jason.app.symphony.boa.manager.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jason.app.symphony.boa.manager.support.AjaxUtils;
import jason.app.symphony.boa.manager.support.MessageHelper;
import jason.app.symphony.commons.http.model.SymphonyUser;
import jason.app.symphony.security.service.impl.CustomUserDetailsService;

@Controller
class SignupController {

	private static final String SIGNUP_VIEW_NAME = "signup/signup";

	@Autowired
	private CustomUserDetailsService accountService;

	@GetMapping("signup")
	String signup(Model model, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
		model.addAttribute(new SignupForm());
		if (AjaxUtils.isAjaxRequest(requestedWith)) {
			return SIGNUP_VIEW_NAME.concat(" :: signupForm");
		}
		return SIGNUP_VIEW_NAME;
	}

	@PostMapping("signup")
	public String signup(@Valid @ModelAttribute SignupForm signupForm, Errors errors, RedirectAttributes ra) throws Exception {
		if (errors.hasErrors()) {
			return SIGNUP_VIEW_NAME;
		}
		SymphonyUser account = accountService.createUser(signupForm.getEmail(),signupForm.getPassword());
		accountService.signin(signupForm.getEmail(),signupForm.getPassword());
        // see /WEB-INF/i18n/messages.properties and /WEB-INF/views/homeSignedIn.html
        MessageHelper.addSuccessAttribute(ra, "signup.success");
		return "redirect:/";
	}
}

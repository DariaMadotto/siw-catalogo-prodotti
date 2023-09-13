package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.CredentialsValidator;
import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.controller.validator.UserValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.Supplier;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.SupplierRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class GlobalController {
	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SupplierRepository supplierRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private CredentialsValidator credentialsValidator;

	@Autowired
	private ProductService productService;

	@Autowired
	private ReviewValidator reviewValidator;



	@GetMapping("/")
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = null;
		Credentials credentials = null;
		if(!(authentication instanceof AnonymousAuthenticationToken)){
			userDetails = (UserDetails)authentication.getPrincipal();
			credentials = this.credentialsService.getCredentials(userDetails.getUsername());
		}
		if(credentials != null && credentials.getRole().equals(Credentials.ADMIN_ROLE)) return "admin/indexAdmin.html";

		/*model.addAttribute("userDetails", userDetails);*/
		model.addAttribute("products", this.productRepository.findAll());
		return "index.html";
	}
	@GetMapping("/index")
	public String index2(Model model){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = null;
		Credentials credentials = null;
		if(!(authentication instanceof AnonymousAuthenticationToken)){
			userDetails = (UserDetails)authentication.getPrincipal();
			credentials = credentialsService.getCredentials(userDetails.getUsername());
		}
		if(credentials != null && credentials.getRole().equals(Credentials.ADMIN_ROLE)) return "admin/indexAdmin.html";

		model.addAttribute("userDetails", userDetails);
		model.addAttribute("products", this.productRepository.findAll());
		return "index.html";
	}

	@GetMapping(value = "/login")
	public String showLoginForm (Model model) {
		return "formLogin.html";
	}

	@GetMapping(value = "/register")
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegister.html";
	}

	/* ridondante, ma viene usata anche per la admin dashboard */
//	@GetMapping("/products")
//	public String products(Model model){
//
//		model.addAttribute("products", this.productRepository.findAll());
//		return "index.html";
//	}
	
	@GetMapping("/products")
	public String products(Model model){

		model.addAttribute("products", this.productRepository.findAll());
		return "products.html";
	}

	@GetMapping("/suppliers")
	public String suppliers(Model model){

		model.addAttribute("suppliers", this.supplierRepository.findAll());
		return "suppliers.html";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user,
			BindingResult userBindingResult, @Valid
			@ModelAttribute("credentials") Credentials credentials,
			BindingResult credentialsBindingResult,
			Model model) {
		this.userValidator.validate(user,userBindingResult);
		this.credentialsValidator.validate(credentials, credentialsBindingResult);                        
		if(!userBindingResult.hasErrors() && ! credentialsBindingResult.hasErrors()) {
			credentials.setUser(user);
			credentialsService.saveCredentials(credentials);
			userService.saveUser(user);
			model.addAttribute("user", user);
			return "formLogin.html";
		}
		return "formRegister.html";
	}

	@GetMapping("/supplier/{id}")
	public String supplier(@PathVariable("id") Long id, Model model){

		model.addAttribute("userDetails", this.userService.getUserDetails());

		Supplier supplier = this.supplierRepository.findById(id).get();
//		Image profilePic = supplier.getProfilePicture(); //è una string rappresentante l'immagine in base64
		model.addAttribute("supplier", this.supplierRepository.findById(id).get());
//		model.addAttribute("profilePic", profilePic);

		return "supplier.html";
	}

	@GetMapping("/product/{id}")
	public String product(@PathVariable("id") Long id, Model model) {

		UserDetails userDetails = this.userService.getUserDetails();
		model.addAttribute("userDetails", userDetails);

		Product product = this.productRepository.findById(id).get();
//		Image image = product.getImage();

		model.addAttribute("product", product);
//		model.addAttribute("image", image);

		/* Gestione della review */
		if (userDetails != null){
			if(this.credentialsService.getCredentials(userDetails.getUsername()) !=null){
				model.addAttribute("review", new Review());
			}
		}

		if(userDetails != null && this.credentialsService.getCredentials(userDetails.getUsername()).getRole().equals(Credentials.ADMIN_ROLE)){
			model.addAttribute("admin", true);
		}
		return "product.html";
	}

	@PostMapping("/user/review/{productId}")
	public String addReview(Model model, @Valid @ModelAttribute("review") Review review, BindingResult bindingResult, @PathVariable("productId") Long id){
		this.reviewValidator.validate(review,bindingResult);
		Product product = this.productRepository.findById(id).get();
		String username = this.userService.getUserDetails().getUsername();

		if(!bindingResult.hasErrors() && !this.productService.hasReviewFromAuthor(id, username)){
			if(this.userService.getUserDetails() != null && !product.getReviews().contains(review)){
				review.setAuthor(username);
				this.reviewRepository.save(review);
				product.getReviews().add(review);
			}
		}
		this.productRepository.save(product);

		if(this.userService.getUserDetails() != null && !product.getReviews().contains(review)){
			if(!this.productService.hasReviewFromAuthor(id, username)){
				this.reviewRepository.save(review);
				product.getReviews().add(review);
			}
			else{
				model.addAttribute("reviewError", "Already Reviewed!");
			}

		}
		this.productRepository.save(product);

		model.addAttribute("product", product);
//		model.addAttribute("image", product.getImage());

		if(this.credentialsService.getCredentials(username).getRole().equals(Credentials.ADMIN_ROLE)){
			model.addAttribute("admin", true);
		}
		return "product.html";
	}

	@GetMapping("/admin/deleteReview/{productId}/{reviewId}")
	public String removeReview(Model model, @PathVariable("productId") Long productId,@PathVariable("reviewId") Long reviewId){
		Product product = this.productRepository.findById(productId).get();
		Review review = this.reviewRepository.findById(reviewId).get();
		UserDetails userDetails = this.userService.getUserDetails();

		product.getReviews().remove(review);
		this.reviewRepository.delete(review);
		this.productRepository.save(product);

		model.addAttribute("product", product);
//		model.addAttribute("image", product.getImage());

		if (userDetails != null){
			if(this.credentialsService.getCredentials(userDetails.getUsername()) !=null ){
				model.addAttribute("review", new Review());
			}
			if(this.productService.hasReviewFromAuthor(productId, userDetails.getUsername())){
				model.addAttribute("reviewError", "You have already reviewed this product.");
			}

		}
		return "product.html";
	}
}

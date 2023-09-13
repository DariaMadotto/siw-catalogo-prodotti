package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ProductValidator;
import it.uniroma3.siw.controller.validator.SupplierValidator;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Supplier;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.SupplierRepository;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class AdminController {
	
//	 @Autowired
//	 private ImageRepository imageRepository;
	 @Autowired
	 private ProductRepository productRepository;
	 @Autowired
	 private SupplierRepository supplierRepository;
	 @Autowired
	 private ProductValidator productValidator;
	 @Autowired
	 private SupplierValidator supplierValidator;
	 @Autowired
	 private ProductService productService;
	 @Autowired
	 private UserService userService;

	 @GetMapping("/admin/formNewProduct")
	 public String newProduct(Model model){
		 model.addAttribute("product",new Product());
		 return "/admin/formNewProduct.html";
	 }
	 
	 @PostMapping("/admin/uploadProduct")
	 public String newProduct(Model model, @Valid @ModelAttribute("product") Product product, BindingResult bindingResult){    //, @RequestParam("file") MultipartFile image)   throws IOException   
		 this.productValidator.validate(product,bindingResult);
		 if(!bindingResult.hasErrors()){
			 this.productService.createProduct(product);  //, image)
			 model.addAttribute("product", product);
//			 model.addAttribute("image", product.getImage());
			 model.addAttribute("userDetails", this.userService.getUserDetails());
			 return "product.html";
		 } else {
			 return "/admin/formNewProduct.html";
		 }
	 }
	 
	  @GetMapping("/admin/formNewSupplier")
	    public String formNewSupplier(Model model){
	        model.addAttribute("supplier",new Supplier());
	        return "/admin/formNewSupplier.html";
	    }
	  
	  @PostMapping("/admin/supplier")
	    public String newSupplier(Model model,@Valid @ModelAttribute("supplier") Supplier supplier, BindingResult bindingResult) {  //, @RequestParam("file") MultipartFile image)  throws  IOException
	        this.supplierValidator.validate(supplier, bindingResult);
	        if(!bindingResult.hasErrors()){
//	            Image pic = new Image(image.getBytes());
//	            this.imageRepository.save(pic);
//	            supplier.setProfilePicture(pic);
	            this.supplierRepository.save(supplier);

	            model.addAttribute("supplier", supplier);
//	            model.addAttribute("profilePic", pic );
	            model.addAttribute("userDetails", this.userService.getUserDetails());
	            return "supplier.html";
	        }
	        else {
	            return "/admin/formNewSupplier.html";
	        }
	    }
	  
	  @GetMapping("/admin/manageProducts")
	    public String manageProducts(Model model){
	        model.addAttribute("products", this.productRepository.findAll());
	        return "/admin/manageProducts.html";
	    }
	  

	    @Transactional
	    @GetMapping("/admin/formUpdateProduct/{id}")
	    public String formUpdateProduct(@PathVariable("id") Long id, Model model){
	        model.addAttribute("product", this.productRepository.findById(id).get());
	        return "/admin/formUpdateProduct.html";
	    }
	    
//metodi director
	    
	    //funzione ausiliaria
	    @Transactional
	    public Set<Supplier> suppliersToAdd(Long productId){
	        Set<Supplier> suppliersToAdd= new HashSet<Supplier>();
	        suppliersToAdd = this.supplierRepository.getByProductsNotContains(this.productRepository.findById(productId).get());
	        return suppliersToAdd;
	    }
	    
	    @Transactional
	    @GetMapping("/admin/updateSuppliersOnProduct/{id}")
	    public String updateSuppliers(@PathVariable("id") Long id,Model model ){

	        Set<Supplier> suppliersToAdd = this.suppliersToAdd(id);
	        model.addAttribute("product", this.productRepository.findById(id).get());
	        model.addAttribute("suppliersToAdd", suppliersToAdd);

	        return "/admin/suppliersToAdd.html";
	    }
	    
	    @Transactional
	    @GetMapping("/admin/addSupplierToProduct/{supplierId}/{productId}")
	    public String addSupplierToProduct(@PathVariable("supplierId") Long supplierId, @PathVariable("productId") Long productId, Model model){
	        Product product = this.productRepository.findById(productId).get();
	        this.productService.setSupplierToProduct(product, supplierId);

	        model.addAttribute("product", product);
	        model.addAttribute("suppliersToAdd", suppliersToAdd(productId));

	        return "/admin/suppliersToAdd.html";
	    }
	    
	    @Transactional
	    @GetMapping("/admin/removeSupplierFromProduct/{supplierId}/{productId}")
	    public String removeSupplierFromProduct(@PathVariable("supplierId") Long supplierId, @PathVariable("productId") Long productId, Model model){
	        Product product = this.productRepository.findById(productId).get();

	        this.productService.removeSupplierToProduct(product, supplierId);

	        model.addAttribute("product", product);
	        model.addAttribute("suppliersToAdd", suppliersToAdd(productId));
	        
	        return "/admin/suppliersToAdd.html";
	    }
}

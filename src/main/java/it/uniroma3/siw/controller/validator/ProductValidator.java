
package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.repository.ProductRepository;


@Component
public class ProductValidator implements Validator{
	@Autowired
    private ProductRepository productRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Product.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		 Product product = (Product) target;
		 if(product.getName() != null && product.getCode() != null
		            && productRepository.existsByNameAndPriceAndCode(product.getName(),product.getPrice(), product.getCode())){
		            errors.reject("product.duplicate");
		        }
		 if(product.getPrice() < 0) errors.reject("product.price.under0");
		
	}

}
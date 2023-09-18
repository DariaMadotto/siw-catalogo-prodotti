package it.uniroma3.siw.controller.validator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Supplier;
import it.uniroma3.siw.repository.SupplierRepository;

@Component
public class SupplierValidator implements Validator{
	
	 @Autowired
	 private SupplierRepository supplierRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return Supplier.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Supplier supplier = (Supplier) target;
        if(supplier.getName() != null && supplier.getSurname() != null
            && this.supplierRepository.existsByNameAndSurnameAndAddressAndEmailAndIva(supplier.getName(), supplier.getSurname(), supplier.getAddress(), supplier.getEmail(), supplier.getIva())){
            errors.reject("supplier.duplicate");
        }
		
	}

}
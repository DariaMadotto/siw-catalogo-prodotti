package it.uniroma3.siw.service;

import java.util.Set;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.Supplier;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.SupplierRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;

@Service
public class ProductService {

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ProductRepository productRepository;

    @Transactional
    public void createProduct(Product product){

    	this.productRepository.save(product);
    }
    
    //cambia i dettagli del prodotto,come prezzo, nome
    @Transactional
    public void editDetailsToProduct(Product product) {
        // Recupera il prodotto esistente dal repository utilizzando l'ID
        Product existingProduct = productRepository.findById(product.getId()).orElse(null);

        if (existingProduct != null) {
            // Aggiorna i dettagli del prodotto con i nuovi valori
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setCode(product.getCode());

            // Salva le modifiche nel repository
            productRepository.save(product);
        }
    }
    
    @Transactional
    public void setSupplierToProduct(Product product, Long productId) {
        Supplier supplier = this.supplierRepository.findById(productId).get();

        supplier.getProducts().add(product);
        product.getSuppliers().add(supplier);

        this.supplierRepository.save(supplier);
        this.productRepository.save(product);
    }

    @Transactional
    public void removeSupplierToProduct(Product product, Long productId) {
        Supplier supplier = this.supplierRepository.findById(productId).get();

        supplier.getProducts().remove(product);
        product.getSuppliers().remove(supplier);

        this.supplierRepository.save(supplier);
        this.productRepository.save(product);
    }

    public boolean hasReviewFromAuthor(Long productId, String username){
        Product product = this.productRepository.findById(productId).get();
        Set<Review> reviews = product.getReviews();
        for (Review review: reviews) {
            if(review.getAuthor().equals(username)) {
                return true;
            }
        }
        return false;
    }
}

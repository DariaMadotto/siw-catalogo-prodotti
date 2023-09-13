package it.uniroma3.siw.repository;

import java.util.Set;

//import java.time.LocalDate;
//import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Supplier;
//import it.uniroma3.siw.model.Product;
import jakarta.transaction.Transactional;

public interface SupplierRepository extends CrudRepository<Supplier,Long> {

    @Transactional
    boolean existsByNameAndSurnameAndAddressAndEmailAndIva(String name, String surname, String address, String email, String iva);
    // @Transactional
    Set<Supplier> getByProductsNotContains(Product product);
    /*List <Artist> getByDirectedMoviesNotContaining(Movie movie);*/
}

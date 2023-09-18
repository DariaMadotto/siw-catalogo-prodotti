package it.uniroma3.siw.model;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Supplier {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	@Column(nullable = false, unique = true)
	private String iva;
	@NotBlank
	private String name;
	@NotBlank
	private String surname;
	
	private String address;
	
	private String email;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Product> products;

	public Set<Product> getProducts() {
		return products;
	}
	public void setProducts(Set<Product> products) {
		this.products = products;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIva() {
		return iva;
	}
	public void setIva(String iva) {
		this.iva = iva;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public int hashCode() {
		return Objects.hash(email, iva, name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Supplier other = (Supplier) obj;
		return Objects.equals(email, other.email) && Objects.equals(iva, other.iva) && Objects.equals(name, other.name);
	}
}

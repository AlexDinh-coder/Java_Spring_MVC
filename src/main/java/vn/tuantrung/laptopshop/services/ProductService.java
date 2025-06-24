package vn.tuantrung.laptopshop.services;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.tuantrung.laptopshop.domain.Product;
import vn.tuantrung.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product pr) {
        return this.productRepository.save(pr);
    }

    public List<Product> getProducts() {
        return this.productRepository.findAll();
    }
    
    public void deleteProduct(long id) {
        this.productRepository.deleteById(id);
    }

    public Optional<Product> getProductById(long id) {
        return this.productRepository.findById(id);
    }
}

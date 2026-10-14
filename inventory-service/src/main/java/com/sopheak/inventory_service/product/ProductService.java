
package com.sopheak.inventory_service.product;

import com.sopheak.inventory_service.event.ProductEvent;
import com.sopheak.inventory_service.messaging.ProductEventProducer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventProducer productEventProducer;

    public ProductService(
            ProductRepository productRepository,
            ProductEventProducer productEventProducer) {
        this.productRepository = productRepository;
        this.productEventProducer = productEventProducer;
    }

    // CREATE
    public Product create(Product product) {
        Product savedProduct = productRepository.save(product);

        ProductEvent event = new ProductEvent(
                "PRODUCT_CREATED",
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getPrice(),
                savedProduct.getStock(),
                Instant.now().toString()
        );

        productEventProducer.publish(event);

        return savedProduct;
    }

    // SAVE / UPDATE
    public Product save(Product product) {
        return productRepository.save(product);
    }

    // READ ALL
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // READ ONE
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );
    }

    // DELETE
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(
                    "Product not found with id: " + id
            );
        }

        productRepository.deleteById(id);
    }
}
package com.rapidtech.productservice.service;

import com.rapidtech.productservice.dto.ProductRequest;
import com.rapidtech.productservice.dto.ProductResponse;
import com.rapidtech.productservice.event.GetProductEvent;
import com.rapidtech.productservice.model.Product;
import com.rapidtech.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final KafkaTemplate<String,GetProductEvent> kafkaTemplate;

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice()).build();
        productRepository.save(product);
        kafkaTemplate.send("notificationTopic",new GetProductEvent(product.getId()));
        log.info("Product {} is saved",product.getId());
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice()).build();
    }
}

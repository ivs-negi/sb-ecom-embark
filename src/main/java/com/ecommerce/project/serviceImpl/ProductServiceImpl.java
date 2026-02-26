package com.ecommerce.project.serviceImpl;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.service.FileService;
import com.ecommerce.project.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    @Value("${projetc.image}")
    private String path;

    // add product by category id
    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category  category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category",categoryId));


        Product product = modelMapper.map(productDTO, Product.class);

        product.setCategory(category);
        product.setImage("Default-Image");
        double productFinalPrice = product.getPrice() -
                ((product.getDiscount() / 100) * product.getPrice());
        product.setFinalPrice(productFinalPrice);

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }


    // get all products
    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;
    }

    @Override
    public ProductResponse getProductByCategory(Long categoryId) {
        Category  category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category",categoryId));

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;
    }

    // get product by keyword
    @Override
    public ProductResponse getProductByKeyword(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product",productId));

        Product product = modelMapper.map(productDTO, Product.class);

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDiscount(product.getDiscount());
        double productFinalPrice = product.getPrice() -
                ((product.getDiscount() / 100) * product.getPrice());
        existingProduct.setFinalPrice(productFinalPrice);

        Product savedProduct = productRepository.save(existingProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);

    }

    // delete product
    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product",productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    // update product image
    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product",productId));

        String fileName = fileService.updateImage(path,image);
        
        existingProduct.setImage(fileName);
        Product updatedproduct = productRepository.save(existingProduct);

        return modelMapper.map(updatedproduct, ProductDTO.class);
    }



}

package com.telusko.SpringEcom.service;

import com.telusko.SpringEcom.model.Product;
import com.telusko.SpringEcom.repo.ProductRepo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ImageGenService imageGenService;

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(int id) {
        return productRepo.findById(id).orElse(new Product(-1));
    }

    public Product addOrUpdateProduct(Product product, MultipartFile image) throws IOException {

        product.setImageName(image.getOriginalFilename());
        product.setImageType(image.getContentType());
        product.setProductImage(image.getBytes());

        Product savedProduct = productRepo.save(product);

        String content = String.format("""
                Product Name: %s,
                Description: %s,
                Brand: %s,
                Category: %s,
                Price: %.2f,
                Realease Date: %s,
                Available: %s,
                Stock: %s
                """,savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getBrand(),
                savedProduct.getCategory(),
                savedProduct.getPrice(),
                savedProduct.getReleaseDate(),
                savedProduct.isProductAvailable(),
                savedProduct.getStockQuantity()
        );

        Document document = new Document(
                UUID.randomUUID().toString(),
                content,
                Map.of("productId", savedProduct.getId())
        );

        vectorStore.add(List.of(document));

        return savedProduct;
    }


    public void deleteProduct(int id) {
        productRepo.deleteById(id);
    }

    @Transactional
    public List<Product> searchProducts(String keyword) {
        return productRepo.searchProducts(keyword);
    }

    public String generateDescription(String name, String category) {

        String description = String.format(
                "Write a simple and clear product description for a %s named '%s'. " +
                        "Avoid technical jargon. Keep it under 250 characters.",
                category, name
        );

        String output = chatClient.prompt()
                .user(description)
                .call()
                .content();


        return output;
    }

    public byte[] generateImage(String name, String category, String description) {

        String prompt = String.format(
                "Create a high-quality, realistic image of a %s called '%s'. " +
                        "The image should reflect this description: %s. " +
                        "Use natural lighting, neutral background, and clear details. " +
                        "Avoid text, logos, watermarks, or artistic filters. " +
                        "Make it look like a real product photo suitable for an e-commerce website.",
                category, name, description
        );

        byte[] genImage = imageGenService.genImage(prompt);

        return genImage;

    }
}

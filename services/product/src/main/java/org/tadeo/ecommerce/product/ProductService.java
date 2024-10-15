package org.tadeo.ecommerce.product;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tadeo.ecommerce.exception.ProductPurchaseException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    private final ProductMapper mapper;

    public Integer createProduct(ProductRequest request) {
        var product = mapper.toProduct(request);
        return repository.save(product).getId();
    }

    @Transactional
    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {

        /*
            List of ProductPurchaseRequest objects:
            {
                "productId": 104,
                "quantity": 2
            },
            {
                "productId": 2,
                "quantity": 5
            },
            {
                "productId": 23,
                "quantity": 1
            },
            {
                "productId": 5,
                "quantity": 3
            }
         */
        // Extract product IDs from the purchase requests
        // [104, 2, 23, 5]
        var productIds = request
                .stream()
                .map(ProductPurchaseRequest::productId)
                .toList();

        /*
            List of ProductPurchaseRequest objects:
            Are the same products as above
            {
                "productId": 104,
                "quantity": 10
            },
            {
                "productId": 2,
                "quantity": 30
            },
            {
                "productId": 23,
                "quantity": 0
            },
            {
                "productId": 5,
                "quantity": 3
            }
         */
        // Fetch the stored products from the repository based on the product IDs in the request
        // Note: the result will be ordered by productId
        // [2, 5, 23, 104] -> could be the same
        // [2, 5, 104] -> could NOT be the same
        // Note: productId: 23 have 0 stock right now
        var storedProducts = repository.findAllByIdInOrderById(productIds);

        // If the number of fetched products doesn't match the number of requested products, throw an exception
        // SAD CASE: If any product ID does not exist (e.g., productId = 23), the sizes will differ
        if (productIds.size() != storedProducts.size()) {
            throw new ProductPurchaseException("One or more products does not exist");
            // The method will exit here, and no further code will be executed
        }

        /*
            Since this point is reached, it means all products exist in the repository
         */

        // Sort the incoming request list by product ID to align with storedProducts (which is already sorted by productId)
        // [2, 5, 23, 104]
        var sortedRequest = request
                .stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();

        // List to store the responses after processing each purchase
        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();

        // Iterate through the stored products and the sorted request to process each purchase
        for (int i = 0; i < storedProducts.size(); i++) {
            var product = storedProducts.get(i);  // Get the product from the repository
            var productRequest = sortedRequest.get(i);  // Get the corresponding request

            // Check if the productRequest quantity is less than or equal to 0
            if (productRequest.quantity() <= 0) {
                throw new ProductPurchaseException("Quantity must be greater than 0 for product with Id:: " + productRequest.productId());
            }

            // Check if the available quantity of the product is less than the requested quantity
            // Note: productId: 23 have 0 stock right now, it will throw an exception
            if (product.getAvailableQuantity() < productRequest.quantity()) {
                throw new ProductPurchaseException("Insufficient stock quantity for product with Id:: " + productRequest.productId());
            }

            // Calculate the new available quantity after the purchase
            var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);  // Update the product's available quantity

            // Save the updated product back to the repository
            repository.save(product);

            // Add the product purchase response to the list after successful processing
            purchasedProducts.add(mapper.toProductPurchaseResponse(product, productRequest.quantity()));
        }

        // Return the list of responses after all products have been processed
        return purchasedProducts;
    }

    public ProductResponse findById(Integer productId) {
        return repository.findById(productId)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with the ID:: " + productId));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toProductResponse)
                .collect(Collectors.toList());
    }
}

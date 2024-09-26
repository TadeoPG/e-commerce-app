package org.tadeo.ecommerce.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tadeo.ecommerce.category.Category;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Product {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String description;

    private double availableQuantity;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}

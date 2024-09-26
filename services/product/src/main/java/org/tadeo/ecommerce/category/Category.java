package org.tadeo.ecommerce.category;

import jakarta.persistence.*;
import lombok.*;
import org.tadeo.ecommerce.product.Product;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Category {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Product> products;
}

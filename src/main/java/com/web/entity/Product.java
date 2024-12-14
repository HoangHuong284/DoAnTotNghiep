package com.web.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String code;

    private String name;

    private String imageBanner;

    private Double oldPrice;

    private Double price;

    private String description;

    private LocalDateTime createdDate;

    private Integer quantity;

    private Integer quantitySold;

    private String author;

    private String nxb;

    private String publishYear;

    private String weight;

    private String size;

    private Integer numPage;

    private String form;

    private Boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<ProductImage> productImages;

}

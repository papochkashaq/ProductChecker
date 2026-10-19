package org.example.productchecker.model;

import org.example.productchecker.config.ProductProperties;

public class ProductCard {

    private final ProductProperties properties;

    private ProductStatus lastStatus;

    public ProductCard(ProductProperties properties) {
        this.properties = properties;
        this.lastStatus = ProductStatus.OUT_OF_STOCK;
    }

    public String getTitle() {
        return properties.title();
    }

    public String getLink() {
        return properties.link();
    }

    public ProductStatus getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(ProductStatus lastStatus) {
        this.lastStatus = lastStatus;
    }

    @Override
    public String toString() {
        return "ProductCard{" +
                "properties=" + properties +
                ", lastStatus=" + lastStatus +
                '}';
    }
}

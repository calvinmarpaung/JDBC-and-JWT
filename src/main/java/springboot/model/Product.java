package springboot.model;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class Product {

    private static final AtomicLong counter = new AtomicLong();

    private long id;

    private String name;

    private int categoryId;

    private BigDecimal price;

    public Product() {
        this.id = counter.incrementAndGet();
    }

    public Product(String name, int categoryId, BigDecimal price) {
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", categoryId=" + categoryId
                + ", price=" + price + "]";
    }


}

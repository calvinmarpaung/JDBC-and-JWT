package springboot.service;

import springboot.model.Product;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service("productRepo")
public interface ProductRepo {
    String name = null;
    int count();

    int save(Product product);

    int update(Product product);

    int deleteById(Long id);

    List<Product> findAll();

    List<Product> findByNameAndPrice(String name, BigDecimal price);

    Product findById(Long id);

    String findNameById(Long id);

    int[] batchInsert(List<Product> products);

    int[][] batchInsert(List<Product> products, int batchSize);

    int[] batchUpdate(List<Product> products);

    int[][] batchUpdate(List<Product> products, int batchSize);

//    void saveImage(Long productId, File image);

//    List<Map<String, InputStream>> findImageByProductId(Long productId);

    void updateProduct(Product currentProduct);

    void deleteProductById(long id);

    void deleteAll();
}





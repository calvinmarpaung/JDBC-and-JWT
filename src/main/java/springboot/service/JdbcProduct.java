package springboot.service;

import springboot.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcProduct implements ProductRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Autowired
//    LobHandler lobHandler;

    public JdbcProduct() {
    }

    @Override
    public int count() {
        return jdbcTemplate
                .queryForObject("select count(*) from products", Integer.class);
    }

    @Override
    public int save(Product product) {
      return jdbcTemplate.update(
                "insert into products (name, price) values(?,?)",
                product.getName(), product.getPrice());
    }


    @Override
    public int update(Product product) {
        return jdbcTemplate.update(
                "update from products set price = ? where id = ?",
                product.getPrice(), product.getId());
    }


    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update(
                "delete from products where id = ?",
                id);
    }

    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query(
                "select * from products",
                (rs, rowNum) ->
                        new Product(
                                rs.getString("name"),
                                rs.getInt("categoryId"),
                                rs.getBigDecimal("price")
                        )
        );
    }

    // jdbcTemplate.queryForObject, populates a single object
    @Override
    public Product findById(Long id) {
        return jdbcTemplate.queryForObject(
                "select * from products where id = ?",
                new Object[]{id},
                (rs, rowNum) ->
                        new Product(
                                rs.getString("name"),
                                rs.getInt("categoryId"),
                                rs.getBigDecimal("price")
                        )
        );
    }

    @Override
    public List<Product> findByNameAndPrice(String name, BigDecimal price) {
        return jdbcTemplate.query(
                "select * from products where name like ? and price <= ?",
                new Object[]{"%" + name + "%", price},
                (rs, rowNum) ->
                        new Product(
                                rs.getString("name"),
                                rs.getInt("categoryId"),
                                rs.getBigDecimal("price")
                        )
        );
    }

    @Override
    public String findNameById(Long id) {
        return jdbcTemplate.queryForObject(
                "select name from products where id = ?",
                new Object[]{id},
                String.class
        );
    }



    public int[] batchUpdate(List<Product> products) {
        return this.jdbcTemplate.batchUpdate(
                "update products set price = ? where id = ?",
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setBigDecimal(1, products.get(i).getPrice());
                        ps.setLong(2, products.get(i).getId());
                    }

                    public int getBatchSize() {
                        return products.size();
                    }

                });

    }

    @Override
    public int[][] batchUpdate(List<Product> products, int batchSize) {
        int[][] updateCounts = jdbcTemplate.batchUpdate(
                "update products set price = ? where id = ?",
                products,
                batchSize,
                new ParameterizedPreparedStatementSetter<Product>() {
                    public void setValues(PreparedStatement ps, Product argument) throws SQLException {
                        ps.setBigDecimal(1, argument.getPrice());
                        ps.setLong(2, argument.getId());
                    }
                });
        return updateCounts;

    }



    @Override
    public int[] batchInsert(List<Product> products) {

        return this.jdbcTemplate.batchUpdate(
                "insert into products (name, price) values(?,?)",
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, products.get(i).getName());
                        ps.setBigDecimal(2, products.get(i).getPrice());
                    }

                    public int getBatchSize() {
                        return products.size();
                    }

                });
    }

    // Any failure causes the entire operation to roll back, none of the product will be added
    @Transactional
    @Override
    public int[][] batchInsert(List<Product> products, int batchSize) {

        int[][] updateCounts = jdbcTemplate.batchUpdate(
                "insert into products (name, price) values(?,?)",
                products,
                batchSize,
                new ParameterizedPreparedStatementSetter<Product>() {
                    public void setValues(PreparedStatement ps, Product argument) throws SQLException {
                        ps.setString(1, argument.getName());
                        ps.setBigDecimal(2, argument.getPrice());
                    }
                });
        return updateCounts;

    }

    // https://www.postgresql.org/docs/7.3/jdbc-binary-data.html
    // https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#jdbc-lob
//    @Override
//    public void saveImage(Long productId, File image) {
//
//        try (InputStream imageInStream = new FileInputStream(image)) {
//
//            jdbcTemplate.execute(
//                    "INSERT INTO product_image (product_id, filename, blob_image) VALUES (?, ?, ?)",
//                    new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
//                        protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
//                            ps.setLong(1, 1L);
//                            ps.setString(2, image.getName());
//                            lobCreator.setBlobAsBinaryStream(ps, 3, imageInStream, (int) image.length());
//                        }
//                    }
//            );
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public List<Map<String, InputStream>> findImageByProductId(Long productId) {
//
//        List<Map<String, InputStream>> result = jdbcTemplate.query(
//                "select id, product_id, filename, blob_image from product_image where product_id = ?",
//                new Object[]{productId},
//                new RowMapper<Map<String, InputStream>>() {
//                    public Map<String, InputStream> mapRow(ResultSet rs, int i) throws SQLException {
//
//                        String fileName = rs.getString("filename");
//                        InputStream blob_image_stream = lobHandler.getBlobAsBinaryStream(rs, "blob_image");
//
//                        // byte array
//                        //Map<String, Object> results = new HashMap<>();
//                        //byte[] blobBytes = lobHandler.getBlobAsBytes(rs, "blob_image");
//                        //results.put("BLOB", blobBytes);
//
//                        Map<String, InputStream> results = new HashMap<>();
//                        results.put(fileName, blob_image_stream);
//
//                        return results;
//
//                    }
//                });
//
//        return result;
//    }

    @Override
    public void updateProduct(Product currentProduct) {

    }

    @Override
    public void deleteProductById(long id) {

    }

    @Override
    public void deleteAll() {

    }

}
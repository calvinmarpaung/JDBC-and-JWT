package springboot.service;

import springboot.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class NamedParameterJdbcProductRepository extends JdbcProduct {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int update(Product product) {
        return namedParameterJdbcTemplate.update(
                "update products set price = :price where id = :id",
                new BeanPropertySqlParameterSource(product));
    }

    @Override
    public Product findById(Long id) {
        return namedParameterJdbcTemplate.queryForObject(
                "select * from products where id = :id",
                new MapSqlParameterSource("id", id),
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

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("name", "%" + name + "%");
        mapSqlParameterSource.addValue("price", price);

        return namedParameterJdbcTemplate.query(
                "select * from products where name like :name and price <= :price",
                mapSqlParameterSource,
                (rs, rowNum) ->
                        new Product(
                                rs.getString("name"),
                                rs.getInt("categoryId"),
                                rs.getBigDecimal("price")
                        )
        );
    }

}
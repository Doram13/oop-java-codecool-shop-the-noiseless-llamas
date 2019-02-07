package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDaoJDBC extends GeneralDaoJDBC implements ProductDao {

    ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
    SupplierDao supplierDataStore = SupplierDaoMem.getInstance();
    protected List<Product> data = new ArrayList<>();


    @Override
    public void add(Product product) {

    }

    @Override
    public Product find(int id) {return null;}


    @Override
    public void remove(int id) {

    }

    @Override
    public List<Product> getAll() {

        setSupplier();
        setProductCategory();
        String query = "SELECT * FROM product;";
            List<Product> productList = new ArrayList<>();
            try (Connection connection = getConnection();
                 Statement statement =connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query);)
            {
                return getProducts(productList, connection, resultSet);
            } catch (SQLException e) {
                e.printStackTrace();}
            return null;
        }

    private List<Product> getProducts(List<Product> productList, Connection connection, ResultSet resultSet) throws SQLException {
        while (resultSet.next()){
            int productid = resultSet.getInt("productid");
            float price = resultSet.getInt("price");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            String currency = resultSet.getString("currency");
            String supplier = resultSet.getString("supplier");
            String product_category = resultSet.getString("product_category");
            Supplier supplierObj = getSupplier(supplier);
            ProductCategory productCategoryObj = getProductCategory(product_category, supplierObj);
            Product product = new Product(productid, name, price, currency, description, productCategoryObj, supplierObj);
            uploadData(productList, product);}
        connection.close();
        return productList;
    }

    private void uploadData(List<Product> productList, Product product) {
        productList.add(product);
        ProductDao productDataStore = ProductDaoMem.getInstance();
        productDataStore.add(product);
        data = productList;
    }

    private ProductCategory getProductCategory(String product_category, Supplier supplierObj) {
        if (!supplierDataStore.isInList(supplierObj.getName())) {
            supplierDataStore.add(supplierObj);
        }
        ProductCategory productCategoryObj = getProductCategory(product_category);
        if (!productCategoryDataStore.isInList(productCategoryObj.getName())) {
            productCategoryDataStore.add(productCategoryObj);
        }
        return productCategoryObj;
    }

    private void setProductCategory() {
        ProductCategory allCategory = new ProductCategory("All", "All", "All items.");
        allCategory.setId(0);
        if (!productCategoryDataStore.isInList(allCategory.getName())) {
            productCategoryDataStore.add(allCategory);
        }
    }

    private void setSupplier() {
        Supplier allSupplier = new Supplier("All", "All items.");
        allSupplier.setId(0);
        if (!supplierDataStore.isInList(allSupplier.getName())) {
            supplierDataStore.add(allSupplier);
        }
    }


    @Override
    public List<Product> getBy(Supplier supplier) {
        return null;
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        if (productCategory.getName().equals("All")) {
            return data;
        } else {
            return data.stream().filter(t -> t.getProductCategory().getName().equals(productCategory.getName())).collect(Collectors.toList());
        }
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory, Supplier supplier) {
         List<Product> filteredByCategory = getBy(productCategory);
        if(supplier.getName().equals("All")) {
            return filteredByCategory;
        } else {
            return filteredByCategory.stream().filter(t -> t.getSupplier().getName().equals(supplier.getName())).collect(Collectors.toList());
        }
    }

    public List<Product> getData() {
        return data;
    }
}

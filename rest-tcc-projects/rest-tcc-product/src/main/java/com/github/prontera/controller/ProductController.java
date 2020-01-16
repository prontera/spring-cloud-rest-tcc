package com.github.prontera.controller;

import com.github.prontera.Delay;
import com.github.prontera.RandomlyThrowsException;
import com.github.prontera.Shift;
import com.github.prontera.domain.Product;
import com.github.prontera.model.request.IncreaseProductInventoryRequest;
import com.github.prontera.model.response.ObjectCollectionResponse;
import com.github.prontera.model.response.ObjectDataResponse;
import com.github.prontera.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/v1", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ProductController {
    @Autowired
    private ProductService productService;

    @Delay
    @RandomlyThrowsException
    @ApiOperation(value = "根据ID获取商品", notes = "")
    @RequestMapping(value = "/products/{id}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    public ObjectDataResponse<Product> findProduct(@PathVariable Long id) {
        final Product product = productService.find(id);
        if (product == null) {
            Shift.fatal(StatusCode.PRODUCT_NOT_EXISTS);
        }
        return new ObjectDataResponse<>(product);
    }

    @Delay
    @RandomlyThrowsException
    @ApiOperation(value = "获取全部商品", notes = "")
    @RequestMapping(value = "/products", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    public ObjectCollectionResponse<Product> getAllProducts() {
        final List<Product> productList = productService.findAll(0, 10000);
        return new ObjectCollectionResponse<>(productList);
    }

    @Delay
    @RandomlyThrowsException
    @ApiOperation(value = "变更商品库存", notes = "")
    @RequestMapping(value = "/products/{productId}/inventory", method = RequestMethod.PATCH)
    public ObjectDataResponse<Product> updateInventory(@PathVariable Long productId, @Valid @RequestBody IncreaseProductInventoryRequest request, BindingResult result) {
        final Product product = productService.find(productId);
        if (product == null) {
            Shift.fatal(StatusCode.PRODUCT_NOT_EXISTS);
        }
        product.setStock(request.getCount());
        productService.updateNonNullProperties(product);
        return new ObjectDataResponse<>(product);
    }

}

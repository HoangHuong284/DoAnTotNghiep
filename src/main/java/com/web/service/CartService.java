package com.web.service;

import com.web.entity.Cart;
import com.web.entity.Product;
import com.web.entity.User;
import com.web.exception.MessageException;
import com.web.repository.CartRepository;
import com.web.repository.ProductRepository;
import com.web.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ProductRepository productRepository;

    public void addCart(Long productId, Integer quantity) {
        Cart cart = new Cart();
        User user = userUtils.getUserWithAuthority();
        Optional<Cart> c = cartRepository.findByProductAndUser(user.getId(), productId);
        if(c.isPresent()){
            return;
        }
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()){
            throw new MessageException("Không tìm thấy product");
        }
        cart.setUser(user);
        cart.setQuantity(quantity);
        cart.setProduct(product.get());
        cartRepository.save(cart);
    }

    public void remove(Long id) {
        cartRepository.deleteById(id);
    }

    public List<Cart> findByUser() {
        List<Cart> list = cartRepository.findByUser(userUtils.getUserWithAuthority().getId());
        return list;
    }

    public void upQuantity(Long id) {
        Cart cart = cartRepository.findById(id).get();
        cart.setQuantity(cart.getQuantity() + 1);
        cartRepository.save(cart);
    }

    public void downQuantity(Long id) {
        Cart cart = cartRepository.findById(id).get();
        cart.setQuantity(cart.getQuantity() - 1);
        if(cart.getQuantity() == 0){
            cartRepository.deleteById(id);
            return;
        }
        cartRepository.save(cart);
    }

    public void removeCart() {
        cartRepository.deleteByUser(userUtils.getUserWithAuthority().getId());
    }

    public Long countCart() {
        return cartRepository.countCart(userUtils.getUserWithAuthority().getId());
    }

    public Double totalAmountCart() {
        List<Cart> list = cartRepository.findByUser(userUtils.getUserWithAuthority().getId());
        Double total = 0D;
        for(Cart c : list){
            total += c.getQuantity() * c.getProduct().getPrice();
        }
        System.out.println("Tổng số tiền giỏ hàng: " + total);
        return total;
    }

}

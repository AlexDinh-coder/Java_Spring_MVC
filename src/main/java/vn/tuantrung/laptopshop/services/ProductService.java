package vn.tuantrung.laptopshop.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.tuantrung.laptopshop.domain.Cart;
import vn.tuantrung.laptopshop.domain.CartDetail;
import vn.tuantrung.laptopshop.domain.Order;
import vn.tuantrung.laptopshop.domain.OrderDetail;
import vn.tuantrung.laptopshop.domain.Product;
import vn.tuantrung.laptopshop.domain.Product_;
import vn.tuantrung.laptopshop.domain.User;
import vn.tuantrung.laptopshop.repository.CartDetailRepository;
import vn.tuantrung.laptopshop.repository.CartRepository;
import vn.tuantrung.laptopshop.repository.OrderDetailRepository;
import vn.tuantrung.laptopshop.repository.OrderRepository;
import vn.tuantrung.laptopshop.repository.ProductRepository;
import vn.tuantrung.laptopshop.services.specification.ProductSpecs;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductService(ProductRepository productRepository,
            CartRepository cartRepository,
            CartDetailRepository cartDetailRepository,
            UserService userService,
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Product createProduct(Product pr) {
        return this.productRepository.save(pr);
    }

    private Specification<Product> nameLike(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Product_.NAME), "%" + name + "%");
    }

    public Page<Product> getProducts(Pageable pageable) {
        return this.productRepository.findAll(pageable);
    }

    public Page<Product> getProductsWithSpec(Pageable pageable, String name) {
    return this.productRepository.findAll(ProductSpecs.nameLike(name), pageable);
    }

    // case 1: min-price
    // public Page<Product> getProductsWithSpec(Pageable pageable, double min) {
    // return this.productRepository.findAll(ProductSpecs.minPrice(min), pageable);
    // }

    // case 2: max-price
    // public Page<Product> getProductsWithSpec(Pageable pageable, double max) {
    // return this.productRepository.findAll(ProductSpecs.maxPrice(max), pageable);
    // }

    // case 3: factory
    // public Page<Product> getProductsWithSpec(Pageable pageable, String factory) {
    // return this.productRepository.findAll(ProductSpecs.matchFactory(factory),
    // pageable);
    // }

    // case 4: array factory
    // public Page<Product> getProductsWithSpec(Pageable pageable, List<String>
    // factory) {
    // return this.productRepository.findAll(ProductSpecs.matchListFactory(factory),
    // pageable);
    // }

    // case 5
    // public Page<Product> getProductsWithSpec(Pageable pageable, String price) {
    // //eg: price 10 tr - 15tr
    // if (price.equals("10-trieu-15-trieu")) {
    // double min = 10000000;
    // double max = 15000000;
    // return this.productRepository.findAll(ProductSpecs.matchPrice(min,max),
    // pageable);
    // } else if (price.equals("15-trieu-30-trieu")) {
    // double min = 15000000;
    // double max = 30000000;
    // return this.productRepository.findAll(ProductSpecs.matchPrice(min,max),
    // pageable);

    // } else
    // return this.productRepository.findAll(pageable);

    // }

    // case 6:
    // public Page<Product> getProductsWithSpec(Pageable pageable, List<String> price) {
    //     Specification<Product> combinedSpec = (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
    //     int count = 0;
    //     for (String p : price) {
    //         double min = 0;
    //         double max = 0;

    //         // Set the appropriate min and max values based on the price range string
    //         switch (p) {
    //             case "10-trieu-15-trieu":
    //                 min = 10000000;
    //                 max = 15000000;
    //                 count++;
    //                 break;
    //             case "15-trieu-20-trieu":
    //                 min = 15000000;
    //                 max = 20000000;
    //                 count++;
    //                 break;

    //             case "20-trieu-30-trieu":
    //                 min = 20000000;
    //                 max = 30000000;
    //                 count++;
    //                 break;
               
    //         }

    //         if (min != 0 && max != 0 ) {
    //             Specification<Product> rangeSpecification = ProductSpecs.matchMultiplePrice(min, max);
    //             combinedSpec = combinedSpec.or(rangeSpecification);
                
    //         }
    //     }

    //     //Check if any price ranges were addded (combinedSpec is empty)
    //     if (count == 0) {
    //         return this.productRepository.findAll(pageable);
    //     } else {
    //         return this.productRepository.findAll(combinedSpec, pageable);
    //     }

    // }

    public void deleteProduct(long id) {
        this.productRepository.deleteById(id);
    }

    public Optional<Product> getProductById(long id) {
        return this.productRepository.findById(id);
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session, long quantity) {
        // Check user co cart chua> k co -> tao cart moi
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            // Check user co cart chua> k co -> tao cart moi
            Cart cart = this.cartRepository.findByUser(user);

            if (cart == null) {
                // tao moi cart
                Cart otherCart = new Cart();
                otherCart.setUser(user);
                otherCart.setSum(0);

                cart = this.cartRepository.save(otherCart);
            }
            // save cart_detail
            // tìm product by id
            Optional<Product> productOptional = this.productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product realProduct = productOptional.get();

                // check sản phẩm đã từng được thêm vào giỏ hàng chưa

                CartDetail oldDetail = this.cartDetailRepository.findByCartAndProduct(cart, realProduct);

                //
                if (oldDetail == null) {
                    CartDetail cartDetail = new CartDetail();
                    cartDetail.setCart(cart);
                    cartDetail.setProduct(realProduct);
                    cartDetail.setPrice(realProduct.getPrice());
                    cartDetail.setQuantity(quantity);
                    this.cartDetailRepository.save(cartDetail);

                    // update cart sum
                    int s = cart.getSum() + 1;
                    cart.setSum(s);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", s);

                } else {
                    oldDetail.setQuantity(oldDetail.getQuantity() + quantity);
                    this.cartDetailRepository.save(oldDetail);
                }

            }

        }

    }

    public Cart fetchByUser(User user) {
        return this.cartRepository.findByUser(user);
    }

    public void handleRemoveCartDetail(long cartDetailid, HttpSession session) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailid);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            Cart currentCart = cartDetail.getCart();
            // delete cart-detail
            this.cartDetailRepository.deleteById(cartDetailid);

            // update cart
            if (currentCart.getSum() > 1) {
                // update current cart
                int s = currentCart.getSum() - 1;
                currentCart.setSum(s);
                session.setAttribute("sum", s);
                this.cartRepository.save(currentCart);
            } else {
                // delete cart (sum = 1)
                this.cartRepository.deleteById(currentCart.getId());
                session.setAttribute("sum", 0);
            }
        }
    }

    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> optionalCartDetail = this.cartDetailRepository.findById(cartDetail.getId());
            if (optionalCartDetail.isPresent()) {
                CartDetail currenCartDetail = optionalCartDetail.get();
                currenCartDetail.setQuantity((cartDetail.getQuantity()));
                this.cartDetailRepository.save(currenCartDetail);
            }
        }
    }

    public void handlePlaceOrder(User user, HttpSession session, String receiverName,
            String receiverAddress,
            String receiverPhoneNumber) {
        // step 1: get cart by user
        Cart cart = this.cartRepository.findByUser(user);
        if (cart != null) {
            List<CartDetail> cartDetails = cart.getCartDetails();
            if (cartDetails != null) {

                // create order
                Order order = new Order();
                order.setUser(user);
                order.setReceiverName(receiverName);
                order.setReceiverAddress(receiverAddress);
                order.setReceiverPhone(receiverPhoneNumber);
                order.setStatus("PENDING");

                double sum = 0;
                for (CartDetail cd : cartDetails) {
                    sum += cd.getPrice();
                }
                order.setTotalPrice(sum);
                order = this.orderRepository.save(order);

                // create orderDetail
                for (CartDetail cd : cartDetails) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cd.getProduct());
                    orderDetail.setPrice(cd.getPrice());
                    orderDetail.setQuantity(cd.getQuantity());

                    this.orderDetailRepository.save(orderDetail);
                }

                // step 2: delete cartDetail and cart
                for (CartDetail cd : cartDetails) {
                    this.cartDetailRepository.deleteById(cd.getId());
                }
                this.cartRepository.deleteById(cart.getId());

                // step 3: update session
                session.setAttribute("sum", 0);

            }
        }

    }
}

package vn.tuantrung.laptopshop.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import vn.tuantrung.laptopshop.domain.Order;
import vn.tuantrung.laptopshop.services.OrderService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller

public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/order")
    public String getDashboard(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                //convert from String to unt
                page = Integer.parseInt(pageOptional.get());
                
            } else {
                //page =1
            }
        } catch (Exception e) {
            // TODO: handle exception
            //page =1;
        }
        Pageable pageable = PageRequest.of(page -1 , 3);
        Page<Order> orderPage = this.orderService.fetchAllOrders(pageable);
        List<Order> order = orderPage.getContent();
        
        model.addAttribute("orders", order);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        return "admin/order/show";
    }

    @GetMapping("/admin/order/{id}")
    public String getOrderDetailPage(Model model, @PathVariable long id) {
        Order order = this.orderService.fetchOrderById(id).get();
        model.addAttribute("order", order);
        model.addAttribute("id", id);
        model.addAttribute("orderDetails", order.getOrderDetails());

        return "admin/order/detail";
    }

    @GetMapping("admin/order/delete/{id}")
    public String getDeleteOrderPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("newOrder", new Order());
        return "admin/order/delete";
    }

    @PostMapping("admin/order/delete")
    public String postDeleteOrder(@ModelAttribute("newOrder") Order order) {
       this.orderService.deleteOrderById(order.getId());
        return "redirect:/admin/order";
        
    
    }

    @GetMapping("admin/order/update/{id}")
    public String getUpdateOrderPage(Model model, @PathVariable long id) {
        Optional<Order> currentOrder = this.orderService.fetchOrderById(id);
        model.addAttribute("newOrder", currentOrder.get());
        return "admin/order/update";
    }

    @PostMapping("admin/order/update")
    public String handleUpdateOrder(@ModelAttribute("newOrder") Order order) {
        this.orderService.updateOrder(order);
    
        return "redirect:/admin/order";
    }
    
    

}

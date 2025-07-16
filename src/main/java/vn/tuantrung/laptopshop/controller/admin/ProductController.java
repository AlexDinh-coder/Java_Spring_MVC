package vn.tuantrung.laptopshop.controller.admin;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.tuantrung.laptopshop.domain.Product;

import vn.tuantrung.laptopshop.services.ProductService;
import vn.tuantrung.laptopshop.services.UploadService;

import org.springframework.web.bind.annotation.PostMapping;


@Controller

public class ProductController {

    private final UploadService uploadService;
    private final ProductService productService;

    public ProductController(UploadService uploadService, ProductService productService) {
        this.uploadService = uploadService;
        this.productService = productService;
    }

    @GetMapping("/admin/product")
    public String getProduct(Model model, @RequestParam("page") Optional<String> pageOptional) 
    {

        int page = 1;
       try {
        if (pageOptional.isPresent()) {
            //convert from String to int
            page = Integer.parseInt(pageOptional.get());
            
        } else {
            //page =1
        }
        
       } catch (Exception e) {
        //page =1
        // TODO: handle exception
       }

        Pageable pageable = PageRequest.of(page -1 , 5);
        Page<Product> prs = this.productService.getProducts(pageable);
        List<Product> listProducts = prs.getContent();
        model.addAttribute("products", listProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", prs.getTotalPages());
        return "admin/product/show";
    }

    @GetMapping("admin/product/create")
    public String getCreateProduct(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    @PostMapping("admin/product/create")
    public String handleCreateProduct(@ModelAttribute("newProduct") @Valid Product pr,
            BindingResult newProductBindingResult,
            @RequestParam("trungFile") MultipartFile file) {
        // validate
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/create";
        }
        //

        // upload image
        String image = this.uploadService.handleSaveUploadFile(file, "product");
        pr.setImage(image);

        this.productService.createProduct(pr);
        return "redirect:/admin/product";

    }

    @GetMapping("admin/product/delete/{id}")
    public String getDeleteProduct(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("newProduct", new Product());
        return "admin/product/delete";
    }

    @PostMapping("admin/product/delete")
    public String postDeleteProduct(Model model, @ModelAttribute("newProduct") Product pr) {
        this.productService.deleteProduct(pr.getId());
        return "redirect:/admin/product";

    }

    @GetMapping("admin/product/{id}")
    public String getProductDetail(Model model, @PathVariable long id) {
        Product pr = this.productService.getProductById(id).get();
        model.addAttribute("product", pr);
        model.addAttribute("id", id);
        return "admin/product/detail";
    }

    @GetMapping("admin/product/update/{id}")
    public String getUpdateProduct(Model model, @PathVariable long id) {
        Optional<Product> currentProduct = this.productService.getProductById(id);
        model.addAttribute("newProduct", currentProduct.get());
        return "admin/product/update";
    }

    @PostMapping("admin/product/update")
    public String postUpdateProduct(@ModelAttribute("newProduct") @Valid Product pr,
            BindingResult newProductBindingResult,
            @RequestParam("trungFile") MultipartFile file) {
        // validate
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/update";
        }

        Product currentProduct = this.productService.getProductById(pr.getId()).get();
        if (currentProduct != null) {
            // update new image

            if (!file.isEmpty()) {
                String image = this.uploadService.handleSaveUploadFile(file, "product");
                currentProduct.setImage(image);
            }
            currentProduct.setName(pr.getName());
            currentProduct.setDetailDesc(pr.getDetailDesc());
            currentProduct.setPrice(pr.getPrice());
            currentProduct.setQuantity(pr.getQuantity());
            currentProduct.setShortDesc(pr.getShortDesc());
            currentProduct.setFactory(pr.getFactory());
            currentProduct.setTarget(pr.getTarget());
          

            this.productService.createProduct(currentProduct);
        }

        return "redirect:/admin/product";
    }

}

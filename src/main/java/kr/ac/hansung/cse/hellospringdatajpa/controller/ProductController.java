package kr.ac.hansung.cse.hellospringdatajpa.controller;

import kr.ac.hansung.cse.hellospringdatajpa.entity.Product;
import kr.ac.hansung.cse.hellospringdatajpa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping({"", "/"})
    public String viewHomePage(Model model) {
        List<Product> listProducts = service.listAll();
        model.addAttribute("listProducts", listProducts);
        return "index";
    }

    @GetMapping("/new")
    public String showNewProductPage(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "new_product";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductPage(@PathVariable(name = "id") Long id, Model model) {
        Product product = service.get(id);
        model.addAttribute("product", product);
        return "edit_product";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product, Model model) {

        // Validation - Price must be 0 or greater
        if (product.getPrice() < 0) {
            model.addAttribute("priceError", "Price must be 0 or greater.");
            if (product.getId() != null) {
                return "edit_product";
            } else {
                return "new_product";
            }
        }

        // Required field validation
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            model.addAttribute("nameError", "Please enter product name.");
            if (product.getId() != null) {
                return "edit_product";
            } else {
                return "new_product";
            }
        }

        service.save(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Long id) {
        service.delete(id);
        return "redirect:/products";
    }
}
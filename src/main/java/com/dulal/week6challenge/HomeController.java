package com.dulal.week6challenge;


import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    CarRepo carRepo;
    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    CloudinaryConfig cloud;

    @RequestMapping("/")
    public String carlist(Model model) {
        model.addAttribute("cats", categoryRepo.findAll());
        model.addAttribute("cars", carRepo.findAll());

        return "carlist";
    }

    @GetMapping("/add")
    public  String addCar(Model model){
        model.addAttribute("options", categoryRepo.findAll());
        model.addAttribute("car", new Car());
        return "addcar";
    }

    @PostMapping("/processcar")
    public String processCarForm(@Valid @ModelAttribute("car") Car car,
                              BindingResult result, @RequestParam("file")MultipartFile file) {
        if (result.hasErrors()) {
            return "addcar";
        }
        else if (file.isEmpty()) {
            return "redirect:/add";
        }
        try {
            Map uploadResult = cloud.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            car.setPicture(uploadResult.get("url").toString());
            carRepo.save(car);
        }
        catch (IOException e) {
            e.printStackTrace();
            return "redirect:/addcar";

        }
        return "redirect:/";

    }


    @GetMapping("/addcat")
    public  String addCategory(Model model) {
        model.addAttribute("cat", new Category());
        return "addcategory";
    }

    @PostMapping("processcat")
    public String processCateg(@Valid Category cat, BindingResult result){
        if (result.hasErrors()){
            return "addcategory";
        }
        categoryRepo.save(cat);
        return "redirect:/";

    }

    @RequestMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") long id, Model model) {
        model.addAttribute("car", carRepo.findById(id).get());
        return "viewcar";
    }

    @RequestMapping("/update/{id}")
    public String updateCar(@PathVariable("id") long id, Model model) {
        model.addAttribute("cat", categoryRepo.findById(id));
        model.addAttribute("car", carRepo.findById(id));
        carRepo.deleteById(id);
        return "addcar";

    }


    @RequestMapping("/delete/{id}")
    public String deletecar(@PathVariable("id")long id){
        carRepo.deleteById(id);
        return "redirect:/";

    }


}














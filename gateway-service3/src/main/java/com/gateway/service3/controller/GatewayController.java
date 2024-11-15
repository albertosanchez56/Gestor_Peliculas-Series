package com.gateway.service3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GatewayController {

	 @GetMapping("/Home")
	    public String showIndex(Model model) {
	        // Agregar cualquier dato que necesites pasar a la vista
	        model.addAttribute("title", "Bienvenido a mi portfolio");
	        
	        // Devolver el nombre de la vista Thymeleaf (index.html)
	        return "index";
	    }
	 
	 @GetMapping("/Pelicula")
	    public String showPelicula(Model model) {
	        // Agregar cualquier dato que necesites pasar a la vista
	        model.addAttribute("title", "Bienvenido a mi portfolio");
	        
	        // Devolver el nombre de la vista Thymeleaf (index.html)
	        return "peliculasinfo";
	    }
	 
}

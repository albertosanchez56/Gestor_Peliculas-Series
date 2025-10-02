package com.gateway.service3.modelos;

public class Director {

    private Long id; // Agregar atributo id
    private String name;

    public Director() {
        super();
    }

    public Director(Long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

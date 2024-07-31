package com.ulatina.model;


public class Categoria {

    private int id;
    private String categoria;

    // Constructor por defecto
    public Categoria() {
    }

    // Constructor con parámetros
    public Categoria(int id, String categoria) {
        this.id = id;
        this.categoria = categoria;
    }

    // Getter para id
    public int getId() {
        return id;
    }

    // Setter para id
    public void setId(int id) {
        this.id = id;
    }

    // Getter para categoria
    public String getCategoria() {
        return categoria;
    }

    // Setter para categoria
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return categoria;
    }
}

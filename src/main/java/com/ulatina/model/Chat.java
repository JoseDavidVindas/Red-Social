/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author josem
 */
public class Chat implements Serializable{
    
    private int id;
    private UsuarioTO usuario1;
    private UsuarioTO usuario2;

    public Chat() {
    }

    public Chat(int id, UsuarioTO usuario1, UsuarioTO usuario2) {
        this.id = id;
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UsuarioTO getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(UsuarioTO usuario1) {
        this.usuario1 = usuario1;
    }

    public UsuarioTO getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(UsuarioTO usuario2) {
        this.usuario2 = usuario2;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.id;
        hash = 47 * hash + Objects.hashCode(this.usuario1);
        hash = 47 * hash + Objects.hashCode(this.usuario2);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Chat other = (Chat) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.usuario1, other.usuario1)) {
            return false;
        }
        return Objects.equals(this.usuario2, other.usuario2);
    }
    
    
}

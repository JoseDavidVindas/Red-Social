/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 *
 * @author josem
 */
public class Conversacion implements Serializable{
    
    private int id;
    private Chat chat;
    private UsuarioTO usuario;
    private String mensaje;
    private Timestamp fecha;
    private String estado;

    public Conversacion() {
    }

    public Conversacion(int id, Chat chat, UsuarioTO usuario, String mensaje, Timestamp fecha, String estado) {
        this.id = id;
        this.chat = chat;
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.estado = estado;
    }

    

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public UsuarioTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioTO usuario) {
        this.usuario = usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.id;
        hash = 59 * hash + Objects.hashCode(this.chat);
        hash = 59 * hash + Objects.hashCode(this.usuario);
        hash = 59 * hash + Objects.hashCode(this.mensaje);
        hash = 59 * hash + Objects.hashCode(this.fecha);
        hash = 59 * hash + Objects.hashCode(this.estado);
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
        final Conversacion other = (Conversacion) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.mensaje, other.mensaje)) {
            return false;
        }
        if (!Objects.equals(this.estado, other.estado)) {
            return false;
        }
        if (!Objects.equals(this.chat, other.chat)) {
            return false;
        }
        if (!Objects.equals(this.usuario, other.usuario)) {
            return false;
        }
        return Objects.equals(this.fecha, other.fecha);
    }

    

    
    
    
}

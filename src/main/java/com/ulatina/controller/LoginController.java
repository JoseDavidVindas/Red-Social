/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.controller;

import com.ulatina.model.UsuarioTO;
import com.ulatina.service.ServicioUsuario;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Jefferson
 */
@ManagedBean(name = "loginController")
@SessionScoped
public class LoginController implements Serializable {

    private static final long serialVersionUID = 1L;
    private UsuarioTO usuarioTO = new UsuarioTO();

    public LoginController() {
    }

    public void ingresar() {
        ServicioUsuario servicioUsuario = new ServicioUsuario();
        UsuarioTO usuarioTORetorno = servicioUsuario.validarUsuario(this.getUsuarioTO().getCorreo(), this.getUsuarioTO().getContrasena());
        if (usuarioTORetorno != null) {
            this.usuarioTO = usuarioTORetorno;
            this.redireccionar("/Publicacion.xhtml");
        } else {
            FacesContext.getCurrentInstance().addMessage("sticky-key", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campos inválidos", "La clave o correo no son correctos"));
        }
    }
    
    public void registrar(){
        this.redireccionar("/registro.xhtml");
    }

    public void redireccionar(String ruta) {
        HttpServletRequest request;
        try {
            request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            FacesContext.getCurrentInstance().getExternalContext().redirect(request.getContextPath() + ruta);
        } catch (Exception e) {

        }
    }

    public UsuarioTO getUsuarioTO() {
        return usuarioTO;
    }

    public void setUsuarioTO(UsuarioTO usuarioTO) {
        this.usuarioTO = usuarioTO;
    }

}

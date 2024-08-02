/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.controller;

import com.ulatina.model.Publicacion;
import com.ulatina.model.Rol;
import com.ulatina.model.UsuarioTO;
import com.ulatina.service.ServicioPublicacion;
import com.ulatina.service.ServicioUsuario;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author Jose
 */
@ManagedBean(name = "verPerfilController")
@ViewScoped
public class VerPerfilController implements Serializable {

    private static final long serialVersionUID = 1L;
    private UsuarioTO usuario;
    private ServicioUsuario servUsuario;
    private String cvUrl;
    private String biografia;
    private String fotoPerfil;
    @ManagedProperty(value = "#{loginController}")
    private LoginController loginController;
    private Rol rol;
    private List<Publicacion> publicaciones;
    private ServicioPublicacion ServPubli;

    public VerPerfilController() {
        rol = new Rol();
        servUsuario = new ServicioUsuario();
        publicaciones = new ArrayList<>();
        ServPubli = new ServicioPublicacion();
    }

    @PostConstruct
    public void init() {
        try {

            usuario = loginController.getUsuarioTO();
            rol = servUsuario.rolPK(usuario.getRol());
            biografia = usuario.getBiografia();
            fotoPerfil = usuario.getFotoPerfil();
            cvUrl = usuario.getCvUrl();
            publicaciones = ServPubli.findAllPublicaciones(usuario.getId());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void verCV() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(cvUrl);
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo abrir el CV."));
            e.printStackTrace();
        }
    }

    public void agregarCV(UploadedFile archivo) {
        try {
            if (archivo != null) {
                String url = saveFile(archivo);
                if (servUsuario.actualizarCV(usuario.getId(), url)) {
                    cvUrl = url;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("CV actualizado satisfactoriamente"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el CV."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizarBiografia() {
        try {
            if (servUsuario.actualizarBiografia(usuario.getId(), biografia)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Biografía actualizada satisfactoriamente"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar la biografía."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizarFotoPerfil(UploadedFile archivo) {
        try {
            if (archivo != null) {
                String url = saveFile(archivo);
                if (servUsuario.actualizarFotoPerfil(usuario.getId(), url)) {
                    fotoPerfil = url;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Foto de perfil actualizada satisfactoriamente"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar la foto de perfil."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void eliminarPublicacion(Publicacion publicacion) {
    try {
        boolean exito = ServPubli.eliminarPublicacion(publicacion);
        if (exito) {
            publicaciones.remove(publicacion);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Publicación eliminada correctamente"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la publicación."));
        }
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al eliminar la publicación."));
        e.printStackTrace();
    }
}

    public void editarPerfil() {
        this.redireccionar("/editarperfil.xhtml");
    }

    public void atras() {
        this.redireccionar("/Publicacion.xhtml");
    }

    public void redireccionar(String ruta) {
        HttpServletRequest request;
        try {
            request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            FacesContext.getCurrentInstance().getExternalContext().redirect(request.getContextPath() + ruta);
        } catch (Exception e) {

        }
    }

    public UsuarioTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioTO usuario) {
        this.usuario = usuario;
    }

    public ServicioUsuario getServUsuario() {
        return servUsuario;
    }

    public void setServUsuario(ServicioUsuario servUsuario) {
        this.servUsuario = servUsuario;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }


    /*private int getUsuarioId() {
         FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        UsuarioTO usuarioLogueado = (UsuarioTO) session.getAttribute("usuarioLogueado");
        return usuarioLogueado != null ? usuarioLogueado.getId() : -1; 
    }*/
    private String saveFile(UploadedFile archivo) {
        String filePath = "/uploads/" + archivo.getFileName();
        try (InputStream input = archivo.getInputStream()) {
            Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar el archivo."));
            return null;
        }
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Publicacion> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public ServicioPublicacion getServPubli() {
        return ServPubli;
    }

    public void setServPubli(ServicioPublicacion ServPubli) {
        this.ServPubli = ServPubli;
    }

}

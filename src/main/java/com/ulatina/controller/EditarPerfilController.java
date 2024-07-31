/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.controller;

import com.ulatina.model.UsuarioTO;
import com.ulatina.service.ServicioUsuario;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author Jose
 */
@ManagedBean(name="editarPerfilController")
@SessionScoped
public class EditarPerfilController implements Serializable {

    private static final long serialVersionUID = 1L;
    private UsuarioTO usuario;
    private ServicioUsuario servUsuario;
    private String biografia;
    private UploadedFile fotoPerfil;
    private UploadedFile nuevoCV;
    private Part archivoFotoPerfil;
    private String nuevacontrasena = "";
    
    @ManagedProperty(value = "#{loginController}")
    private LoginController loginController;

    @PostConstruct
    public void init() {
        if (loginController != null) {
            usuario = loginController.getUsuarioTO();
        }
    }

    public void guardarCambios() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (archivoFotoPerfil != null && archivoFotoPerfil.getSize() > 0) {
                String fotoPerfilPath = guardarArchivo(archivoFotoPerfil);
                usuario.setFotoPerfil(fotoPerfilPath);
            }

            if (nuevacontrasena != null && !nuevacontrasena.isEmpty()) {
                usuario.setContrasena(nuevacontrasena);
            }

          
            ServicioUsuario servicioUsuario = new ServicioUsuario();

            if (servicioUsuario.actualizarUsuario(usuario)) {
                context.addMessage(null, new FacesMessage("Perfil actualizado satisfactoriamente"));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron guardar los cambios en el perfil."));
            }
        } catch (IOException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al subir el archivo."));
            e.printStackTrace();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error inesperado."));
            e.printStackTrace();
        }
    }

    private String guardarArchivo(Part archivo) throws IOException {
        String fileName = Paths.get(archivo.getSubmittedFileName()).getFileName().toString();
  
        String filePath = "http://localhost:8080/Red_Social_Academica/archivos/imagenes/" + fileName;
        
        File file = new File(filePath);
        
        try (InputStream input = archivo.getInputStream(); OutputStream output = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        return filePath;
    }
    
    public void agregarCV() {
        try {
            if (nuevoCV != null) {
                String url = saveFile(nuevoCV); // Método para guardar el archivo y obtener la URL
                if (servUsuario.actualizarCV(usuario.getId(), url)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("CV actualizado satisfactoriamente"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el CV."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void atras(){
        this.redireccionar("/verperfil.xhtml");
    }
    
    public void redireccionar(String ruta) {
        HttpServletRequest request;
        try {
            request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            FacesContext.getCurrentInstance().getExternalContext().redirect(request.getContextPath() + ruta);
        } catch (Exception e) {

        }
    }

    // Getters y setters para los atributos

    private int getUsuarioId() {
        // Implementa la lógica para obtener el id del usuario actual
        return 1; // Ejemplo de retorno; reemplaza con la lógica real
    }

    private String saveFile(UploadedFile archivo) {
        // Implementa la lógica para guardar el archivo y devolver la URL
        return ""; // Retorna la URL del archivo guardado
    }
    
    public UsuarioTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioTO usuario) {
        this.usuario = usuario;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public String getNuevacontrasena() {
        return nuevacontrasena;
    }

    public void setNuevacontrasena(String nuevacontrasena) {
        this.nuevacontrasena = nuevacontrasena;
    }

    public Part getArchivoFotoPerfil() {
        return archivoFotoPerfil;
    }

    public void setArchivoFotoPerfil(Part archivoFotoPerfil) {
        this.archivoFotoPerfil = archivoFotoPerfil;
    }

    public UploadedFile getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(UploadedFile fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}


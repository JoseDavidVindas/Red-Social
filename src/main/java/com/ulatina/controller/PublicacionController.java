/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.controller;

import com.ulatina.model.Archivo;
import com.ulatina.model.Categoria;
import com.ulatina.model.Documento;
import com.ulatina.model.Imagen;
import com.ulatina.model.Publicacion;
import com.ulatina.model.UsuarioTO;
import com.ulatina.service.ServicioCategoria;
import com.ulatina.service.ServicioFavorito;
import com.ulatina.service.ServicioPublicacion;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.ResponsiveOption;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author josem
 */
@ManagedBean(name = "publicacionController")
@SessionScoped
public class PublicacionController implements Serializable {

    private static final long serialVersionUID = 1L;
    private ServicioFavorito servicioFavorito;
    private Publicacion publicacion;
    private String descripcion;
    private ServicioPublicacion servPublicacion;
    private ServicioCategoria servCategoria;
    private String categoria;
    private List<Publicacion> publicaciones;
    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;
    private UsuarioTO usuario;
    //private UploadedFiles files;
    private List<Archivo> archivos;
    private Archivo archivo;
    List<UploadedFile> files;
    private Documento documento;
    private Imagen imagen;
    private List<Documento> documentos;
    private List<Imagen> imagenes;
    private List<ResponsiveOption> responsiveOptions1;
    private String photo;
    private UsuarioTO user;
    private List<Categoria> categorias;
    private String categoriaSeleccionada;
    private List<String> categoriasSeleccionadas;

    @ManagedProperty(value = "#{loginController}")
    private LoginController loginController;

    public PublicacionController() {
        servPublicacion = new ServicioPublicacion();
        servCategoria = new ServicioCategoria();
        publicaciones = new ArrayList<>();
        responsiveOptions1 = new ArrayList<>();
        responsiveOptions1.add(new ResponsiveOption("1024px", 5));
        responsiveOptions1.add(new ResponsiveOption("768px", 3));
        responsiveOptions1.add(new ResponsiveOption("560px", 1));
        photo = "No se cargo la imagen";
        cargarPublicaciones(0);
        cargarCategorias();
    }

    @PostConstruct
    public void init() {
        user = loginController.getUsuarioTO();
        this.servicioFavorito = new ServicioFavorito();
    }

    public void handleFileUploadEvent(FileUploadEvent event) throws IOException {
        System.out.println("===>>> " + event.getFile().getFileName() + " size: " + event.getFile().getSize());
        documento = new Documento();
        imagen = new Imagen();
        this.copyFile(event.getFile().getFileName(), event.getFile().getInputStream(), false);

    }

    public void nuevaPublicacion() {
        descripcion = "";
        files = new ArrayList<>();
        imagenes = new ArrayList<Imagen>();
        documentos = new ArrayList<Documento>();
        categoriaSeleccionada = null;
    }

    protected void copyFile(String fileName, InputStream in, boolean esTemporal) {
        try {
            if (fileName != null) {

                String[] partesArchivo = fileName.split(Pattern.quote("."));
                String nombreArchivo = partesArchivo[0];
                String extensionArchivo = partesArchivo[1];
                if (esTemporal) {
                    nombreArchivo += "_TMP";
                }
                String url;
                // Determine the destination path based on the file extension
                String destinationFile;
                if (extensionArchivo.equalsIgnoreCase("jpg") || extensionArchivo.equalsIgnoreCase("jpeg")
                        || extensionArchivo.equalsIgnoreCase("png") || extensionArchivo.equalsIgnoreCase("gif")) {
                    destinationFile = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/archivos/imagenes/");
                    url = "" + "http://localhost:8080/Red_Social_Academica/archivos/imagenes/" + nombreArchivo + "." + extensionArchivo;
                    imagen.setUrl(url);
                    imagenes.add(imagen);
                } else if (extensionArchivo.equalsIgnoreCase("pdf") || extensionArchivo.equalsIgnoreCase("docx")) {
                    destinationFile = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/archivos/documentos/");
                    url = "" + "http://localhost:8080/Red_Social_Academica/archivos/documentos/" + nombreArchivo + "." + extensionArchivo;
                    documento.setUrl(url);
                    documentos.add(documento);
                } else {
                    throw new IOException("Unsupported file type: " + extensionArchivo);
                }

                //File tmp = new File(destinationFile + fileName);
                File tmp = new File(destinationFile + nombreArchivo + "." + extensionArchivo);
                tmp.getParentFile().mkdirs();
                OutputStream out = new FileOutputStream(tmp);
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }

                in.close();
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void crearPublicacion() {
        try {

            //handleFileUpload();
            publicacion = new Publicacion();
            publicacion.setDescripcion(descripcion);
            publicacion.setUsuario(loginController.getUsuarioTO());
            publicacion.setDocumentos(documentos);
            publicacion.setImagenes(imagenes);
            publicacion.setCategoria(categoriaSeleccionada);

            if (!servPublicacion.insertar(publicacion)) {
                descripcion = "";
                FacesContext.getCurrentInstance().addMessage("sticky-key", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo realizar la publicacion"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Publicacion creada satisfactoriamente"));
                cargarPublicaciones(0); // Actualiza la lista de publicaciones
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Publicacion> getPublicaciones() {
        return publicaciones;
    }

    public void cargarPublicaciones(int size) {
        try {
            size = size + 10;
            publicaciones = servPublicacion.findAll(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarMasPublicaciones() {
        try {
            int size = publicaciones.size() + 10;
            publicaciones = servPublicacion.findAll(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMore() {
        currentPage++;
        cargarPublicaciones(0);
    }

    public String obtenerIconoDocumento(String url) {
        if (url != null && !url.isEmpty()) {
            String extension = url.substring(url.lastIndexOf('.') + 1).toLowerCase();
            switch (extension) {
                case "pdf":
                    return "pi pi-file-pdf";
                case "doc":
                case "docx":
                    return "pi pi-file-word";
                case "xls":
                case "xlsx":
                    return "pi pi-file-excel";
                // Agrega más casos según sea necesario
                default:
                    return "pi pi-file";
            }
        }
        return "pi pi-file";
    }

    public String obtenerNombreDocumento(String url) {
        if (url != null && !url.isEmpty()) {
            // Extraer el nombre del archivo desde la URL
            return url.substring(url.lastIndexOf('/') + 1);
        }
        return "Documento";
    }

    public void verDocumento(String url) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo abrir el documento."));
            e.printStackTrace();
        }
    }

    public void agregarFavoritos(Publicacion publicacion) {
        try {
            int idUsuario = obtenerIdUsuarioActual();
            servicioFavorito.agregarArchivoFavorito(idUsuario, publicacion.getId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Publicación agregada favoritos"));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo agregar la publicación a favoritos"));
            e.printStackTrace();
        }
    }

    public void RedireccionarFavorito() {
        this.redireccionar("/Favorito.xhtml");
    }

    private int obtenerIdUsuarioActual() {
        // Aquí debes obtener el ID del usuario actual, por ejemplo, desde la sesión
        UsuarioTO usuarioActual = loginController.getUsuarioTO();
        return usuarioActual != null ? usuarioActual.getId() : -1; // O manejar caso de usuario no logueado
    }

    public List<Publicacion> cargarFavoritos() {
        try {
            int idUsuario = obtenerIdUsuarioActual();
            return servicioFavorito.obtenerFavoritosPorUsuario(idUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void cargarCategorias() {
        try {
            categorias = servCategoria.obtenerTodasCategorias(); // Carga todas las categorías
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cerrarSesion() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
            if (session != null) {
                session.invalidate(); // Invalidar la sesión
            }
        }
        // Redirigir a la página de inicio de sesión u otra página después de cerrar sesión
        FacesContext.getCurrentInstance().getExternalContext().redirect("IniciarSesion.xhtml");
    }

    public void redireccionar(String ruta) {
        HttpServletRequest request;
        try {
            request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            FacesContext.getCurrentInstance().getExternalContext().redirect(request.getContextPath() + ruta);
        } catch (Exception e) {

        }
    }

    public void descargarDocumentos(Publicacion publicacion) {
        List<Documento> documentos = publicacion.getDocumentos();
        List<Imagen> imagenes = publicacion.getImagenes();

        try {
            // Configurar la respuesta HTTP
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.reset();
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment;filename=publicacion_" + publicacion.getId() + ".zip");

            // Crear el archivo ZIP
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());

            // Agregar documentos al ZIP
            for (Documento doc : documentos) {
                addToZipFile(doc.getUrl(), zos);
            }

            // Agregar imágenes al ZIP
            for (Imagen img : imagenes) {
                addToZipFile(img.getUrl(), zos);
            }

            zos.close();
            facesContext.responseComplete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addToZipFile(String fileUrl, ZipOutputStream zos) throws IOException {
        // Obtener la ruta absoluta del archivo en el servidor
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        String relativeWebPath = fileUrl.replaceFirst("http://localhost:8080/Red_Social_Academica", "");
        String absoluteDiskPath = externalContext.getRealPath(relativeWebPath);

        // Leer el archivo desde la ruta absoluta
        File file = new File(absoluteDiskPath);
        if (!file.exists()) {
            throw new FileNotFoundException("El archivo no se encontró: " + absoluteDiskPath);
        }
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length); // Asegúrate de usar los tres parámetros
        }

        zos.closeEntry();
        fis.close();
    }

    public void onCategoriaChange(AjaxBehaviorEvent event) throws ClassNotFoundException {
        // Este método se llamará cada vez que se seleccione o deseleccione una categoría
        System.out.println("Categorías seleccionadas: " + categoriasSeleccionadas);
        // Aquí puedes agregar cualquier lógica adicional que desees ejecutar

        publicaciones.clear();

        if (categoriasSeleccionadas.size() != 0 || !categoriasSeleccionadas.isEmpty()) {
            for (String categoria : categoriasSeleccionadas) {
                for (Publicacion publi : servPublicacion.findAllByCategorias(categoria)) {
                    publicaciones.add(publi);
                }
            }
        } else {
            cargarPublicaciones(0);
        }
    }

    public void descargarDocumento(String url) {
        // Implementación del método para descargar documento
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public UsuarioTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioTO usuario) {
        this.usuario = usuario;
    }

    public List<Archivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivo> archivos) {
        this.archivos = archivos;
    }

    public List<UploadedFile> getFiles() {
        return files;
    }

    public void setFiles(List<UploadedFile> files) {
        this.files = files;
    }

    public List<ResponsiveOption> getResponsiveOptions1() {
        return responsiveOptions1;
    }

    public void setResponsiveOptions1(List<ResponsiveOption> responsiveOptions1) {
        this.responsiveOptions1 = responsiveOptions1;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public Imagen getImagen() {
        return imagen;
    }

    public void setImagen(Imagen imagen) {
        this.imagen = imagen;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    public List<Imagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<Imagen> imagenes) {
        this.imagenes = imagenes;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public UsuarioTO getUser() {
        return user;
    }

    public void setUser(UsuarioTO user) {
        this.user = user;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public String getCategoriaSeleccionada() {
        return categoriaSeleccionada;
    }

    public void setCategoriaSeleccionada(String categoriaSeleccionada) {
        this.categoriaSeleccionada = categoriaSeleccionada;
    }

    public ServicioFavorito getServicioFavorito() {
        return servicioFavorito;
    }

    public void setServicioFavorito(ServicioFavorito servicioFavorito) {
        this.servicioFavorito = servicioFavorito;
    }

    public ServicioPublicacion getServPublicacion() {
        return servPublicacion;
    }

    public void setServPublicacion(ServicioPublicacion servPublicacion) {
        this.servPublicacion = servPublicacion;
    }

    public ServicioCategoria getServCategoria() {
        return servCategoria;
    }

    public void setServCategoria(ServicioCategoria servCategoria) {
        this.servCategoria = servCategoria;
    }

    public Archivo getArchivo() {
        return archivo;
    }

    public void setArchivo(Archivo archivo) {
        this.archivo = archivo;
    }

    public List<String> getCategoriasSeleccionadas() {
        return categoriasSeleccionadas;
    }

    public void setCategoriasSeleccionadas(List<String> categoriasSeleccionadas) {
        this.categoriasSeleccionadas = categoriasSeleccionadas;
    }

}

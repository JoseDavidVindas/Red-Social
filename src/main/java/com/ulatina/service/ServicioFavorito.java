package com.ulatina.service;

import com.ulatina.model.Documento;
import com.ulatina.model.Imagen;
import com.ulatina.model.Publicacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class ServicioFavorito extends Servicio {

    public void agregarFavorito(int idUsuario, int idPublicacion) {
        String sql = "INSERT INTO favoritos (id_usuario, id_publicacion) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Conectar();
            conn = getConexion();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idUsuario);
            pstmt.setInt(2, idPublicacion);
            pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            CerrarStatement(pstmt);
            Desconectar();
        }
    }

    public void agregarArchivoFavorito(int idUsuario, int idPublicacion) {
        String sql = "INSERT INTO favorito (id_usuario, id_publicacion) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Conectar();
            conn = getConexion();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idUsuario);
            pstmt.setInt(2, idPublicacion);
            pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            CerrarStatement(pstmt);
            Desconectar();
        }
    }



  public List<Publicacion> obtenerFavoritosPorUsuario(int idUsuario) throws SQLException, ClassNotFoundException {
        List<Publicacion> publicacionesFavoritas = new ArrayList<>();
        String sql = "SELECT p.* FROM publicacion p " +
                     "JOIN favorito f ON p.id = f.id_publicacion " +
                     "WHERE f.id_usuario = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;
        ServicioUsuario servUsuario = new ServicioUsuario();
        
        try {
            Conectar();
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Publicacion publicacion = new Publicacion();
                publicacion.setId(rs.getInt("id"));
                publicacion.setDescripcion(rs.getString("descripcion"));
                publicacion.setUsuario(servUsuario.usuarioPK(rs.getInt("usuario_id")));
                publicacion.setFecha_publicacion(rs.getTimestamp("fecha_publicacion"));
                publicacion.setFecha_actualizacion(rs.getTimestamp("fecha_actualizacion"));
                publicacion.setNumero_favoritos(rs.getInt("numero_favoritos"));
                publicacion.setCategoria(rs.getString("categoria"));

                // Recuperar documentos favoritos
                ServicioArchivo servA = new ServicioArchivo();
                publicacion.setDocumentos(servA.buscarDocumento(publicacion));

                // Recuperar imágenes favoritas
                publicacion.setImagenes(servA.buscarImagen(publicacion));

                publicacionesFavoritas.add(publicacion);
            }
        } finally {
            // Asegúrate de cerrar los recursos
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            Desconectar();
        }
        
        return publicacionesFavoritas;
    }
}
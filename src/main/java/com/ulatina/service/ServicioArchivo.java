/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.service;

import com.ulatina.model.Archivo;
import com.ulatina.model.Documento;
import com.ulatina.model.Imagen;
import com.ulatina.model.Publicacion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author josem
 */
public class ServicioArchivo extends Servicio{
    
    public Boolean insertarDocumento(Documento t) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try{
            Conectar();
            
            String sql = "INSERT INTO documento (url,id_publicacion) VALUES (?,?)";
            stmt = getConexion().prepareStatement(sql);
            stmt.setString(1, t.getUrl());
            stmt.setInt(2, t.getPublicacion().getId());
            
            int filasInsertadas = stmt.executeUpdate();
            
            if(filasInsertadas >0){
                return true;
            }else{
                return false;
            }
            
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally{
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }
    }
    
    public Boolean insertarImagen(Imagen t) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try{
            Conectar();
            
            String sql = "INSERT INTO imagen (url,id_publicacion) VALUES (?,?)";
            stmt = getConexion().prepareStatement(sql);
            stmt.setString(1, t.getUrl());
            stmt.setInt(2, t.getPublicacion().getId());
            
            int filasInsertadas = stmt.executeUpdate();
            
            if(filasInsertadas >0){
                return true;
            }else{
                return false;
            }
            
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally{
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }
    }
    
    public List<Documento> buscarDocumento(Publicacion publicacion) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Documento> documentos = new ArrayList<>();
        
        try{
            Conectar();
            
            String sql = "Select * from documento where id_publicacion = ?";
            
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, publicacion.getId());
            rs = stmt.executeQuery();

            while (rs.next()) {
                Documento documento = new Documento();
                documento.setId(rs.getInt("id"));
                documento.setUrl(rs.getString("url"));
                documento.setPublicacion(publicacion);
                documentos.add(documento);
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }
        return documentos;
    }
    
    public List<Imagen> buscarImagen(Publicacion publicacion) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Imagen> imagenes = new ArrayList<>();
        
        try{
            Conectar();
            
            String sql = "Select * from imagen where id_publicacion = ?";
            
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, publicacion.getId());
            rs = stmt.executeQuery();

            while (rs.next()) {
                Imagen imagen = new Imagen();
                imagen.setId(rs.getInt("id"));
                imagen.setUrl(rs.getString("url"));
                imagen.setPublicacion(publicacion);
                imagenes.add(imagen);
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }
        return imagenes;
    }
    
    public Boolean eliminarDocumento(Publicacion t) throws ClassNotFoundException {
        PreparedStatement stmt = null;
        boolean exito = false;

        try {
            Conectar();
            String sql = "DELETE FROM documento WHERE id_publicacion = ?";
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, t.getId());

            int filasAfectadas = stmt.executeUpdate();
            exito = filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CerrarStatement(stmt);
            Desconectar();
        }
        return exito;
    }
    public Boolean eliminarImagen(Publicacion t) throws ClassNotFoundException {
        PreparedStatement stmt = null;
        boolean exito = false;

        try {
            Conectar();
            String sql = "DELETE FROM imagen WHERE id_publicacion = ?";
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, t.getId());

            int filasAfectadas = stmt.executeUpdate();
            exito = filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CerrarStatement(stmt);
            Desconectar();
        }
        return exito;
    }
    
    public Boolean eliminarFavorito(Publicacion t) throws ClassNotFoundException {
        PreparedStatement stmt = null;
        boolean exito = false;

        try {
            Conectar();
            String sql = "DELETE FROM favorito WHERE id_publicacion = ?";
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, t.getId());

            int filasAfectadas = stmt.executeUpdate();
            exito = filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CerrarStatement(stmt);
            Desconectar();
        }
        return exito;
    }
    
    
}

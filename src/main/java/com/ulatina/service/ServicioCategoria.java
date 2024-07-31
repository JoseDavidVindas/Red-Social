package com.ulatina.service;

import com.ulatina.model.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ServicioCategoria extends Servicio {

    private static final String SQL_SELECT_CATEGORIAS = "SELECT id, categoria FROM categoria";


    public List<Categoria> obtenerTodasCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        Connection conexion = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Conectar();
            conexion = getConexion();
            stmt = conexion.prepareStatement(SQL_SELECT_CATEGORIAS);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setCategoria(rs.getString("categoria"));
                categorias.add(categoria);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }

        return categorias;
    }
}
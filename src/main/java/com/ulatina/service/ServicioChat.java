/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.service;

import com.ulatina.model.Chat;
import com.ulatina.model.Conversacion;
import com.ulatina.model.UsuarioTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author josem
 */
public class ServicioChat extends Servicio {

    public Chat CrearChat(Chat chat) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Conectar();

            String sql = "INSERT INTO chat (id_usuario1,id_usuario2) VALUES (?,?)";
            stmt = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, chat.getUsuario1().getId());
            stmt.setInt(2, chat.getUsuario2().getId());

            int filasInsertadas = stmt.executeUpdate();

            if (filasInsertadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    chat.setId(rs.getInt(1));
                }
                return chat;
            } else {
                return chat;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }
    }

    public Conversacion AgregarMensaje(Conversacion conversacion) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Conectar();

            String sql = "INSERT INTO conversacion (id_chat,id_usuario,mensaje,estado) VALUES (?,?,?,?)";
            stmt = getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, conversacion.getChat().getId());
            stmt.setInt(2, conversacion.getUsuario().getId());
            stmt.setString(3, conversacion.getMensaje());
            stmt.setString(4, conversacion.getEstado());

            int filasInsertadas = stmt.executeUpdate();

            if (filasInsertadas > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    conversacion.setId(rs.getInt(1));
                    conversacion = buscarMensaje(conversacion);
                }
                return conversacion;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }
    }

    public List<Chat> ChatsUsuario(UsuarioTO usuario) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Chat> chats = new ArrayList<>();
        UsuarioTO usuario1 = new UsuarioTO();
        UsuarioTO usuario2 = new UsuarioTO();
        ServicioUsuario servU = new ServicioUsuario();
        String sql;

        try {

            Conectar();

            sql = "SELECT * FROM chat WHERE id_usuario1 = ? ";
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, usuario.getId());
            rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                usuario1 = servU.usuarioPK(rs.getInt("id_usuario1"));
                usuario2 = servU.usuarioPK(rs.getInt("id_usuario2"));

                Chat chat = new Chat(id, usuario1, usuario2);
                chats.add(chat);
            }

            sql = "SELECT * FROM chat WHERE id_usuario2 = ?";
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, usuario.getId());
            rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                usuario1 = servU.usuarioPK(rs.getInt("id_usuario1"));
                usuario2 = servU.usuarioPK(rs.getInt("id_usuario2"));

                Chat chat = new Chat(id, usuario2, usuario1);
                chats.add(chat);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Paso 5
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }
        return chats;
    }

    public List<Conversacion> HistorialChat(Chat chat) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Conversacion> historial = new ArrayList<>();
        UsuarioTO usuario = new UsuarioTO();
        ServicioUsuario servU = new ServicioUsuario();

        try {

            Conectar();

            String sql = "SELECT * FROM conversacion WHERE id_chat = ?";
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, chat.getId());
            rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                usuario = servU.usuarioPK(rs.getInt("id_usuario"));
                String mensaje = rs.getString("mensaje");
                Timestamp fecha = rs.getTimestamp("fecha");
                String estado = rs.getString("estado");

                Conversacion conversacion = new Conversacion(id, chat, usuario, mensaje, fecha, estado);
                historial.add(conversacion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Paso 5
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }
        return historial;
    }

    public Chat BuscarChat(UsuarioTO usuario1, UsuarioTO usuario2) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ServicioUsuario servU = new ServicioUsuario();
        String sql;
        Chat chat = new Chat();

        try {

            Conectar();

            sql = "SELECT * FROM chat WHERE id_usuario1 = ? AND id_usuario2 = ?";
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, usuario1.getId());
            stmt.setInt(2, usuario2.getId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                usuario1 = servU.usuarioPK(rs.getInt("id_usuario1"));
                usuario2 = servU.usuarioPK(rs.getInt("id_usuario2"));

                chat.setId(id);
                chat.setUsuario1(usuario1);
                chat.setUsuario2(usuario2);

            } else {
                sql = "SELECT * FROM chat WHERE id_usuario1 = ? AND id_usuario2 = ?";
                stmt = getConexion().prepareStatement(sql);
                stmt.setInt(1, usuario2.getId());
                stmt.setInt(2, usuario1.getId());
                rs = stmt.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("id");
                    usuario1 = servU.usuarioPK(rs.getInt("id_usuario1"));
                    usuario2 = servU.usuarioPK(rs.getInt("id_usuario2"));

                    chat.setId(id);
                    chat.setUsuario1(usuario2);
                    chat.setUsuario2(usuario1);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Paso 5
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }
        return chat;
    }

    public Conversacion buscarMensaje(Conversacion conversacion) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            Conectar();

            String sql = "SELECT * FROM conversacion WHERE id = ?";
            stmt = getConexion().prepareStatement(sql);
            stmt.setInt(1, conversacion.getId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp fecha = rs.getTimestamp("fecha");

                conversacion.setFecha(fecha);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Paso 5
            CerrarResultSet(rs);
            CerrarStatement(stmt);
            Desconectar();
        }
        return conversacion;
    }

    public Conversacion actualizarConversacion(Conversacion conversacion) {
        PreparedStatement stmt = null;

        try {
            Conectar();

            String sql = "UPDATE conversacion SET estado = ? WHERE id = ?";
            stmt = getConexion().prepareStatement(sql);
            stmt.setString(1, conversacion.getEstado());
            stmt.setInt(2, conversacion.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Conversación actualizada exitosamente.");
            } else {
                System.out.println("No se encontró ninguna conversación con el ID proporcionado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CerrarStatement(stmt);
            Desconectar();
        }
        return conversacion;
    }

}

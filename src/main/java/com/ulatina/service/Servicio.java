/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author josem
 */
public class Servicio {
    
    private Connection conexion = null;
    private String host = "localhost";
    private String puerto = "3306";
    private String sid = "red_social_academica";
    private String usuario = "root";
    private String clave = "12349876";
    
    public void Conectar() throws ClassNotFoundException, SQLException{
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        conexion = DriverManager.getConnection("jdbc:mysql://"+host+":"+puerto+"/"+sid+"?serverTimezone=UTC",usuario,clave);
        
    }
    
    public void Desconectar(){
        try{
            if(conexion != null && !conexion.isClosed()){
                conexion.close();
                conexion =null;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public Connection getConexion(){
        return this.conexion;
    }
    
    public void CerrarStatement(PreparedStatement stmt){
        try{
            if(stmt != null && !stmt.isClosed()){
                stmt.close();
                stmt = null;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void CerrarResultSet(ResultSet rs){
        try{
            if(rs != null && !rs.isClosed()){
                rs.close();
                rs = null;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
}

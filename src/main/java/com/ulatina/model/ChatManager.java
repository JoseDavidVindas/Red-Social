/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.model;

/**
 *
 * @author josem
 */
public class ChatManager {
     // Instancia única de ChatManager
    private static ChatManager instance;
    
    // Instancia del objeto Chat
    private Chat chat;

    // Constructor privado
    private ChatManager() {
        // Inicializa el objeto Chat
        chat = new Chat();
    }

    // Método estático para obtener la instancia única
    public static ChatManager getInstance() {
        if (instance == null) {
            synchronized (ChatManager.class) {
                if (instance == null) {
                    instance = new ChatManager();
                }
            }
        }
        return instance;
    }

    // Método para obtener la instancia de Chat
    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
    
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.controller;

import com.ulatina.model.Chat;
import com.ulatina.model.ChatManager;
import com.ulatina.model.Conversacion;
import com.ulatina.service.ServicioChat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author josem
 */
@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private List<Conversacion> messageHistory = new ArrayList<>();
    private static final Map<Session, Chat> sessionChats = new ConcurrentHashMap<>();
    private ChatManager chatManager;
    //private Chat chat;
    //private UsuarioTO usuario;

    public ChatEndpoint() {
        chatManager = ChatManager.getInstance();
        //usuario = chat.getUsuario1();
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("New session opened: " + session.getId());

        // Obtener el chat del singleton
        Chat chat = chatManager.getChat();
        sessionChats.put(session, chat);

        // Instanciar ServicioChat y obtener el historial del chat
        ServicioChat servC = new ServicioChat();
        messageHistory = servC.HistorialChat(chat);

        for (int i = messageHistory.size() - 1; i >= 0; i--) {
            Conversacion conversacion = messageHistory.get(i);

            if (chat.getUsuario1().getId() != conversacion.getUsuario().getId()) {
                if ("Visto".equals(conversacion.getEstado())) {
                    break;
                } else {
                    conversacion.setEstado("Visto");

                    // Actualizar la conversación en la base de datos
                    servC.actualizarConversacion(conversacion);

                    // Actualizar la conversación en el historial
                    messageHistory.set(i, conversacion);
                }
            }
        }

        // Limpiar la pantalla de chat de las sesiones del mismo chat
        for (Session s : sessions) {
            Chat chat2 = sessionChats.get(s);
            if (s.isOpen() && chat.getId() == chat2.getId()) {
                try {
                    s.getBasicRemote().sendText("COMMAND:CLEAR_CHAT");
                } catch (IOException e) {
                    System.out.println("Error sending clear command: " + e.getMessage());
                    // No cerrar la conexión aquí, solo reportar el error
                }
            }
        }

        // Send message history to the new session
        if (messageHistory != null && !messageHistory.isEmpty()) {
            for (Conversacion conversacion : messageHistory) {
                String message = conversacion.getId() + "|" + conversacion.getUsuario().getNombre() + "|" + conversacion.getFecha() + "|" + conversacion.getMensaje() + "|" + conversacion.getEstado() + "|" + conversacion.getUsuario().getId();

                for (Session s : sessions) {
                    Chat chat2 = sessionChats.get(s);
                    if (s.isOpen()) {
                        if (chat.getId() == chat2.getId()) {
                            try {
                                s.getBasicRemote().sendText(message);
                            } catch (IOException e) {
                                System.out.println("Error sending message: " + e.getMessage());
                                // No cerrar la conexión aquí, solo reportar el error
                            }
                        }

                    }
                }
            }
        } else {
            System.out.println("No hay mensajes en el historial.");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("Received message: " + message + " from session: " + session.getId());
        int idmensaje;
        ServicioChat servC = new ServicioChat();
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Message is empty");
            return;
        }

        // if que decide si es un menssaje para eliminar o enviar
        if (message.startsWith("DELETE|")) {
            String messageId = message.split("\\|")[1];
            System.out.println("" + messageId);
            idmensaje = Integer.parseInt(messageId);

            // Obtener el chat asociado a la sesión
            Chat chat = sessionChats.get(session);

            servC.EliminarConversacion(idmensaje);

            for (Conversacion conv : messageHistory) {
                if (conv.getId() == idmensaje) {
                    messageHistory.remove(conv);
                    break;
                }
            }

            // Send message history to the new session
            if (messageHistory != null && !messageHistory.isEmpty()) {
                for (Conversacion conversacion : messageHistory) {
                    String message1 = conversacion.getId() + "|" + conversacion.getUsuario().getNombre() + "|" + conversacion.getFecha() + "|" + conversacion.getMensaje() + "|" + conversacion.getEstado() + "|" + conversacion.getUsuario().getId();

                    for (Session s : sessions) {
                        Chat chat2 = sessionChats.get(s);
                        if (s.isOpen()) {
                            if (chat.getId() == chat2.getId()) {
                                try {
                                    s.getBasicRemote().sendText(message1);
                                } catch (IOException e) {
                                    System.out.println("Error sending message: " + e.getMessage());
                                    // No cerrar la conexión aquí, solo reportar el error
                                }
                            }

                        }
                    }
                }
            }

        } else {

            // Obtener el chat asociado a la sesión
            Chat chat = sessionChats.get(session);

            // Crear la nueva conversación
            Conversacion conversacion = new Conversacion();
            conversacion.setChat(chat);
            conversacion.setUsuario(chat.getUsuario1());
            conversacion.setMensaje(message);
            conversacion.setEstado("No visto");

            Conversacion conv = servC.AgregarMensaje(conversacion);

            if (conv.getId() != 0) {
                // Agregar la conversación al historial
                if (messageHistory == null) {
                    messageHistory = new ArrayList<>();
                }
                messageHistory.add(conv);

                for (Session s : sessions) {
                    Chat chat2 = sessionChats.get(s);

                    if (s.isOpen()) {
                        if (chat.getId() == chat2.getId()) {
                            if (chat2.getUsuario1().getId() != chat.getUsuario1().getId()) {
                                conversacion.setEstado("Visto");
                                conv = servC.actualizarConversacion(conversacion);
                            }
                        }
                    }
                }

                // Enviar el mensaje a todas las sesiones
                String formattedMessage = conv.getId() + "|" + conv.getUsuario().getNombre() + "|" + conv.getFecha() + "|" + conv.getMensaje() + "|" + conversacion.getEstado() + "|" + conv.getUsuario().getId();
                for (Session s : sessions) {
                    Chat chat2 = sessionChats.get(s);
                    if (s.isOpen()) {
                        if (chat.getId() == chat2.getId()) {
                            try {
                                s.getBasicRemote().sendText(formattedMessage);
                            } catch (IOException e) {
                                System.out.println("Error sending message: " + e.getMessage());
                                // No cerrar la conexión aquí, solo reportar el error
                            }
                        }

                    }
                }
            } else {
                System.out.println("No se logró enviar el mensaje");
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        sessions.remove(session);
        sessionChats.remove(session); // Eliminar la sesión del mapa
        System.out.println("Session closed: " + session.getId() + ", reason: " + reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error on session " + session.getId() + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }

    public List<Conversacion> getMessageHistory() {
        return messageHistory;
    }

    public void setMessageHistory(List<Conversacion> messageHistory) {
        this.messageHistory = messageHistory;
    }

}

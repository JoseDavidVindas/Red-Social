/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.controller;

import com.ulatina.model.Chat;
import com.ulatina.model.ChatManager;
import com.ulatina.model.Conversacion;
import com.ulatina.model.UsuarioTO;
import com.ulatina.service.ServicioChat;
import com.ulatina.service.ServicioUsuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author josem
 */
@ManagedBean(name = "chatController")
@SessionScoped
public class ChatController implements Serializable {

    private static final long serialVersionUID = 1L;
    private UsuarioTO usuario1;
    private UsuarioTO usuario2;
    @ManagedProperty(value = "#{loginController}")
    private LoginController loginController;
    @ManagedProperty(value = "#{busquedaController}")
    private BusquedaController busquedaController;
    private ServicioChat servC;
    private Chat chat;
    private List<Chat> chatsSinVer;
    private int notificacionChats;
    private List<Chat> chats;

    public ChatController() {
        usuario1 = new UsuarioTO();
        usuario2 = new UsuarioTO();
        chat = new Chat();
        servC = new ServicioChat();
        chatsSinVer = new ArrayList<>();
        notificacionChats = 0;
        chats = new ArrayList<>();
    }

    public void IniciarChat() {
        usuario2 = busquedaController.getUsuarioSeleccionado();
        usuario1 = loginController.getUsuarioTO();

       
            chat = servC.BuscarChat(usuario1, usuario2);

            // Obtener la instancia única de ChatManager
            ChatManager chatManager = ChatManager.getInstance();

            if (chat == null || chat.getId() == 0) {
                chat.setUsuario1(usuario1);
                chat.setUsuario2(usuario2);
                chat = servC.CrearChat(chat);
            }

            chatManager.setChat(chat);

            for (Chat chat1 : chatsSinVer) {
                if (chat1.getId() == chat.getId()) {
                    chatsSinVer.remove(chat);
                    notificacionChats = notificacionChats - 1;
                    break;
                }
            }

            this.redireccionar("/Chat.xhtml");
        

    }

    public void IrChat(int idUsuario2) {
        ServicioUsuario servU = new ServicioUsuario();
        UsuarioTO user2 = new UsuarioTO();

        user2 = servU.usuarioPK(idUsuario2);

        usuario1 = loginController.getUsuarioTO();

        

            chat = servC.BuscarChat(usuario1, user2);

            // Obtener la instancia única de ChatManager
            ChatManager chatManager = ChatManager.getInstance();

            if (chat == null || chat.getId() == 0) {
                chat.setUsuario1(usuario1);
                chat.setUsuario2(user2);
                chat = servC.CrearChat(chat);
            }

            chatManager.setChat(chat);

            for (Chat chat1 : chatsSinVer) {
                if (chat1.getId() == chat.getId()) {
                    chatsSinVer.remove(chat);
                    notificacionChats = notificacionChats - 1;
                    break;
                }
            }

            this.redireccionar("/Chat.xhtml");
        

    }

    @PostConstruct
    public void init() {
        Chats();
    }

    public void Chats() {
        UsuarioTO user = new UsuarioTO();
        user = loginController.getUsuarioTO();
        if (user.getId() != usuario1.getId()) {
            usuario1 = user;
            notificacionChats = 0;
        }
        List<Conversacion> historial = new ArrayList<>();

        chats = servC.ChatsUsuario(usuario1);

        for (Chat chat : chats) {
            historial = servC.HistorialChat(chat);

            for (int i = historial.size() - 1; i >= 0; i--) {
                Conversacion conversacion = historial.get(i);

                if (usuario1.getId() != conversacion.getUsuario().getId()) {
                    if ("Visto".equals(conversacion.getEstado())) {
                        Boolean flag = false;
                        for (Chat chat1 : chatsSinVer) {
                            if (chat1.getId() == chat.getId()) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            chatsSinVer.remove(chat);
                            notificacionChats = notificacionChats - 1;
                            break;
                        }
                        break;
                    } else if ("No visto".equals(conversacion.getEstado())) {

                        if (chatsSinVer.size() == 0) {

                            chatsSinVer.add(chat);
                            notificacionChats = notificacionChats + 1;
                            break;

                        } else {
                            Boolean flag = false;
                            for (Chat chat1 : chatsSinVer) {
                                if (chat1.getId() == chat.getId()) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                chatsSinVer.add(chat);
                                notificacionChats = notificacionChats + 1;
                                break;
                            }

                        }
                        break;
                    }
                }
            }
        }
    }

    public void redireccionar(String ruta) {
        HttpServletRequest request;
        try {
            request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            FacesContext.getCurrentInstance().getExternalContext().redirect(request.getContextPath() + ruta);
        } catch (Exception e) {

        }
    }
    
    public void MensajeEliminado(){
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Mensaje eliminado"));
    }

    public UsuarioTO getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(UsuarioTO usuario1) {
        this.usuario1 = usuario1;
    }

    public UsuarioTO getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(UsuarioTO usuario2) {
        this.usuario2 = usuario2;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public BusquedaController getBusquedaController() {
        return busquedaController;
    }

    public void setBusquedaController(BusquedaController busquedaController) {
        this.busquedaController = busquedaController;
    }

    public ServicioChat getServC() {
        return servC;
    }

    public void setServC(ServicioChat servC) {
        this.servC = servC;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public List<Chat> getChatsSinVer() {
        return chatsSinVer;
    }

    public void setChatsSinVer(List<Chat> chatsSinVer) {
        this.chatsSinVer = chatsSinVer;
    }

    public int getNotificacionChats() {
        return notificacionChats;
    }

    public void setNotificacionChats(int notificacionChats) {
        this.notificacionChats = notificacionChats;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

}

package br.com.thallysprojetos.common_dtos.auth;

import br.com.thallysprojetos.common_dtos.usuario.Role;

public class LoginResponseDTO {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String userName;
    private String email;
    private Role role;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, Long userId, String userName, String email, Role role) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

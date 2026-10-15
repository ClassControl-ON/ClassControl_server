/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.time.LocalDate;

public class Usuarios {

private int id_usuarios;
private String nombres;
private String apellidos;
private String identificacion;
private LocalDate fecha_Nacimiento;
private String correo;
private String telefono;
private String direccion;
private String username;
private String nivel_Educativo;
private String profesion;
private String clave;
private LocalDate fecha_Creacion = LocalDate.now();
private boolean activo;
private LocalDate fecha_ExpiracionContraseña = LocalDate.now().plusDays(90);
private int Roles_id_roles;
private int Tipo_Documento_id_tipo_Documento;
private int Tipo_vinculacion_id_tipo_vinculacion;
/** Ficha a la que pertenece el Aprendiz. Null para otros roles. */
private Integer Ficha_id_ficha;

    public int getId_usuarios() {
        return id_usuarios;
    }

    public void setId_usuarios(int id_usuarios) {
        this.id_usuarios = id_usuarios;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public LocalDate getFecha_Nacimiento() {
        return fecha_Nacimiento;
    }

    public void setFecha_Nacimiento(LocalDate fecha_Nacimiento) {
        this.fecha_Nacimiento = fecha_Nacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNivel_Educativo() {
        return nivel_Educativo;
    }

    public void setNivel_Educativo(String nivel_Educativo) {
        this.nivel_Educativo = nivel_Educativo;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
        this.fecha_ExpiracionContraseña = LocalDate.now().plusDays(90);
    }

    public LocalDate getFecha_Creacion() {
        return fecha_Creacion;
    }

    public void setFecha_Creacion(LocalDate fecha_Creacion) {
        this.fecha_Creacion = fecha_Creacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFecha_ExpiracionContraseña() {
        return fecha_ExpiracionContraseña;
    }

    public void setFecha_ExpiracionContraseña(LocalDate fecha_ExpiracionContraseña) {
        this.fecha_ExpiracionContraseña = fecha_ExpiracionContraseña;
    }

    public int getRoles_id_roles() {
        return Roles_id_roles;
    }

    public void setRoles_id_roles(int Roles_id_roles) {
        this.Roles_id_roles = Roles_id_roles;
    }

    public int getTipo_Documento_id_tipo_Documento() {
        return Tipo_Documento_id_tipo_Documento;
    }

    public void setTipo_Documento_id_tipo_Documento(int Tipo_Documento_id_tipo_Documento) {
        this.Tipo_Documento_id_tipo_Documento = Tipo_Documento_id_tipo_Documento;
    }

    public int getTipo_vinculacion_id_tipo_vinculacion() {
        return Tipo_vinculacion_id_tipo_vinculacion;
    }

    public void setTipo_vinculacion_id_tipo_vinculacion(int Tipo_vinculacion_id_tipo_vinculacion) {
        this.Tipo_vinculacion_id_tipo_vinculacion = Tipo_vinculacion_id_tipo_vinculacion;
    }

    public Integer getFicha_id_ficha() {
        return Ficha_id_ficha;
    }

    public void setFicha_id_ficha(Integer Ficha_id_ficha) {
        this.Ficha_id_ficha = Ficha_id_ficha;
    }

    public boolean isContraseñaExpirada() {
        if (fecha_ExpiracionContraseña == null) return true;
        return LocalDate.now().isAfter(fecha_ExpiracionContraseña);
    }

}
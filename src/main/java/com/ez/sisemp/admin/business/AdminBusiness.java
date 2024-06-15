package com.ez.sisemp.admin.business;

import com.ez.sisemp.admin.dao.AdminDao;
import com.ez.sisemp.admin.exception.UsuariosNotFoundException;
import com.ez.sisemp.admin.model.Usuario;
import com.ez.sisemp.login.entity.UsuarioEntity;
import com.ez.sisemp.parametro.dao.ParametroDao;

import java.util.ArrayList;
import java.util.List;

public class AdminBusiness {

    private final AdminDao adminDao;
    private final ParametroDao parametroDao;

    public AdminBusiness() {
        this.adminDao = new AdminDao();
        this.parametroDao = new ParametroDao();

    }

    public List<Usuario> obtenerUsuariosJpa() {
        var usuarios = adminDao.obtenerUsuariosJPA();
        if(usuarios.isEmpty()) {
            throw new UsuariosNotFoundException("No se encontraron usuarios");
        }

        var usuariosToReturn = new ArrayList<Usuario>();

        usuarios.forEach(
                u -> {
                    var usuarioRecord = mapToRecord(u);
                    usuariosToReturn.add(usuarioRecord);
                }
        );
        return usuariosToReturn;
    }

    private Usuario mapToRecord(UsuarioEntity u) {
        var rol = parametroDao.getRoleById(u.getIdRol());
        boolean estado = false;
        if(u.getEstado() == 1) {
            estado = true;
        } else {
            estado = false;
        }
        return new Usuario(
                u.getNombreUsuario(),
                u.getContrasena(),
                "",
                u.getUltimaConexion(),
                estado,
                u.getPrimerNombre(),
                u.getApellidoPaterno(),
                u.getFotoPerfil(),
                rol.getNombre()
        );
    }


}

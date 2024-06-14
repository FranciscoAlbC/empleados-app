package com.ez.sisemp.empleado.servlet;

import com.ez.sisemp.empleado.business.EmpleadoBusiness;
import com.ez.sisemp.empleado.entity.EmpleadoEntity;
import com.ez.sisemp.empleado.exception.EmailAlreadyInUseException;
import com.ez.sisemp.empleado.model.Empleado;
import com.ez.sisemp.parametro.dao.ParametroDao;
import com.ez.sisemp.parametro.model.Departamento;
import com.ez.sisemp.shared.enums.Routes;
import com.ez.sisemp.shared.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/empleado/editar")
public class EditarEmpleadoServlet extends HttpServlet {

     private static final String EMPLEADO_JSP = "/empleado/editar.jsp";
     private static final String ERROR_SERVER = "Error interno del servidor";
     private EmpleadoBusiness empleadoBusiness;
     private ParametroDao parametroDao;

    @Override
    public void init() throws ServletException {
        super.init();
        empleadoBusiness = new EmpleadoBusiness();
        parametroDao = new ParametroDao();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        Long idFloat = Long.parseLong(id);

        EmpleadoEntity empleado = empleadoBusiness.consultarEmpleadoById(idFloat);
        req.setAttribute("empleado", empleado);

        loadDepartamentos(req);
        req.getRequestDispatcher(EMPLEADO_JSP).forward(req, resp);
    }


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (!SessionUtils.validarSesion(req, resp)) {
            return;
        }
        try {
            EmpleadoEntity empleadoEntity = createEmpleadoEntityFromRequest(req);
            empleadoBusiness.editarEmpleado(empleadoEntity);
            req.setAttribute("msj", "Empleado editado correctamente");
            resp.sendRedirect(Routes.EMPLEADO.getRoute());
        } catch (ParseException e) {
            handleParseException(req, resp, e);
        } catch (EmailAlreadyInUseException e){
            handleEmailAlreadyInUseException(req, resp, e);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }


    //Entidad para pasar al metodo EditarEmpleado
    private EmpleadoEntity createEmpleadoEntityFromRequest(HttpServletRequest req) throws ParseException {
        String strDate = req.getParameter("fechaNacimiento");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        EmpleadoEntity empleadoEntity = new EmpleadoEntity();

        Long id = Long.parseLong(req.getParameter("id"));
        String codigoEmpleado = req.getParameter("codigoEmpleado");


        empleadoEntity.setId(id);
        empleadoEntity.setCodigoEmpleado(codigoEmpleado);
        //empleadoEntity.setEstado(estado);
        empleadoEntity.setNombres(req.getParameter("nombres"));
        empleadoEntity.setApellidoPat(req.getParameter("apellidoPat"));
        empleadoEntity.setApellidoMat(req.getParameter("apellidoMat"));
        empleadoEntity.setIdDepartamento(Integer.parseInt(req.getParameter("idDepartamento")));
        empleadoEntity.setCorreo(req.getParameter("correo"));
        empleadoEntity.setSalario(Double.parseDouble(req.getParameter("salario")));
        empleadoEntity.setFechaNacimiento(sdf.parse(strDate));
        return empleadoEntity;
    }

    private void handleParseException(HttpServletRequest request, HttpServletResponse response, ParseException e) throws ServletException, IOException {
        loadDepartamentos(request);
        request.setAttribute("error", "Fecha Nacimiento no válido, el formato debe ser yyyy-MM-dd");
        request.getRequestDispatcher("/empleado/registrar.jsp").forward(request, response);
    }

    private void handleEmailAlreadyInUseException(HttpServletRequest request, HttpServletResponse response, EmailAlreadyInUseException e) throws ServletException, IOException {
        loadDepartamentos(request);
        request.setAttribute("error", e.getMessage());
        request.getRequestDispatcher("/empleado/registrar.jsp").forward(request, response);
    }

    private void loadDepartamentos(HttpServletRequest req)  {
        List<Departamento> departamentos = parametroDao.obtenerDepartamentos();
        req.setAttribute("departamentos", departamentos);
    }
}

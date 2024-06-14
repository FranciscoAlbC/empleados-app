package com.ez.sisemp.empleado.dao;

import com.ez.sisemp.empleado.entity.EmpleadoEntity;
import com.ez.sisemp.empleado.model.Empleado;
import com.ez.sisemp.shared.config.MySQLConnection;
//importaciones de JAKARTA
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDao{
    private static final String SQL_GET_ALL_EMPLEADOS = """
    SELECT 
        e.id, 
        e.codigo_empleado, 
        e.nombres, 
        e.apellido_pat, 
        e.apellido_mat, 
        d.nombre AS departamento, 
        e.correo, 
        FLOOR(DATEDIFF(NOW(), e.fecha_nacimiento) / 365.25) AS edad, 
        e.salario 
    FROM 
        empleado e
    INNER JOIN departamentos d ON d.id = e.id_departamento  
    WHERE 
        e.activo = 1;
    """;

    //JPQL para JPA
    private static final String SQL_GET_ALL_EMPLEADOS_JPQL = """
            SELECT e
            FROM EmpleadoEntity e
            """;
    private static final String JPQL_DELETE_EMPLEADO = "UPDATE EmpleadoEntity e SET e.estado = 0 WHERE e.id = :id";


    private static String SQL_UPDATE_EMPLEADO = "UPDATE empleado SET nombres = ?, apellido_pat = ?, apellido_mat = ?, id_departamento = ?, correo = ?, salario = ? WHERE id = ?;";
    private static String SQL_DELETE_EMPLEADO = "UPDATE empleado set activo=0 WHERE id = ?;";
    private static String SQL_INSERT_EMPLEADO = "INSERT INTO empleado (codigo_empleado, nombres, apellido_pat, apellido_mat, id_departamento, correo, fecha_nacimiento, salario) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
    private static String SQL_GET_NEW_EMPLEADO_CODE = "SELECT CONCAT('EMP', LPAD(MAX(CAST(SUBSTRING(codigo_empleado, 4) AS UNSIGNED)) + 1, 4, '0')) AS next_emp_code FROM empleado;";

    public List<Empleado> obtenerEmpleados() throws SQLException, ClassNotFoundException {
        List<Empleado> empleados = new ArrayList<>();
        PreparedStatement preparedStatement = MySQLConnection.getConnection()
                                                .prepareStatement(SQL_GET_ALL_EMPLEADOS);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            empleados.add(mapResultSetToEmpleado(resultSet));
        }
        return empleados;
    }

    //Integracion JPA a obtener Empleado
    public List<EmpleadoEntity> obtenerEmpleadosJPA () {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //Obtener Lista de alumnos
        var empleados = entityManager.createQuery(SQL_GET_ALL_EMPLEADOS_JPQL, EmpleadoEntity.class).getResultList();

        return empleados;
    }

    public void editarEmpleado (Empleado empleado) throws SQLException, ClassNotFoundException {
        //TODO: Implementar la edición de un empleado
    }

    //Editar Empleado JPA 2 metodos
        // Consultar y Editar
    public EmpleadoEntity consultarEmpleadoById(Long id) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            var empleado = entityManager.find(EmpleadoEntity.class, id);
            return empleado;
        } finally {
            entityManager.close();
        }
    }

    public void editarEmpleado(EmpleadoEntity empleado){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.merge(empleado);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }




    public void eliminarEmpleado(int id) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = MySQLConnection.getConnection()
                                                .prepareStatement(SQL_DELETE_EMPLEADO);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    //Eliminar empleado (UPDATE) JPA
    public void eliminarEmpleadoJpa(int id) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Query query = entityManager.createQuery(JPQL_DELETE_EMPLEADO);
        query.setParameter("id", id);
        int rowsUpdated = query.executeUpdate();

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void agregarEmpleado(Empleado empleado) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = MySQLConnection.getConnection()
                                                .prepareStatement(SQL_INSERT_EMPLEADO);
        preparedStatement.setString(1, empleado.codigoEmpleado());
        preparedStatement.setString(2, empleado.nombres());
        preparedStatement.setString(3, empleado.apellidoPat());
        preparedStatement.setString(4, empleado.apellidoMat());
        preparedStatement.setInt(5, empleado.idDepartamento());
        preparedStatement.setString(6, empleado.correo());
        preparedStatement.setDate(7, new Date(empleado.fechaNacimiento().getTime()));
        preparedStatement.setDouble(8, empleado.salario());
        preparedStatement.executeUpdate();
    }

    private Empleado mapResultSetToEmpleado(ResultSet resultSet) throws SQLException {
        return new Empleado(resultSet.getInt("id"),
                resultSet.getString("codigo_empleado"),
                resultSet.getString("nombres"),
                resultSet.getString("apellido_pat"),
                resultSet.getString("apellido_mat"),
                resultSet.getString("departamento"),
                resultSet.getString("correo"),
                resultSet.getInt("edad"),
                resultSet.getDouble("salario")
        );
    }

    //Agregar empleado JPA
    public void agregarEmpleadoJPA(EmpleadoEntity empleado) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(empleado); //entidad 'pre armada'
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }
}

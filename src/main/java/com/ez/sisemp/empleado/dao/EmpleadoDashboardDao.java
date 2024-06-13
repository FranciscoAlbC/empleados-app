package com.ez.sisemp.empleado.dao;

import com.ez.sisemp.empleado.model.EmpleadoDashboard;
import com.ez.sisemp.shared.config.MySQLConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static com.ez.sisemp.shared.utils.EdadUtils.calcularEdad;

public class EmpleadoDashboardDao {

    private static final String SQL_GET_TOTAL_EMPLEADOS = "SELECT COUNT(*) FROM empleado";
    private static final String SQL_GET_PROMEDIO_EDAD = "SELECT FLOOR(AVG(DATEDIFF(NOW(), fecha_nacimiento) / 365.25)) AS avg_age FROM empleado;";
    private static final String SQL_GET_MAYOR_SALARIO = "SELECT MAX(salario) FROM empleado";
    private static final String SQL_GET_TOTAL_DEPARTAMENTOS = "SELECT COUNT(DISTINCT id_departamento) FROM empleado"; //TODO

    //Cadenas JPQL
    private static final String JPQL_GET_TOTAL_EMPLEADOS = "SELECT COUNT(e) FROM EmpleadoEntity e";
    private static final String JPQL_GET_EDAD = "SELECT e.fechaNacimiento FROM EmpleadoEntity e";
    private static final String JPQL_GET_MAYOR_SALARIO = "SELECT MAX(e.salario) FROM EmpleadoEntity e";
    private static final String JPQL_GET_TOTAL_DEPARTAMENTOS = "SELECT COUNT(DISTINCT e.idDepartamento) FROM EmpleadoEntity e";

    //metodo JPA
    public EmpleadoDashboard getJPA() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("devUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Long totalEmpleados = getTotalEmpleadosJPA(entityManager);
        Double promedioEdad = getPromedioEdadJPA(entityManager);
        Double mayorSalario = getMayorSalarioJPA(entityManager);
        Long totalDepartamentos = getTotalDepartamentosJPA(entityManager);

        entityManager.close();
        return new EmpleadoDashboard(totalEmpleados.intValue(), mayorSalario, promedioEdad.intValue(), totalDepartamentos.intValue());
    }

    //getters jpa
    public Long getTotalEmpleadosJPA(EntityManager entityManager) {
        TypedQuery<Long> query = entityManager.createQuery(JPQL_GET_TOTAL_EMPLEADOS, Long.class);
        return query.getSingleResult();
    };

    public Double getPromedioEdadJPA(EntityManager entityManager) {
        TypedQuery<Date> query = entityManager.createQuery(JPQL_GET_EDAD, Date.class);

        List<Date> fechasNacimiento = query.getResultList();
        if(!fechasNacimiento.isEmpty()) {
            long sumaEdades = 0;
            for(Date fechaNacimiento : fechasNacimiento) {
                sumaEdades += calcularEdad(fechaNacimiento);
            }
            return (double) sumaEdades / fechasNacimiento.size();
        } else {
            return null;
        }
    }

    public Double getMayorSalarioJPA(EntityManager entityManager) {
        TypedQuery<Double> query = entityManager.createQuery(JPQL_GET_MAYOR_SALARIO, Double.class);
        return query.getSingleResult();
    }

    public Long getTotalDepartamentosJPA(EntityManager entityManager) {
        TypedQuery<Long> query = entityManager.createQuery(JPQL_GET_TOTAL_DEPARTAMENTOS, Long.class);
        return query.getSingleResult();
    }



//----JDBC-------

    public EmpleadoDashboard get() throws SQLException, ClassNotFoundException {
        return new EmpleadoDashboard(
            getTotalEmpleados(),
            getMayorSalario(),
            getPromedioEdad(),
            getTotalDepartamentos()
        );
    }

    public int getTotalEmpleados() throws SQLException, ClassNotFoundException {
        var result = MySQLConnection.executeQuery(SQL_GET_TOTAL_EMPLEADOS);
        result.next();
        return result.getInt(1);
    }
    public int getPromedioEdad() throws SQLException, ClassNotFoundException {
       var result = MySQLConnection.executeQuery(SQL_GET_PROMEDIO_EDAD);
       result.next();
       return result.getInt(1);
    }
    public double getMayorSalario() throws SQLException, ClassNotFoundException {
        var result = MySQLConnection.executeQuery(SQL_GET_MAYOR_SALARIO);
        result.next();
        return result.getDouble(1);
    }
    public int getTotalDepartamentos() {
        return 0;
    }
}



package com.codeoftheweb.salvo;

public class Alumno extends Person{
    private int numeroAlumno;
    private double promedio;
    private String carrera;

    public Alumno() {
    }

    public Alumno(String nombre, String apellido, int edad, double altura, char genero, int numeroAlumno, double promedio, String carrera) {
        super(nombre, apellido, edad, altura, genero);
        this.numeroAlumno = numeroAlumno;
        this.promedio = promedio;
        this.carrera = carrera;
    }

    public int getNumeroAlumno() {
        return numeroAlumno;
    }

    public double getPromedio() {
        return promedio;
    }

    public String getCarrera() {
        return carrera;
    }
}

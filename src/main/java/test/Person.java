package test;

public class Person {
    private String nombre;
    private String apellido;
    private int edad;
    private double altura;
    private char genero;

    public Person() {
    }

    public Person(String nombre, String apellido, int edad, double altura, char genero) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.altura = altura;
        this.genero = genero;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public int getEdad() {
        return edad;
    }

    public double getAltura() {
        return altura;
    }

    public char getGenero() {
        return genero;
    }
}

package test;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("Holis");
        Alumno alejo = new Alumno("Alejo","Martinez",19,1.70,'M',12792,6.80,"Ingenieria");
        Person alejoP = (Person) alejo;
        System.out.println(alejoP.getNombre());
    }
}

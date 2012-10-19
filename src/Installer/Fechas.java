package Installer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;
/**
 *
 * @author Reed
 */
public class Fechas {
    public String dia;
    public String mes;
    public String año;
    public String hora;
    public String minuto;
    public String segundo;
    public String mili;
    private String fecha;
    public Fechas (String temp){
        //Separamos cada parámetro
        StringTokenizer token = new StringTokenizer (temp, "-");
        StringTokenizer fec = new StringTokenizer (token.nextToken(), "/");
        StringTokenizer hor = new StringTokenizer (token.nextToken(), "/");
        dia = fec.nextToken();
        mes = fec.nextToken();
        año = fec.nextToken();
        hora = hor.nextToken();
        minuto = hor.nextToken();
        segundo = hor.nextToken();
        mili = hor.nextToken();
        fecha = año + "/" + mes + "/" + dia + "/" + hora + "/" + minuto + "/" + segundo + "/" + mili;
    }
    public String getCadena() { return fecha;}
    //Método que devuelve true si esta fecha es más reciente
    public boolean compare(Fechas f){
        StringTokenizer cadena = new StringTokenizer(fecha, "/");
        boolean res = false, exit = false;
        StringTokenizer token = new StringTokenizer(f.getCadena(), "/");
        while (token.hasMoreTokens() && cadena.hasMoreTokens() && !exit){
            String tok = token.nextToken(), cad = cadena.nextToken();
            int A = Integer.parseInt(tok), B = Integer.parseInt(cad);
            int C = compareDate(A, B);
            if (C == 1){
                res = true;
                exit = true;
            } else if (C == -1){
                exit = true;
            }
        }
        
        return res;
    }
    //Método que devuelve 1 si A es más pequeño que B (reciente), 0 si A es igual que B (iguales) o -1 si A es mayor que B (antiguo)
    public int compareDate(int A, int B){
        int res = 0;
        if (A < B){
            res = 1;
        } else if (A > B){
            res = -1;
        }
        return res;
    }
    public String toString(){
        StringBuilder build = new StringBuilder().append("Copia de ").append(dia).append("/").append(mes).append("/").append(año).append(" - ").append(hora).append(":").append(minuto).append(":").append(segundo);
        return build.toString();
    }
}

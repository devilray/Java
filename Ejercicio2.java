package practica2;

import java.io.IOException;
import java.util.Scanner;

import afd.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Trabajo Realizado por:

// Domingo Poveda Lopez
// Juan Jose Marin Peralta
//validaAFD
public class Ejercicio2 {
	//error handler and constants
	//public static final String ERROR_LECTURA_FICHERO = "Error. No se puede leer el fichero, porque: 1) Se ha equivocado al introducir el nombre del fichero; 2) la extensión del fichero no es correcta.";
	public static final String ERROR_LECTURA_AUTOMATA = "Error. No se ha podido abrir el fichero del automata: ";

	//Regular Expression to validate the extension file format
	private static final String extension = "([^\\s]+(\\.(?i)(jff))$)"; //ONLY "jff" files
	private static final Pattern patronExtension = Pattern.compile(extension);
	public static boolean validaExtension(String extension){
		Matcher m = patronExtension.matcher(extension);
		boolean ok = m.matches();
		return ok;
	}

	/**
	 * Method to analyze if a automate is correct or not correct.
	 * @param automata
	 * @param cadena
	 * @return true if is correct and false if is not
	 */
	public static boolean esValida(Afd automata, String cadena){
		int longitud = cadena.length(), posicion = 0;

		//empezamos con el automata a 0. Para que podamos empezar la lectura del automata
		automata.reset();

		//mientas la longitud de la cadena sea mayor que la posicion
		//seguimos leyendo (mientras queden elementos por leer)
		while(posicion < longitud){
			automata.setSimboloActual(cadena.charAt(posicion));

			//si existe una transicion con el simbolo actual en el estado actual entonces
			//nos posicionamos sobre el
			if(automata.transicion(automata.getEstadoActual(), automata.getSimboloActual()) != null)
				automata.setEstadoActual(automata.transicion(automata.getEstadoActual(), automata.getSimboloActual()));
			else return false;

			//avanzamos de estado y posicion
			posicion += 1;
		}

		//si el estado actual es final devuelve true
        return automata.esFinal(automata.getEstadoActual()) == true;
	}

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner entrada = new Scanner(System.in);
		String cadena;
                String fichero;
		boolean error;

		do{
	        error = false;
	        try {
	        System.out.println("Por favor, introduzca el nombre del fichero (incluye extensión): ");
	        fichero = entrada.nextLine();

	            if(validaExtension(fichero)){
	                //creamos una instancia del objeto Afd con el fichero que le pasamos
	                //como parametro
	                Afd automata = new Afd(fichero);

	                //introduzco una cadena a analizar en el autamata
	                System.out.println("Por favor, introduzca las cadenas de entrada: ");
	                cadena = entrada.nextLine();

	                //recorro el automata para validar la cadena
	                //meto más de una cadena que esté separada por ";"
	                for(String resul : cadena.split(";")){
	                    //System.out.println(esValida(automata, resul));
	                	if(esValida(automata, resul))
	                			System.out.println("*** Cadena <" + resul + "> aceptada ***");
	                	else if(!esValida(automata, resul))
	                			System.out.println("*** Cadena <" + resul + "> rechazada ***");
	                }
	            }else {
	            	System.out.println("Por favor, introduzca el nombre del fichero (incluye extensión): ");
	            	//System.err.println(ERROR_LECTURA_FICHERO);
	            	error = true;
	            	entrada = new Scanner(System.in);
	            	cadena = entrada.nextLine();
	            }
	        } catch (IOException e) {
	                error = true;
	                System.err.println(ERROR_LECTURA_AUTOMATA + e.getMessage());
	                entrada = new Scanner(System.in);
	                cadena = entrada.nextLine();
	        }
		}while(error);
	}

}

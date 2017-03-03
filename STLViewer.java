package practica3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stl.Facet;
import stl.FacetList;
import stl.J3DViewer;

// Trabajo realizado por:

// Domingo Poveda Lopez
// Juan Jose Marin Peralta
public class STLViewer {
	//counts
	private static int contSolid = 0;
	private static int contFacet = 0;
	private static int contOuter = 0;
	private static int contVertex = 0;
	private static int contador = 1;

	//Errors handlers and constants
	public static final float INFINITO_POSITIVO = Float.POSITIVE_INFINITY;
	public static final float INFINITO_NEGATIVO = Float.NEGATIVE_INFINITY;
	public static final String TIPO_AVISO_1 = "Un vértice no está en el cuadrante positivo.";
	public static final String TIPO_AVISO_2 = "Un vector normal es incorrecto.";
	public static final String TIPO_SUBAVISO_21 = "no es un vector unitario.";
	public static final String TIPO_SUBAVISO_22 = "Es unitario, pero no está bien calculado.";
	public static final String ERROR_FUERA_DE_RANGO = "Error. Los valores del vector se encuentran fuera del rango. Se encuentra cercano a <Infinito Positivo> o <Infinito Negativo>.";
	public static final String ERROR_VECTOR_MAL_CALCULADO = "Error: El vector normal está mal calculado. Línea ";
	public static final String ERROR_LA_NORMAL_NO_ES_UN_VECTOR = TIPO_AVISO_2 + "La normal " + TIPO_SUBAVISO_21;
	public static final String ERROR_FORMATO = "Posible error 1: Al poner \"endsolid nombre\" el nombre tiene que ser el mismo que aparezca en \"solid nombre\"\n"
			+ "Posible error 2: No hay una expresión al principio de la forma \"solid nombre\"";
	public static final String ERROR_1_SOLID = "Error en línea " + contador + ": Ya existe una expresión de la forma \"solid nombre\" antes de esta expresión.";
	public static final String ERROR_2_SOLID = "Error de formato: La primera línea del fichero tiene que tener la forma \"solid nombre\"";
	public static final String ERROR_FORMATO_SOLID = "Error de formato: No hay una expresión de la forma \"solid nombre\" antes de la expresión de la línea "+ contador;
	public static final String ERROR_1_FACET = "Error en línea " + contador + ": Ya existe una expresión de la forma \"facet normal vx vy vz\" antes de esta expresión.";
	public static final String ERROR_2_FACET = "Error de formato: Antes de la expresión de la línea " + contador + " tiene que aparecer la expresión \"facet normal nx ny nz\"";
	public static final String ERROR_1_OUTER = "Error en línea " + contador + ": Ya existe una expresión de la forma \"outer loop\" antes de esta expresión.";
	public static final String ERROR_FORMATO_OUTER = "Error de formato: Antes de la expresión de la línea "+ contador +" tiene que aparecer la expresión \"outer loop\"";
	public static final String ERROR_FORMATO_FACET = "Error de formato: Antes de la expresión de la línea "+ contador +" tiene que aparecer la expresión \"facet normal nx ny nz\"";
	public static final String ERROR_FORMATO_TRIANGULO = "Error de formato: El número de vértices del triángulo debe ser 3";
	public static final String ERROR_FORMATO_END_OUTER = "Error de formato: Antes de la expresión de la línea "+ contador +" tiene que aparecer la expresión \"endouter loop\"";
	public static final String SUCCESSFUL_FILE_ANALIZING = "¡Análisis del fichero STL completo!";
	public static final double ERROR_ADMITIDO_NORMAL = 1.0E-06;
	public static final double MAX_VALOR_VECTORES_IGUALES_JAVA = 1.0E-03;
	public static final int ANCHO = 800;
	public static final int ALTO = 600;

	//FACET
	public static FacetList listFacets = new FacetList();
	//*************************************************
	//public static final String NOMBRE_ARCHIVO = "E:\\workspaces\\JAVA\\ProyectoAutomatas\\src\\ficherosSTL\\test.stl";

	//Auxiliary float VAR
	private static String stringFloat1 = "";
	private static String stringFloat2 = "";
	private static String stringFloat3 = "";
	//*************************************************
	private static float vertexV1x = 0;
	private static float vertexV2x = 0;
	private static float vertexV3x = 0;
	private static float vertexV1y = 0;
	private static float vertexV2y = 0;
	private static float vertexV3y = 0;
	private static float vertexV1z = 0;
	private static float vertexV2z = 0;
	private static float vertexV3z = 0;
	private static float facetNx = 0;
	private static float facetNy = 0;
	private static float facetNz = 0;
	private static float Ax = 0;
	private static float Ay = 0;
	private static float Az = 0;
	private static float Bx = 0;
	private static float By = 0;
	private static float Bz = 0;
	private static float Nx = 0;
	private static float Ny = 0;
	private static float Nz = 0;
	//*************************************************
	private static boolean normalBienCalculado;


	//Regular Expression of lines in file
	//-?\d\.\d+([Ee](-(0[1-9]|[1-9]\d)|\+\d\d))?
	private static final String float_ = "-?\\d\\.\\d+([Ee](-(0[1-9]|[1-9]\\d)|\\+\\d\\d))?";
	private static String nombrePrograma = "";
	private static final String solid = "solid ([^ ].*)";
	private static final String facet = "[ ]*facet normal (" + float_ + ") (" + float_ + ") (" + float_ +")";
	private static final String outer = "[ ]*outer loop";
	private static final String vertex = "[ ]*vertex (" + float_ + ") (" + float_ + ") (" + float_ +")";
	private static final String endOuter = "[ ]*endloop";
	private static final String endFacet = "[ ]*endfacet";
	private static final String endSolid = "endsolid ([^ ].*)";

	//Pattern´s Regulars Expressions of STL file
	private static final Pattern patronSolid = Pattern.compile(solid);
	private static final Pattern patronFacet = Pattern.compile(facet);
	private static final Pattern patronOuter = Pattern.compile(outer);
	private static final Pattern patronVertex = Pattern.compile(vertex);
	private static final Pattern patronEndOuter = Pattern.compile(endOuter);
	private static final Pattern patronEndFacet = Pattern.compile(endFacet);
	private static final Pattern patronEndSolid = Pattern.compile(endSolid);

	//Methods to validate the RE´s
	public static boolean validaSolid(String cadena){//begin solid
		Matcher m = patronSolid.matcher(cadena);
		boolean ok = m.matches();
		if (ok) nombrePrograma = m.group(1);
		return ok;
	}

	public static boolean validaFacet(String cadena){	//begin facet normal
		Matcher m = patronFacet.matcher(cadena);
		boolean ok = m.matches();
		if(ok){

			int cont = 0;
			while (cont < 3){

				if (cont == 0){
					stringFloat1 = m.group(1);
					facetNx = Float.parseFloat(stringFloat1);

					if(facetNx < INFINITO_NEGATIVO || facetNx > INFINITO_POSITIVO)
						throw new IllegalStateException(ERROR_FUERA_DE_RANGO);
				}
				if (cont == 1){
					stringFloat2 = m.group(5);
					facetNy = Float.parseFloat(stringFloat2);

					if(facetNy < INFINITO_NEGATIVO || facetNy > INFINITO_POSITIVO)
						throw new IllegalStateException(ERROR_FUERA_DE_RANGO);
				}
				if (cont == 2){
					stringFloat3 = m.group(9);
					facetNz = Float.parseFloat(stringFloat3);

					if(facetNz < INFINITO_NEGATIVO || facetNz > INFINITO_POSITIVO)
						throw new IllegalStateException(ERROR_FUERA_DE_RANGO);
				}
				cont++;
			}
		}
		if (ok){
			double nx = Math.pow(facetNx, 2);
			double ny = Math.pow(facetNy, 2);
			double nz = Math.pow(facetNz, 2);
			double n = Math.sqrt(nx + ny + nz);

			if(Math.abs(n - 1) > ERROR_ADMITIDO_NORMAL){
				System.err.println(ERROR_LA_NORMAL_NO_ES_UN_VECTOR + contador);
				normalBienCalculado = false;
			}else
				normalBienCalculado = true;
		}
		return ok;
	}

	public static boolean validaOuter(String cadena){	//begin outer loop
		Matcher m = patronOuter.matcher(cadena);
		boolean ok = m.matches();
		return ok;
	}

	public static boolean validaVertex(String cadena){	//vertex [must be x3]
		Matcher m = patronVertex.matcher(cadena);
		boolean ok = m.matches();
		boolean cuadrantePositivo = true;
		if(ok){
			if(contVertex == 0){
				int cont = 0;
				while (cuadrantePositivo && cont < 3){

					if (cont == 0){
						stringFloat1 = m.group(1);
						vertexV1x = Float.parseFloat(stringFloat1);
						if(vertexV1x < 0) cuadrantePositivo = false;
					}
					if (cont == 1){
						stringFloat2 = m.group(5);
						vertexV1y = Float.parseFloat(stringFloat2);
						if(vertexV1y < 0) cuadrantePositivo = false;
					}
					if (cont == 2){
						stringFloat3 = m.group(9);
						vertexV1z = Float.parseFloat(stringFloat3);
						if(vertexV1z < 0) cuadrantePositivo = false;
					}
					cont++;
				}
			}else if(contVertex == 1){
				int cont = 0;
				while (cuadrantePositivo && cont < 3){

					if (cont == 0){
						stringFloat1 = m.group(1);
						vertexV2x = Float.parseFloat(stringFloat1);
						if(vertexV2x < 0) cuadrantePositivo = false;
					}
					if (cont == 1){
						stringFloat2 = m.group(5);
						vertexV2y = Float.parseFloat(stringFloat2);
						if(vertexV2y < 0) cuadrantePositivo = false;
					}
					if (cont == 2){
						stringFloat3 = m.group(9);
						vertexV2z = Float.parseFloat(stringFloat3);
						if(vertexV2z < 0) cuadrantePositivo = false;
					}
					cont++;
				}
			}else if(contVertex == 2){
				int cont = 0;
				while (cuadrantePositivo && cont < 3){

					if (cont == 0){
						stringFloat1 = m.group(1);
						vertexV3x = Float.parseFloat(stringFloat1);
						if(vertexV3x < 0) cuadrantePositivo = false;
					}
					if (cont == 1){
						stringFloat2 = m.group(5);
						vertexV3y = Float.parseFloat(stringFloat2);
						if(vertexV3y < 0) cuadrantePositivo = false;
					}
					if (cont == 2){
						stringFloat3 = m.group(9);
						vertexV3z = Float.parseFloat(stringFloat3);
						if(vertexV3z < 0) cuadrantePositivo = false;
					}
					cont++;
				}
			}
			if (!cuadrantePositivo) System.err.println(TIPO_AVISO_1 + " Línea " + contador);
		}
		return ok;
	}

	public static boolean validaEndOuter(String cadena){//end outer loop
		Matcher m = patronEndOuter.matcher(cadena);
		boolean ok = m.matches();
		return ok;
	}

	public static boolean validaEndFacet(String cadena){//end facet normal
		Matcher m = patronEndFacet.matcher(cadena);
		boolean ok = m.matches();
		return ok;

	}

	public static boolean validaEndSolid(String cadena){//end solid
		Matcher m = patronEndSolid.matcher(cadena);
		boolean ok = m.matches();
		if(ok) ok = nombrePrograma.equals(m.group(1));
		if(ok == false) System.err.println(ERROR_FORMATO);
		return ok;
	}

	private static void calcularNormal() {
		Ax = vertexV2x - vertexV1x;
		Ay = vertexV2y - vertexV1y;
		Az = vertexV2z - vertexV1z;
		Bx = vertexV3x - vertexV1x;
		By = vertexV3y - vertexV1y;
		Bz = vertexV3z - vertexV1z;
		//**************************
		Nx = (Ay*Bz)-(Az*By);
		Ny = (Az*Bx)-(Ax*Bz);
		Nz = (Ax*By)-(Ay*Bx);
		//**************************
		double nx = Math.pow(Nx, 2);
		double ny = Math.pow(Ny, 2);
		double nz = Math.pow(Nz, 2);
		double n = Math.sqrt(nx + ny + nz);

		Nx /= n;
		Ny /= n;
		Nz /= n;

		double angulo = (facetNx*Nx) + (facetNy*Ny) + (facetNz*Nz);
		/*System.out.println(facetNx + " " + Nx + " " + facetNy + " " + Ny + " " + facetNz + " " + Nz );*/
		if (Math.acos(angulo) > MAX_VALOR_VECTORES_IGUALES_JAVA){
			Facet f1 = new Facet();

			f1.setVertex1(vertexV1x, vertexV1y, vertexV1z);
			f1.setVertex2(vertexV2x, vertexV2y, vertexV2z);
			f1.setVertex3(vertexV3x, vertexV3y, vertexV3z);

			f1.setNormal(Nx, Ny, Nz);
			listFacets.add(f1);
			System.err.println(ERROR_VECTOR_MAL_CALCULADO + (contador - 5));
		}
		else
		{
			Facet f1 = new Facet();

			f1.setVertex1(vertexV1x, vertexV1y, vertexV1z);
			f1.setVertex2(vertexV2x, vertexV2y, vertexV2z);
			f1.setVertex3(vertexV3x, vertexV3y, vertexV3z);

			f1.setNormal(facetNx, facetNy, facetNz);
			listFacets.add(f1);
		}
	}

	//private static File archivo;
	private static FileReader fr;
	private static BufferedReader br;

	//Scanner fichero = new Scanner("E:\\workspaces\\JAVA\\ProyectoAutomatas\\src\\ficherosSTL\\");

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		//Verificar el formato STL en un solo recorrido del fichero.
		/*
		 * Apertura del fichero y creación de BufferedReader
		 * para poder hacer una lectura más cómoda (disponer de readLine())
		 */
		Scanner entrada = new Scanner(System.in);

		System.out.println("Dime el nombre del fichero STL --> ");
		String nombreFichero = entrada.nextLine();
		boolean error;


		do{
			error = false;
			try{
				//archivo = new File(NOMBRE_ARCHIVO); //Create new file
				fr = new FileReader(nombreFichero);
				br = new BufferedReader(fr);
				//reading file
				String linea;
				while((linea = br.readLine()) != null){
					if(validaSolid(linea)){
						if(contSolid == 1) System.err.println(ERROR_1_SOLID);
						if(contador > 1 && contSolid==0) System.err.println(ERROR_2_SOLID);
						else{
							/*System.out.println(linea);*/
							contSolid++;
						}
					}else if(validaFacet(linea)){
						if(contFacet == 1) System.err.println(ERROR_1_FACET);
						else{
							/*System.out.println(linea);*/
							contFacet++;
						}
					}else if(validaOuter(linea)){
						if(contOuter == 1) System.err.println(ERROR_1_OUTER);
						if(contFacet == 0) System.err.println(ERROR_2_FACET);
						else{
							/*System.out.println(linea);*/
							contOuter++;
						}
					}else if(validaVertex(linea)){
						/*System.out.println(linea);*/
						if(contOuter == 0) System.err.println(ERROR_FORMATO_OUTER);
						if(contFacet == 0) System.err.println(ERROR_FORMATO_FACET);
						contVertex++;
					}else if(validaEndOuter(linea)){
						if(contOuter == 0) System.err.println(ERROR_FORMATO_OUTER);
						if(contFacet == 0) System.err.println(ERROR_FORMATO_FACET);
						if (contVertex != 3) System.err.println(ERROR_FORMATO_TRIANGULO);
						/*System.out.println(linea);*/
						if (normalBienCalculado) calcularNormal();
						contOuter = 0;
						contVertex = 0;
					}else if(validaEndFacet(linea)){
						if(contFacet != 1) System.err.println(ERROR_FORMATO_FACET);
						if(contOuter != 0) System.err.println(ERROR_FORMATO_END_OUTER);
						/*System.out.println(linea);*/
						contFacet = 0;
					}else if(validaEndSolid(linea)){
						if(contSolid != 1) System.err.println(ERROR_FORMATO_SOLID);
						/*System.out.println(linea);*/
						contSolid = 0;
					}
					contador++;
				}
				System.out.println(SUCCESSFUL_FILE_ANALIZING);
			}catch(IOException e){
				error = true;
				System.out.println("Introduzca de nuevo el fichero de manera correcta: ");
				entrada = new Scanner(System.in);
				nombreFichero = entrada.nextLine();
			}
		}while(error);

		try{
			if(fr != null)
				fr.close();
		}catch(IOException ee){
			System.err.println("Error al cerrar el fichero: " + ee.getMessage() + " :: " + ee.getCause());
		}
		J3DViewer j3d = new J3DViewer(listFacets);
		listFacets.setUseFileNormals(false);
		j3d.display(listFacets, ANCHO, ALTO);
	}
}

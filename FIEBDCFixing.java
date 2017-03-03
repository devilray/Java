import java.io.*; 
import java.util.Scanner;
import java.util.regex.*;
import java.text.SimpleDateFormat;
import java.util.Date;


//* Trabajo realizado por:

//*	Domingo Poveda López
//* Juan José Marín Peralta

public class FIEBDCFixing {
	/* Variables globales */
	
	private static int registros = 0;
	private static String tipoRegistro = "";
	private static boolean formatoCorrecto = true;
	private static StringBuffer cambiosBuffer;
	
	//*******************************************************************************************
	/* Cadenas que contienen la informacion de los registros */
	/* Registros V */
	
	private static String propiedadArchivoV = "";
	private static String versionFormatoV = "";
	private static String fechaV = "";
	private static String programaEmisionV = "";
	private static String cabeceraV = "";
	private static String rotuloV = "";
	private static String juegoCaracteresV = "";
	private static String comentarioV = "";
	private static String tipoInformacionV = "";
	private static String numeroCertificacionV = "";
	private static String fechaCertificacionV = "";
	private static String urlBaseV = "";
	
	/* Registros C */
	
	private static String codigoC = "";
	private static String codigoSiguienteC = "";
	private static String unidadesC = "";
	private static String resumenC = "";
	private static String preciosC = "";
	private static String ultimoPrecioC = "";
	private static String fechaC = "";
	private static String tipoC = "";
	
	/* Registros T */
	
	
	private static String codigoT = "";
	private static String textoT = "";
	
	
	//*******************************************************************************************
	/* Grupos de todos los registros */
	/* Grupos de registroV */
	
	private static int PROP_ARCH = 2;
	private static int VER_FORM = 4;
	private static int FECHA_V = 5;
	private static int PROGRAM_EMIS = 8;
	private static int CABE = 11;
	private static int ROT = 12;
	private static int JUEGO_CARAC = 15;
	private static int COM = 17;
	private static int TIP_INFOR = 19;
	private static int NUM_CER = 21;
	private static int FECHA_CER = 23;
	private static int URL_BAS = 25;
	
	/* Grupos de registroC */
	
	private static int COD = 3;
	private static int CODSIG = 4;
	private static int UNID = 7;
	private static int RESUM = 19;
	private static int PRECI = 21;
	private static int ULT_PRECIO = 22;
	private static int FECHA_C = 27;
	private static int TIP = 30;
	
	/* Grupos de registroT */
	
	
	private static int CODT = 2;
	private static int TEXTT = 4;
	
	
	//******************************************************************************************
	
	/* Comprobacion de que la linea siguiente es un registro nuevo */
	
	private static final String registro = "[ ]*~(V|C|T).*";
	private static final String registroDiferente = "[ ]*~([^V|C|T]).*";
	private static final String malregistro = "[ ]*~[ ]*[^\\p{Lu}].*";
	
	//******************************************************************************************
	
	/* Como sustituir valores de una expresion regular, en el main esta el resto del codigo */
	/*
	private static final String c = "(hola|hula)";
	private static final Pattern prueba = Pattern.compile(c);
	private static final Matcher m = prueba.matcher("hola que tal estas hula que pasa hola hula holaaaaaahulaholahula adios jeje");
	private static final String sus = "\"sus\"";
	private static final StringBuffer sb = new StringBuffer("");
	*/
	//******************************************************************************************
	/* EXPRESIONES REGULARES */
	
	/* Expresiones regulares registro ~V*/
	
	private static final String FECHA = "\\d\\d?\\d?\\d?\\d?\\d?\\d?\\d?";
	private static final String ROTULO = "[^|\\\\~]*";
	private static final String PROPIEDAD_ARCHIVO = "[^|\\\\~]*";
	private static final String VERSION_FORMATO = "([^|\\\\~]*)(\\\\("+ FECHA + "))?";
	private static final String PROGRAMA_EMISION = "[^\\s|\\\\~]*";
	private static final String CABECERA = "([^|\\\\~]*)((\\\\" + ROTULO + ")*)";
	private static final String JUEGO_CARACTERES = "(850|437|ANSI)"; 		
	private static final String COMENTARIO = "[^\\d{1,8}][^|\\\\~]*";
	private static final String TIPO_INFORMACION = "(1|2|3|4)"; 			
	private static final String NUMERO_CERTIFICACION = "\\d\\d?";
	private static final String FECHA_CERTIFICACION = FECHA;
	private static final String URL_BASE = "(?i)https?://([\\w-]+\\.)+[a-z]{2,6}(:\\d{1,4})?(/[\\w\\/#~:.?+=&%@~-]+)?"; //Asi se ponen las url segun tutorial-metodosJava-sintaxER.pdf
	private static final String registroV = "[ ]*~V[ ]*\\|[ ]*((" + PROPIEDAD_ARCHIVO + ")[ ]*\\|[ ]*)?(" + VERSION_FORMATO + ")[ ]*\\|[ ]*(("
						+ PROGRAMA_EMISION + ")[ ]*\\|[ ]*)?((" + CABECERA + ")[ ]*\\|[ ]*)?([ ]*" + JUEGO_CARACTERES + "[ ]*\\|[ ]*)?(("
						+ COMENTARIO + ")[ ]*\\|[ ]*)?([ ]*" + TIPO_INFORMACION + "[ ]*\\|[ ]*)?([ ]*(" + NUMERO_CERTIFICACION + ")[ ]*\\|[ ]*)?([ ]*("
						+ FECHA_CERTIFICACION + ")[ ]*\\|[ ]*)?((" + URL_BASE + ")[ ]*\\|[ ]*)?";
	
	/* Expresiones regulares registro ~C */
	private static final String CODIGO = "([A-Za-z\\dñÑ._&#$%]{1,20})((\\\\[A-Za-z\\dñÑ._&#$%]{1,20})*)";
	private static final String UNIDAD = "([Mm](|²|³|2|3)|[Kk](g(|r)|m)|t|l|h(|a)|[Dd](|m(²|³|2|3))|a|[Cc]m(²|³|2|3)|[Uu](|d)|(m|c)u)?";
	private static final String RESUMEN = "[^|\\\\~]*";
	private static final String PRECIO = "([ ]*(\\d+(,|.)\\d\\d)[ ]*(\\\\)?)*";
	private static final String TIPO = "(0|1|2|3|4|5)";
	
	private static final String registroC = "[ ]*~C[ ]*\\|[ ]*((" + CODIGO + ")[ ]*\\|[ ]*)((" + UNIDAD + ")[ ]*\\|[ ]*)?((" + RESUMEN + ")[ ]*\\|[ ]*)?((" 
						+ PRECIO + ")[ ]*\\|[ ]*)?((" + FECHA + ")?[ ]*\\|[ ]*)?((" + TIPO + ")[ ]*\\|[ ]*)?";
	
	/* Expresiones regulares registro ~T */
	
	private static final String CODIGO_CONCEPTO = "[A-Za-z\\dñÑ._&#$%]{1,20}";
	private static final String TEXTO_DESCRIPTIVO = "[^|\\\\~]*";
	private static final String registroT = "[ ]*~T[ ]*\\|[ ]*((" + CODIGO_CONCEPTO + ")[ ]*\\|[ ]*)((" + TEXTO_DESCRIPTIVO + ")[ ]*\\|[ ]*)";
	
	/* Expresion regular para quitar los espacios en blancos */
	
	private static final String cadenaConBlancos = "[ ]*~(V|C|T)[ ]*";
	
	
	//********************************************************************************************
	
	/* Patters de todas las expresiones regulares */
	
	private static final Pattern REGISTROV = Pattern.compile(registroV);
	private static final Pattern REGISTROC = Pattern.compile(registroC);
	private static final Pattern REGISTROT = Pattern.compile(registroT);
	private static final Pattern REGISTRO = Pattern.compile(registro);
	private static final Pattern MALREGISTRO = Pattern.compile(malregistro);
	private static final Pattern REGISTRODIFERENTE = Pattern.compile(registroDiferente);
	private static final Pattern BLANCOS = Pattern.compile(cadenaConBlancos);
	
	
	/* Metodos de validacion y relacionados con Matcher */
	
	private static boolean matchRegistroV(String cadena)
	{
		Matcher m = REGISTROV.matcher(cadena);
		boolean ok = m.matches();
		if (ok)
		{
			propiedadArchivoV = m.group(PROP_ARCH);
			versionFormatoV = m.group(VER_FORM);
			fechaV = m.group(FECHA_V);
			programaEmisionV = m.group(PROGRAM_EMIS);
			cabeceraV = m.group(CABE);
			rotuloV = m.group(ROT);
			juegoCaracteresV = m.group(JUEGO_CARAC);
			comentarioV = m.group(COM);
			tipoInformacionV = m.group(TIP_INFOR);
			numeroCertificacionV = m.group(NUM_CER);
			fechaCertificacionV = m.group(FECHA_CER);
			urlBaseV = m.group(URL_BAS);
		}
		return ok;
	}
	
	private static boolean matchRegistroC(String cadena)
	{
		Matcher m = REGISTROC.matcher(cadena);
		boolean ok = m.matches();
		if(ok)
		{
			codigoC = m.group(COD);
			codigoSiguienteC = m.group(CODSIG);
			unidadesC = m.group(UNID);
			resumenC = m.group(RESUM);
			preciosC = m.group(PRECI);
			ultimoPrecioC = m.group(ULT_PRECIO);
			fechaC = m.group(FECHA_C);
			tipoC = m.group(TIP);
		}
		return ok;
	}
	
	private static boolean matchRegistroT(String cadena)
	{
		Matcher m = REGISTROT.matcher(cadena);
		boolean ok = m.matches();
		if(ok)
		{
			codigoT = m.group(CODT);
			textoT = m.group(TEXTT);
		}
		return ok;
	}

	private static boolean isBadRegistro(String cadena)
	{
		Matcher m = MALREGISTRO.matcher(cadena);
		boolean ok = m.matches();
		if (ok) mensajesError(6);
		return ok;
	}
	
	private static boolean isRegistro(String cadena)
	{
		Matcher m = REGISTRO.matcher(cadena);
		boolean ok = m.matches();
		if (ok) {
			if(cambiosBuffer != null && cambiosBuffer.toString().length() != 0){
				if(tipoRegistro == "T" || tipoRegistro == "V" || tipoRegistro == "C")
					mensajesError(4);
			}
			tipoRegistro = m.group(1);
		}
		return ok;
	}
	
	private static boolean isRegistroDiferente(String cadena)
	{
		Matcher m = REGISTRODIFERENTE.matcher(cadena);
		boolean ok = m.matches();
		if (ok){
			if(cambiosBuffer != null && cambiosBuffer.toString().length() != 0){
				if(tipoRegistro == "T" || tipoRegistro == "V" || tipoRegistro == "C")
					mensajesError(4);
			}
			tipoRegistro = m.group(1);
		}
		return ok;
	}
	//*********************************************************************************************
	
	/*Obtencion de la fecha actual del sistema*/
	
	private static SimpleDateFormat patron = new SimpleDateFormat("ddMMyyyy");
	private static Date hoy = new Date();
	private static String cadena_hoy = patron.format(hoy);
	//********************************************************************************************
	
	/*Caracteres de cuadrado y cubo: ² ³  */
	//********************************************************************************************
	
	/* Mensajes de error */
	
	private static void mensajesError(int n)
	{
		switch (n) {
		case 0:
			System.err.println("Error de formato: Registro " + registros + "no cumple el formato FIE-BDC3.");
			break;

		case 1:
			System.err.println("Error de formato: No hay registro tipo V al principio del fichero.");
			break;
			
		case 2:
			System.err.println("El registro " + (registros+1) + "es de la forma ~V y ya hay uno antes. No se incluye en la salida." );
			break;
		case 3:
			System.err.println("La fecha de certificación del registro V tiene mas de 8 números(formato de fecha = ddmmAAAA).");
			break;
		case 4:
			System.err.println("Registro de nº " + registros +" de tipo " + tipoRegistro + " con contenido incorrecto");
			break;
		case 6:
			System.err.println("Registro nº " + registros + "de tipo inexistente.");
		}
	}
	
	private static void mensajes(int n)
	{
		switch (n) {
		case 0:
			System.out.println("Registro nº " + (registros+1) + " de tipo existente. Incluyendo en salida.");
			break;
		case 1:
			System.out.println("Registro nº " + registros + " de tipo existente. Incluyendo en salida.");
			break;
		case 2:
			System.out.println("Registro nº " + registros + " de tipo " + tipoRegistro + ". No se incluye en la salida");
			break;
		case 3:
			System.out.println("Registro nº " + registros + " ha sido reparado");
			break;
		case 4:
			System.out.println("Registro nº " + (registros+1) + " ha sido reparado");
			break;	
		}
	}
	
	//********************************************************************************************
	
	public static void main(String[] args) 
	{
		System.out.println("Introduzca el nombre del fichero FIE-BDC3: ");
		Scanner consola = new Scanner(System.in);
		String lectura = consola.nextLine();
		String ruta = "./"+lectura+".bc3";
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		String cadena = "";
		StringBuffer contenido = new StringBuffer(cadena);
		StringBuffer registroParcial = new StringBuffer(cadena);
		String registroParcialTexto = "";
		boolean error;
			do{
				error=false;
			      try{
			    	  
			         archivo = new File (ruta);
			         fr = new FileReader (archivo);
			         br = new BufferedReader(fr);
	
			         String linea;
			         while((linea=br.readLine())!=null)
				         {
								if(isRegistro(linea)){							
									if(registros==0 && !tipoRegistro.equals("V")){
										formatoCorrecto = false;				
										mensajesError(1);				
										break;
									}											
									else if (registros == 1 && !tipoRegistro.equals("V")){
										if(registroParcial.toString().length()!=0){
											formatoCorrecto = false;
											mensajesError(1);
											break;
										}
									}
									else {
										registroParcial = new StringBuffer("");
										if(tipoRegistro.equals("V")){																								//* Encontrada la cadena registro V
											if(matchRegistroV(linea)){
												if(registros==0){
													cambiosBuffer = new StringBuffer("");
													Matcher espacios = BLANCOS.matcher(linea);//*eliminacion espacios en blanco iniciales
													while(espacios.find())
													{
														espacios.appendReplacement(cambiosBuffer, "~V");
													}
													espacios.appendTail(cambiosBuffer);
													linea = cambiosBuffer.toString();
													cambiosBuffer = new StringBuffer("");
													if (fechaV != null){														
														Pattern fechaPatternV = Pattern.compile(fechaV);
														Matcher fechaMatcherV = fechaPatternV.matcher(linea);
														while(fechaMatcherV.find()){
															fechaMatcherV.appendReplacement(cambiosBuffer, cadena_hoy);
														}
														fechaMatcherV.appendTail(cambiosBuffer);
														linea = cambiosBuffer.toString();
														fechaV = cadena_hoy;
													}
													else{
														Pattern versionPatternV = Pattern.compile(versionFormatoV);
														Matcher versionMatcherV = versionPatternV.matcher(linea);
														while(versionMatcherV.find()){
															versionMatcherV.appendReplacement(cambiosBuffer, versionFormatoV+"\\\\" + cadena_hoy);
														}
														versionMatcherV.appendTail(cambiosBuffer);
														linea = cambiosBuffer.toString();
														fechaV = cadena_hoy;
													}
													cambiosBuffer = new StringBuffer("");
													if (programaEmisionV != null){
														Pattern programaEmisionPattern = Pattern.compile(programaEmisionV);
														Matcher programaEmisionMatcher = programaEmisionPattern.matcher(linea);
														while(programaEmisionMatcher.find()){
															programaEmisionMatcher.appendReplacement(cambiosBuffer, "FiebdcFixing");
														}
														programaEmisionMatcher.appendTail(cambiosBuffer);
														linea = cambiosBuffer.toString();
														programaEmisionV = "FiebdcFixing";
													}
													else{
														Pattern fechaPatternV = Pattern.compile(fechaV);
														Matcher fechaMatcherV = fechaPatternV.matcher(linea);
														while(fechaMatcherV.find()){
															fechaMatcherV.appendReplacement(cambiosBuffer, fechaV + "\\|FiebdcFixing" );
														}
														fechaMatcherV.appendTail(cambiosBuffer);
														programaEmisionV = "FiebdcFixing";
														linea = cambiosBuffer.toString();	
													}
													cambiosBuffer = new StringBuffer("");
													if (cabeceraV == null){
														if(juegoCaracteresV!=null){
															Pattern juegoPatternV = Pattern.compile(juegoCaracteresV);
															Matcher juegoMatcherV = juegoPatternV.matcher(linea);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, "ANSI");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															linea = cambiosBuffer.toString();
														}
														else{
															Pattern juegoPatternV = Pattern.compile(programaEmisionV);
															Matcher juegoMatcherV = juegoPatternV.matcher(linea);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, programaEmisionV + "\\|ANSI");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															linea = cambiosBuffer.toString();
														}
													}
													else if (cabeceraV != null && rotuloV == null){
														if(juegoCaracteresV!=null){
															Pattern juegoPatternV = Pattern.compile(juegoCaracteresV);
															Matcher juegoMatcherV = juegoPatternV.matcher(linea);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, "ANSI");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															linea = cambiosBuffer.toString();
														}
														else {
															Pattern juegoPatternV = Pattern.compile("\\|[ ]*"+ cabeceraV + "[ ]*\\|");
															Matcher juegoMatcherV = juegoPatternV.matcher(linea);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, "\\|" + cabeceraV + "\\|ANSI");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															linea = cambiosBuffer.toString();
														}
													}
													else if(cabeceraV != null && rotuloV !=null){
														if(juegoCaracteresV!=null){
															Pattern juegoPatternV = Pattern.compile(juegoCaracteresV);
															Matcher juegoMatcherV = juegoPatternV.matcher(linea);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, "ANSI");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															linea = cambiosBuffer.toString();
														}
														else{
															Pattern juegoPatternV = Pattern.compile(rotuloV + "[ ]*\\|");
															Matcher juegoMatcherV = juegoPatternV.matcher(linea);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, rotuloV + "\\|ANSI\\|");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															linea = cambiosBuffer.toString();
														}
													}
													cambiosBuffer = new StringBuffer("");
													if(fechaCertificacionV != null){//*comprobacion fecha
														String cambioFechaCert = fechaCertificacionV;
														Pattern fechaCertificacionPatternV = Pattern.compile("\\d");
														Matcher fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
														int conta = 0;
														while(fechaCertificacionMatcherV.find()){
															conta++;
															}
														if(conta < 8){
															mensajes(4);
															if (conta%2 == 1){
																cambioFechaCert = "0"+cambioFechaCert;
																conta++;
															}
															if(conta==6){
																int cont6 = 2;
																fechaCertificacionPatternV = Pattern.compile("\\d\\d\\d\\d(\\d\\d)");
																fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																String año;
																if (fechaCertificacionMatcherV.matches()){
																	año = fechaCertificacionMatcherV.group(1);
																	Float añofloat = Float.parseFloat(año);
																	if(añofloat >=80){
																		fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																		fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																		while(fechaCertificacionMatcherV.find()){
																			if(cont6 == 0)
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "19"+año);
																			cont6--;
																		}
																		cambioFechaCert = cambiosBuffer.toString();
																	}
																	else{
																		fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																		fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																		while(fechaCertificacionMatcherV.find()){
																			if(cont6 == 0)
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "20"+año);
																			cont6--;
																		}
																		cambioFechaCert = cambiosBuffer.toString();
																	}
																}
															}
															else if (conta==4){
																int cont6 = 1;
																fechaCertificacionPatternV = Pattern.compile("\\d\\d(\\d\\d)");
																fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																String año;
																if(fechaCertificacionMatcherV.matches()){
																	año =fechaCertificacionMatcherV.group(1);
																	Float añofloat = Float.parseFloat(año);
																	if(añofloat >=80){
																		fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																		fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																		while(fechaCertificacionMatcherV.find()){
																			if(cont6 == 0)
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "19"+año);
																			cont6--;
																		}
																		cambioFechaCert = cambiosBuffer.toString();
																	}
																	else{
																		fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																		fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																		while(fechaCertificacionMatcherV.find()){
																			if(cont6 == 0)
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "20"+año);
																			cont6--;
																		}
																}		
																	cambioFechaCert = cambiosBuffer.toString();
																}
																cambioFechaCert = "00" + cambioFechaCert;
															}
															else{
																fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																String año;
																if(fechaCertificacionMatcherV.matches()){
																	año= fechaCertificacionMatcherV.group(1);
																	Float añofloat = Float.parseFloat(año);
																	if(añofloat >=80){
																		while(fechaCertificacionMatcherV.find()){
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "19"+año);
																		}
																		cambioFechaCert = cambiosBuffer.toString();
																	}
																	else{
																		fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																		fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																		while(fechaCertificacionMatcherV.find()){
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "20"+año);
																		}
																		cambioFechaCert = cambiosBuffer.toString();
																	}
																}
																cambioFechaCert = "0000" + cambioFechaCert;
															}
															cambiosBuffer = new StringBuffer("");
															fechaCertificacionPatternV = Pattern.compile(fechaCertificacionV);
															fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(linea);
															while(fechaCertificacionMatcherV.find()){
																fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, cambioFechaCert);
															}
															fechaCertificacionMatcherV.appendTail(cambiosBuffer);
															linea = cambiosBuffer.toString();
														}
														else if(conta > 8)
															mensajesError(3);
													}
													contenido.append(linea + "\r\n");
													mensajes(0);
												}
												else mensajesError(2);
											}
											else{
												registroParcial.append(linea);
											}
										}
										else if(tipoRegistro.equals("C")){																					//*cadena registro C
											if(matchRegistroC(linea)){
												cambiosBuffer = new StringBuffer("");
												Matcher espacios = BLANCOS.matcher(linea);//*eliminacion espacios en blanco iniciales
												while(espacios.find())
												{
													espacios.appendReplacement(cambiosBuffer, "~C");
												}
												espacios.appendTail(cambiosBuffer);
												linea = cambiosBuffer.toString();
												if (rotuloV != null){
													Pattern barraPatter = Pattern.compile("\\\\");
													Matcher barraMatcher = barraPatter.matcher(rotuloV);
													int rotulos = 0;
													while(barraMatcher.find()){
														rotulos++;
													}
													Pattern preciosPatternC = Pattern.compile("[ ]*(\\d+(,|.)\\d\\d)[ ]*(\\\\)?");
													Matcher preciosMatcherC = preciosPatternC.matcher(preciosC);
													int precios = 0;
													while(preciosMatcherC.find()){
														precios++;
													}
													if (rotulos > precios) 
														mensajes(4);
													while(rotulos > precios){
														cambiosBuffer = new StringBuffer("");
														Pattern nuevoPatterC = Pattern.compile("\\|([ ]*(\\d+(,|.)\\d\\d)[ ]*(\\\\)?)*[ ]*\\|");
														Matcher nuevoMatcherC = nuevoPatterC.matcher(linea);
														if(nuevoMatcherC.find()){
															Pattern barrasFindCP = Pattern.compile("\\\\");
															Matcher barrasFindCM = barrasFindCP.matcher(preciosC);
															StringBuffer barra = new StringBuffer("");
															while (barrasFindCM.find()){
																barrasFindCM.appendReplacement(barra, "\\\\\\\\");
															}
															barrasFindCM.appendTail(barra);
															String preciosBarraC = barra.toString();
															nuevoMatcherC.appendReplacement(cambiosBuffer, "\\|" + preciosBarraC + "\\\\" + ultimoPrecioC + "\\|");
														}
														preciosC = preciosC + "\\" + ultimoPrecioC;
														nuevoMatcherC.appendTail(cambiosBuffer); 
														precios++;
														linea = cambiosBuffer.toString();
													}
												}
												cambiosBuffer = new StringBuffer("");
												if(fechaC != null){	//*comprobacion fecha
													String cambioFechaCert = fechaC;
													Pattern fechaCPatternC = Pattern.compile("\\d");
													Matcher fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
													int conta = 0;
													while(fechaCMatcherC.find()){
														conta++;
														}
													if(conta < 8){
														mensajes(4);
														if (conta%2 == 1){
															cambioFechaCert = "0"+cambioFechaCert;
															conta++;
														}
														if(conta==6){
															int cont6 = 2;
															fechaCPatternC = Pattern.compile("\\d\\d\\d\\d(\\d\\d)");
															fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
															String año;
															if (fechaCMatcherC.matches()){
																año = fechaCMatcherC.group(1);
																Float añofloat = Float.parseFloat(año);
																if(añofloat >=80){
																	fechaCPatternC = Pattern.compile("(\\d\\d)");
																	fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
																	while(fechaCMatcherC.find()){
																		if(cont6 == 0)
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "19"+año);
																		cont6--;
																	}
																	cambioFechaCert = cambiosBuffer.toString();
																}
																else{
																	fechaCPatternC = Pattern.compile("(\\d\\d)");
																	fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
																	while(fechaCMatcherC.find()){
																		if(cont6 == 0)
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "20"+año);
																		cont6--;
																	}
																	cambioFechaCert = cambiosBuffer.toString();
																}
															}
														}
														else if (conta==4){
															int cont6 = 1;
															fechaCPatternC = Pattern.compile("\\d\\d(\\d\\d)");
															fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
															String año;
															if(fechaCMatcherC.matches()){
																año =fechaCMatcherC.group(1);
																Float añofloat = Float.parseFloat(año);
																if(añofloat >=80){
																	fechaCPatternC = Pattern.compile("(\\d\\d)");
																	fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
																	while(fechaCMatcherC.find()){
																		if(cont6 == 0)
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "19"+año);
																		cont6--;
																	}
																	cambioFechaCert = cambiosBuffer.toString();
																}
																else{
																	fechaCPatternC = Pattern.compile("(\\d\\d)");
																	fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
																	while(fechaCMatcherC.find()){
																		if(cont6 == 0)
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "20"+año);
																		cont6--;
																	}
															}		
																cambioFechaCert = cambiosBuffer.toString();
															}
															cambioFechaCert = "00" + cambioFechaCert;
														}
														else{
															fechaCPatternC = Pattern.compile("(\\d\\d)");
															fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
															String año;
															if(fechaCMatcherC.matches()){
																año= fechaCMatcherC.group(1);
																Float añofloat = Float.parseFloat(año);
																if(añofloat >=80){
																	while(fechaCMatcherC.find()){
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "19"+año);
																	}
																	cambioFechaCert = cambiosBuffer.toString();
																}
																else{
																	fechaCPatternC = Pattern.compile("(\\d\\d)");
																	fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
																	while(fechaCMatcherC.find()){
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "20"+año);
																	}
																	cambioFechaCert = cambiosBuffer.toString();
																}
															}
															cambioFechaCert = "0000" + cambioFechaCert;
														}
														cambiosBuffer = new StringBuffer("");
														fechaCPatternC = Pattern.compile("\\|[ ]*" + fechaC + "[ ]*\\|");
														fechaCMatcherC = fechaCPatternC.matcher(linea);
														while(fechaCMatcherC.find()){
															fechaCMatcherC.appendReplacement(cambiosBuffer, "\\|" + cambioFechaCert + "\\|");
														}
														fechaCMatcherC.appendTail(cambiosBuffer);
														linea = cambiosBuffer.toString();
													}
													else if(conta > 8)
														mensajesError(3);
												}
												cambiosBuffer = new StringBuffer("");
												Pattern resumenExtenso = Pattern.compile("[^|\\\\~]");
												Matcher resumentExtensoM = resumenExtenso.matcher(resumenC);
												int cont = 0;
												while(resumentExtensoM.find()){
													cont++;
												}
												if(cont>64){
													mensajes(3);
													resumenExtenso = Pattern.compile(resumenC);
													resumentExtensoM = resumenExtenso.matcher(linea);
													if(resumentExtensoM.find()){
														resumentExtensoM.appendReplacement(cambiosBuffer, "Resumen reubicado en registro TEXTO");
													}
													resumentExtensoM.appendTail(cambiosBuffer);
													linea = cambiosBuffer.toString() + "\r\n~T|"+ codigoC + "|" + resumenC + "|";
												}
												cambiosBuffer = new StringBuffer("");
												if (unidadesC == "m2"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(linea);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|m²\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													linea = cambiosBuffer.toString();
												}
												else if (unidadesC == "m3"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(linea);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|m³\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													linea = cambiosBuffer.toString();
												}
												else if (unidadesC == "dm2"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(linea);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|dm²\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													linea = cambiosBuffer.toString();
												}
												else if (unidadesC == "dm3"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(linea);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|dm³\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													linea = cambiosBuffer.toString();
												}
												else if (unidadesC == "cm2"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(linea);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|cm²\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													linea = cambiosBuffer.toString();
												}
												else if (unidadesC == "cm3"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(linea);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|cm³\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													linea = cambiosBuffer.toString();
												}
												else if (unidadesC == "kgr"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(linea);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|kg\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													linea = cambiosBuffer.toString();
												}
												else if (unidadesC == "ud" || unidadesC == "Ud"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(linea);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|u\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													linea = cambiosBuffer.toString();
												}
												contenido.append(linea + "\r\n");
												mensajes(0);
											}
											else{
												registroParcial.append(linea + "\r\n");
											}
										}
										else if (tipoRegistro.equals("T")){																														//*registros tipo T
											if(matchRegistroT(linea)){
												cambiosBuffer = new StringBuffer("");//*eliminacion espacios en blanco iniciales
												Matcher espacios = BLANCOS.matcher(linea);
												while(espacios.find())
												{
													espacios.appendReplacement(cambiosBuffer, "~T");
												}
												espacios.appendTail(cambiosBuffer);
												linea = cambiosBuffer.toString();
												contenido.append(linea + "\r\n");
												mensajes(0);
											}
											else {
												registroParcial.append(linea + "\n");
											}
										}
										registros++;
									}
								
								}											
								else if(isBadRegistro(linea)){
									formatoCorrecto=false;
									mensajesError(0);
									break;
								}
								else if(isRegistroDiferente(linea)) 
								{
									registros++;
									if(registros==1 && !tipoRegistro.equals("V")){	
										formatoCorrecto = false;				
										mensajesError(1);						
										break;
									}											
									else if (registros == 2 && !tipoRegistro.equals("V")){
										if(registroParcial.toString().length()!=0){
											formatoCorrecto = false;
											mensajesError(1);
											break;
										}
									}
									mensajes(2);
								}
								else{
									if(tipoRegistro == "T" || tipoRegistro == "V" || tipoRegistro == "C"){																		//***********************************************************************
										registroParcial.append(linea);
										registroParcialTexto = registroParcial.toString();
										if(tipoRegistro.equals("V")){																		//* registro V
											if(matchRegistroV(registroParcialTexto)){
												if(registros==1){
													cambiosBuffer = new StringBuffer("");
													Matcher espacios = BLANCOS.matcher(registroParcialTexto);//*eliminacion espacios en blanco iniciales
													while(espacios.find())
													{
														espacios.appendReplacement(cambiosBuffer, "~V");
													}
													espacios.appendTail(cambiosBuffer);
													registroParcialTexto = cambiosBuffer.toString();
													cambiosBuffer = new StringBuffer("");
													if (fechaV != null){														
														Pattern fechaPatternV = Pattern.compile(fechaV);
														Matcher fechaMatcherV = fechaPatternV.matcher(registroParcialTexto);
														while(fechaMatcherV.find()){
															fechaMatcherV.appendReplacement(cambiosBuffer, cadena_hoy);
														}
														fechaMatcherV.appendTail(cambiosBuffer);
														registroParcialTexto = cambiosBuffer.toString();
														fechaV = cadena_hoy;
													}
													else{
														Pattern versionPatternV = Pattern.compile(versionFormatoV);
														Matcher versionMatcherV = versionPatternV.matcher(registroParcialTexto);
														while(versionMatcherV.find()){
															versionMatcherV.appendReplacement(cambiosBuffer, versionFormatoV+"\\\\" + cadena_hoy);
														}
														versionMatcherV.appendTail(cambiosBuffer);
														registroParcialTexto = cambiosBuffer.toString();
														fechaV = cadena_hoy;
													}
													cambiosBuffer = new StringBuffer("");
													if (programaEmisionV != null){
														Pattern programaEmisionPattern = Pattern.compile(programaEmisionV);
														Matcher programaEmisionMatcher = programaEmisionPattern.matcher(registroParcialTexto);
														while(programaEmisionMatcher.find()){
															programaEmisionMatcher.appendReplacement(cambiosBuffer, "FiebdcFixing");
														}
														programaEmisionMatcher.appendTail(cambiosBuffer);
														registroParcialTexto = cambiosBuffer.toString();
														programaEmisionV = "FiebdcFixing";
													}
													else{
														Pattern fechaPatternV = Pattern.compile(fechaV);
														Matcher fechaMatcherV = fechaPatternV.matcher(registroParcialTexto);
														while(fechaMatcherV.find()){
															fechaMatcherV.appendReplacement(cambiosBuffer, fechaV + "\\|FiebdcFixing" );
														}
														fechaMatcherV.appendTail(cambiosBuffer);
														programaEmisionV = "FiebdcFixing";
														registroParcialTexto = cambiosBuffer.toString();	
													}
													cambiosBuffer = new StringBuffer("");
													if (cabeceraV == null){
														if(juegoCaracteresV!=null){
															Pattern juegoPatternV = Pattern.compile(juegoCaracteresV);
															Matcher juegoMatcherV = juegoPatternV.matcher(registroParcialTexto);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, "ANSI");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															registroParcialTexto = cambiosBuffer.toString();
														}
														else{
															Pattern juegoPatternV = Pattern.compile(programaEmisionV);
															Matcher juegoMatcherV = juegoPatternV.matcher(registroParcialTexto);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, programaEmisionV + "\\|ANSI");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															registroParcialTexto = cambiosBuffer.toString();
														}
													}
													else if (cabeceraV != null && rotuloV == null){
														if(juegoCaracteresV!=null){
															Pattern juegoPatternV = Pattern.compile(juegoCaracteresV);
															Matcher juegoMatcherV = juegoPatternV.matcher(registroParcialTexto);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, "ANSI");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															registroParcialTexto = cambiosBuffer.toString();
														}
														else {
															Pattern juegoPatternV = Pattern.compile("\\|[ ]*"+ cabeceraV + "[ ]*\\|");
															Matcher juegoMatcherV = juegoPatternV.matcher(registroParcialTexto);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, "\\|" + cabeceraV + "\\|ANSI");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															registroParcialTexto = cambiosBuffer.toString();
														}
													}
													else if(cabeceraV != null && rotuloV !=null){
														if(juegoCaracteresV!=null){
															Pattern juegoPatternV = Pattern.compile(juegoCaracteresV);
															Matcher juegoMatcherV = juegoPatternV.matcher(registroParcialTexto);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, "\\|ANSI\\|");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															registroParcialTexto = cambiosBuffer.toString();
														}
														else{
															Pattern juegoPatternV = Pattern.compile(rotuloV + "[ ]*\\|");
															Matcher juegoMatcherV = juegoPatternV.matcher(registroParcialTexto);
															while(juegoMatcherV.find()){
																juegoMatcherV.appendReplacement(cambiosBuffer, rotuloV + "\\|ANSI\\|");
															}
															juegoMatcherV.appendTail(cambiosBuffer);
															juegoCaracteresV = "ANSI";
															registroParcialTexto = cambiosBuffer.toString();
														}
													}
													cambiosBuffer = new StringBuffer("");
													if(fechaCertificacionV != null){//*comprobacion fecha
															
														String cambioFechaCert = fechaCertificacionV;
														Pattern fechaCertificacionPatternV = Pattern.compile("\\d");
														Matcher fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
														int conta = 0;
														while(fechaCertificacionMatcherV.find()){
															conta++;
															}
														if(conta < 8){
															mensajes(3);
															if (conta%2 == 1){
																cambioFechaCert = "0"+cambioFechaCert;
																conta++;
															}
															if(conta==6){
																int cont6 = 2;
																fechaCertificacionPatternV = Pattern.compile("\\d\\d\\d\\d(\\d\\d)");
																fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																String año;
																if (fechaCertificacionMatcherV.matches()){
																	año = fechaCertificacionMatcherV.group(1);
																	Float añofloat = Float.parseFloat(año);
																	if(añofloat >=80){
																		fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																		fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																		while(fechaCertificacionMatcherV.find()){
																			if(cont6 == 0)
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "19"+año);
																			cont6--;
																		}
																		cambioFechaCert = cambiosBuffer.toString();
																	}
																	else{
																		fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																		fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																		while(fechaCertificacionMatcherV.find()){
																			if(cont6 == 0)
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "20"+año);
																			cont6--;
																		}
																		cambioFechaCert = cambiosBuffer.toString();
																	}
																}
															}
															else if (conta==4){
																int cont6 = 1;
																fechaCertificacionPatternV = Pattern.compile("\\d\\d(\\d\\d)");
																fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																String año;
																if(fechaCertificacionMatcherV.matches()){
																	año =fechaCertificacionMatcherV.group(1);
																	Float añofloat = Float.parseFloat(año);
																	if(añofloat >=80){
																		fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																		fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																		while(fechaCertificacionMatcherV.find()){
																			if(cont6 == 0)
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "19"+año);
																			cont6--;
																		}
																		cambioFechaCert = cambiosBuffer.toString();
																	}
																	else{
																		fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																		fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																		while(fechaCertificacionMatcherV.find()){
																			if(cont6 == 0)
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "20"+año);
																			cont6--;
																		}
																}		
																	cambioFechaCert = cambiosBuffer.toString();
																}
																cambioFechaCert = "00" + cambioFechaCert;
															}
															else{
																fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																String año;
																if(fechaCertificacionMatcherV.matches()){
																	año= fechaCertificacionMatcherV.group(1);
																	Float añofloat = Float.parseFloat(año);
																	if(añofloat >=80){
																		while(fechaCertificacionMatcherV.find()){
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "19"+año);
																		}
																		cambioFechaCert = cambiosBuffer.toString();
																	}
																	else{
																		fechaCertificacionPatternV = Pattern.compile("(\\d\\d)");
																		fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(cambioFechaCert);
																		while(fechaCertificacionMatcherV.find()){
																				fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, "20"+año);
																		}
																		cambioFechaCert = cambiosBuffer.toString();
																	}
																}
																cambioFechaCert = "0000" + cambioFechaCert;
															}
															cambiosBuffer = new StringBuffer("");
															fechaCertificacionPatternV = Pattern.compile(fechaCertificacionV);
															fechaCertificacionMatcherV = fechaCertificacionPatternV.matcher(registroParcialTexto);
															while(fechaCertificacionMatcherV.find()){
																fechaCertificacionMatcherV.appendReplacement(cambiosBuffer, cambioFechaCert);
															}
															fechaCertificacionMatcherV.appendTail(cambiosBuffer);
															registroParcialTexto = cambiosBuffer.toString();
														}
														else if(conta > 8)
															mensajesError(3);
													}
													contenido.append(registroParcialTexto + "\r\n");
													mensajes(1);
												}
												else mensajesError(2);
											}
										}
										else if (tipoRegistro.equals("C")) {																												//* registro C
											if(matchRegistroC(registroParcialTexto)){
												cambiosBuffer = new StringBuffer("");//*eliminacion espacios en blanco iniciales
												Matcher espacios = BLANCOS.matcher(registroParcialTexto);
												while(espacios.find())
												{
													espacios.appendReplacement(cambiosBuffer, "~C");
												}
												espacios.appendTail(cambiosBuffer);
												registroParcialTexto = cambiosBuffer.toString();
												if (rotuloV != null){
													Pattern barraPatter = Pattern.compile("\\\\");
													Matcher barraMatcher = barraPatter.matcher(rotuloV);
													int rotulos = 0;
													while(barraMatcher.find()){
														rotulos++;
													}
													Pattern preciosPatternC = Pattern.compile("[ ]*(\\d+(,|.)\\d\\d)[ ]*(\\\\)?");
													Matcher preciosMatcherC = preciosPatternC.matcher(preciosC);
													int precios = 0;
													while(preciosMatcherC.find()){
														precios++;
													}
													if (rotulos > precios) 
														mensajes(3);
													while(rotulos > precios){
														cambiosBuffer = new StringBuffer("");
														Pattern nuevoPatterC = Pattern.compile("\\|([ ]*(\\d+(,|.)\\d\\d)[ ]*(\\\\)?)*[ ]*\\|");
														Matcher nuevoMatcherC = nuevoPatterC.matcher(registroParcialTexto);
														if(nuevoMatcherC.find()){
															Pattern barrasFindCP = Pattern.compile("\\\\");
															Matcher barrasFindCM = barrasFindCP.matcher(preciosC);
															StringBuffer barra = new StringBuffer("");
															while (barrasFindCM.find()){
																barrasFindCM.appendReplacement(barra, "\\\\\\\\");
															}
															barrasFindCM.appendTail(barra);
															String preciosBarraC = barra.toString();
															nuevoMatcherC.appendReplacement(cambiosBuffer, "\\|" + preciosBarraC + "\\\\" + ultimoPrecioC + "\\|");
														}
														preciosC = preciosC + "\\" + ultimoPrecioC;
														nuevoMatcherC.appendTail(cambiosBuffer); 
														precios++;
														registroParcialTexto = cambiosBuffer.toString();
													}
												}
												cambiosBuffer = new StringBuffer("");
												if(fechaC != null){//*comprobacion fecha
														
													String cambioFechaCert = fechaC;
													Pattern fechaCPatternC = Pattern.compile("\\d");
													Matcher fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
													int conta = 0;
													while(fechaCMatcherC.find()){
														conta++;
														}
													if(conta < 8){
														mensajes(3);
														if (conta%2 == 1){
															cambioFechaCert = "0"+cambioFechaCert;
															conta++;
														}
														if(conta==6){
															int cont6 = 2;
															fechaCPatternC = Pattern.compile("\\d\\d\\d\\d(\\d\\d)");
															fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
															String año;
															if (fechaCMatcherC.matches()){
																año = fechaCMatcherC.group(1);
																Float añofloat = Float.parseFloat(año);
																if(añofloat >=80){
																	fechaCPatternC = Pattern.compile("(\\d\\d)");
																	fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
																	while(fechaCMatcherC.find()){
																		if(cont6 == 0)
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "19"+año);
																		cont6--;
																	}
																	cambioFechaCert = cambiosBuffer.toString();
																}
																else{
																	fechaCPatternC = Pattern.compile("(\\d\\d)");
																	fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
																	while(fechaCMatcherC.find()){
																		if(cont6 == 0)
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "20"+año);
																		cont6--;
																	}
																	cambioFechaCert = cambiosBuffer.toString();
																}
															}
														}
														else if (conta==4){
															int cont6 = 1;
															fechaCPatternC = Pattern.compile("\\d\\d(\\d\\d)");
															fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
															String año;
															if(fechaCMatcherC.matches()){
																año =fechaCMatcherC.group(1);
																Float añofloat = Float.parseFloat(año);
																if(añofloat >=80){
																	fechaCPatternC = Pattern.compile("(\\d\\d)");
																	fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
																	while(fechaCMatcherC.find()){
																		if(cont6 == 0)
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "19"+año);
																		cont6--;
																	}
																	cambioFechaCert = cambiosBuffer.toString();
																}
																else{
																	fechaCPatternC = Pattern.compile("(\\d\\d)");
																	fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
																	while(fechaCMatcherC.find()){
																		if(cont6 == 0)
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "20"+año);
																		cont6--;
																	}
															}		
																cambioFechaCert = cambiosBuffer.toString();
															}
															cambioFechaCert = "00" + cambioFechaCert;
														}
														else{
															fechaCPatternC = Pattern.compile("(\\d\\d)");
															fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
															String año;
															if(fechaCMatcherC.matches()){
																año= fechaCMatcherC.group(1);
																Float añofloat = Float.parseFloat(año);
																if(añofloat >=80){
																	while(fechaCMatcherC.find()){
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "19"+año);
																	}
																	cambioFechaCert = cambiosBuffer.toString();
																}
																else{
																	fechaCPatternC = Pattern.compile("(\\d\\d)");
																	fechaCMatcherC = fechaCPatternC.matcher(cambioFechaCert);
																	while(fechaCMatcherC.find()){
																			fechaCMatcherC.appendReplacement(cambiosBuffer, "20"+año);
																	}
																	cambioFechaCert = cambiosBuffer.toString();
																}
															}
															cambioFechaCert = "0000" + cambioFechaCert;
														}
														cambiosBuffer = new StringBuffer("");
														fechaCPatternC = Pattern.compile("\\|[ ]*" + fechaC + "[ ]*\\|");
														fechaCMatcherC = fechaCPatternC.matcher(registroParcialTexto);
														while(fechaCMatcherC.find()){
															fechaCMatcherC.appendReplacement(cambiosBuffer, "\\|" + cambioFechaCert + "\\|");
														}
														fechaCMatcherC.appendTail(cambiosBuffer);
														registroParcialTexto = cambiosBuffer.toString();
													}
													else if(conta > 8)
														mensajesError(3);
												}
												cambiosBuffer = new StringBuffer("");
												Pattern resumenExtenso = Pattern.compile("[^|\\\\~]");
												Matcher resumentExtensoM = resumenExtenso.matcher(resumenC);
												int cont = 0;
												while(resumentExtensoM.find()){
													cont++;
												}
												if(cont>64){
													mensajes(3);
													resumenExtenso = Pattern.compile(resumenC);
													resumentExtensoM = resumenExtenso.matcher(registroParcialTexto);
													if(resumentExtensoM.find()){
														resumentExtensoM.appendReplacement(cambiosBuffer, "Resumen reubicado en registro TEXTO");
													}
													resumentExtensoM.appendTail(cambiosBuffer);
													registroParcialTexto = cambiosBuffer.toString() + "\r\n~T|"+ codigoC + "|" + resumenC + "|";
												}
												cambiosBuffer = new StringBuffer("");
												if (unidadesC == "m2"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(registroParcialTexto);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|m²\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													registroParcialTexto = cambiosBuffer.toString();
												}
												else if (unidadesC == "m3"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(registroParcialTexto);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|m³\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													registroParcialTexto = cambiosBuffer.toString();
												}
												else if (unidadesC == "dm2"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(registroParcialTexto);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|dm²\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													registroParcialTexto = cambiosBuffer.toString();
												}
												else if (unidadesC == "dm3"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(registroParcialTexto);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|dm³\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													registroParcialTexto = cambiosBuffer.toString();
												}
												else if (unidadesC == "cm2"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(registroParcialTexto);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|cm²\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													registroParcialTexto = cambiosBuffer.toString();
												}
												else if (unidadesC == "cm3"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(registroParcialTexto);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|cm³\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													registroParcialTexto = cambiosBuffer.toString();
												}
												else if (unidadesC == "kgr"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(registroParcialTexto);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|kg\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													registroParcialTexto = cambiosBuffer.toString();
												}
												else if (unidadesC == "ud" || unidadesC == "Ud"){
													Pattern unidadPattern = Pattern.compile("\\|[ ]*" + unidadesC + "[ ]*\\|");
													Matcher unidadMatcher = unidadPattern.matcher(registroParcialTexto);
													if(unidadMatcher.find()){
														unidadMatcher.appendReplacement(cambiosBuffer, "\\|u\\|");
													}
													unidadMatcher.appendTail(cambiosBuffer);
													registroParcialTexto = cambiosBuffer.toString();
												}
												contenido.append(registroParcialTexto + "\r\n");
												mensajes(1);
											}
										}
										else if (tipoRegistro.equals("T")){																													//* Registro T
											if(matchRegistroT(registroParcialTexto)){
												cambiosBuffer = new StringBuffer("");//*eliminacion espacios en blanco iniciales
												Matcher espacios = BLANCOS.matcher(registroParcialTexto);
												while(espacios.find())
												{
													espacios.appendReplacement(cambiosBuffer, "~T");
												}
												espacios.appendTail(cambiosBuffer);
												registroParcialTexto = cambiosBuffer.toString();
												contenido.append(registroParcialTexto + "\r\n");
												mensajes(1);
											}
											
										}
									}
								}
									
								
													          
				         }
			         if(formatoCorrecto)
			         	{
					         PrintWriter escritor = new PrintWriter(new File(lectura + "Fixed.bc3"), "8859_1");
					         cadena = contenido.toString();
					         escritor.write(cadena);
					         escritor.close();
			         	}
			         }
			      catch(Exception e){
			    	  System.out.println("No se ha podido abrir el fichero: " + lectura + ".bc3\nIntroduzca de nuevo el nombre del fichero FIE-BDC3:");
			    	  consola = new Scanner(System.in);
			    	  lectura = consola.nextLine();
			    	  ruta = "./"+lectura + ".bc3";
			    	  error=true;
			      }
			}
			      while (error);
			
				try{                    
			            if( null != fr ){   
			               fr.close();     
			            }                  
			         }catch (Exception e2){ 
			            e2.printStackTrace();
			         }
			consola.close();
			
			/* Ejemplo para sustituir cadenas de expresiones regulares */
			/*
			//System.out.println(m.matches() +" y "+ m2.matches()); //prueba antigua
			int conta=0;											// aqui comienza la prueba para hacer las modificaciones
			while(m.find())
			{
				conta++;
				System.out.println("Apariciones " + conta);
				m.appendReplacement(sb, sus);						//Añade al buffer la cadena con las sustituciones oportunas
			}
			cadena = sb.toString();									
			System.out.println(cadena);
			m.appendTail(sb);										//añade el resto de la cadena que no hace matching
			cadena = sb.toString();
			System.out.println(cadena);
			*/
			//****************************************************************
	}
}

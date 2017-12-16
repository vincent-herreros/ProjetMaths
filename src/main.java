import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class main {

	public static void main(String[] args) throws Exception {
		String fichier;
		try {
			fichier =args[0];
		}
		catch(Exception e) {
			fichier = "preferences.csv";
		}
		String[][] csv = null;
		int nbrEleves;
		String[] nomEleves=null;
		//lecture du fichier texte	
		//test
		try{
			FileInputStream ips=new FileInputStream(fichier); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			ligne=br.readLine();
			nbrEleves = ligne.split(",").length - 1;
			
			csv = new String [nbrEleves][nbrEleves];
			nomEleves = new String[nbrEleves];
			for(int i = 0; i < nbrEleves;i++) {
				nomEleves[i] = ligne.split(",")[i+1];
			}
			int i = 0;
			while ((ligne=br.readLine())!=null){
				for(int j = 0; j < nbrEleves;j++) {
					csv[i][j] = ligne.split(",")[j+1];
				}
				i++;
			}
			br.close(); 
			
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		
		for(int i = 0;i <csv.length;i++){
			for(int j = 0; j <csv[i].length;j++){
				System.out.print(csv[i][j] + ", ");
			}
			System.out.println("");
		}
		
		int[][] groupes = repartir(csv, nomEleves);
		
		
		for(int i = 0; i < groupes.length;i++){
			for(int j = 0; j < groupes[i].length; j++){
				System.out.print(groupes[i][j] + ", "); 
			}
			System.out.println("");
		}
		
		try{
			File ff=new File("resultat.csv"); // définir l'arborescence
			ff.createNewFile();
			FileWriter ffw=new FileWriter(ff);
			for(int i = 0; i < groupes.length;i++){
				for(int j = 0; j < groupes[i].length; j++){
					ffw.write(groupes[i][j] + ";");  // écrire une ligne dans le fichier resultat.txt
					
				}
				ffw.write("\n"); // forcer le passage à la ligne
			}
			ffw.close(); // fermer le fichier à la fin des traitements
		} catch (Exception e) {}
		
	}
	
	
	public static int[][] repartir(String[][] notes, String[] nomEleves){
		
		int nbrEleves = notes.length;
		int[][] res = new int[nbrEleves][nbrEleves];
		String [] nomElevesSauvegarde = new String [nbrEleves];
		for(int i=0;i<nbrEleves;i++){
			nomElevesSauvegarde[i]=nomEleves[i];
		}
		
		int nbrGrp3 = 0;
		int nbrGrp2 = 0;
		int nbrGrp=18;
		//Calcul des groupes
		if(nbrEleves<36) {
			if(nbrEleves%2==1) {
				nbrGrp2=(nbrEleves/2)-1;
			}
			else{
				nbrGrp2=(nbrEleves/2);
			}
			nbrGrp3=(nbrEleves-(nbrGrp2*2))/3;
		}
		else if(nbrEleves>=36) {
			nbrGrp2=54-nbrEleves;
			nbrGrp3=nbrGrp-nbrGrp2;
		}
		
		System.out.println("Nombre d'élèves au total : "+nbrEleves);
		System.out.println("Nombre de groupe de 3 : "+nbrGrp3);
		System.out.println("Nombre de groupe de 2 : "+nbrGrp2);
		System.out.println("Appuyez sur entrée");
		Scanner sc=new Scanner(System.in);
		String entre=sc.nextLine();
		
		int indAD = 0;
		int[] alreadyDone = new int[nbrEleves];
		for(int i = 0; i < nbrEleves;i++) {
			alreadyDone[i] = -1;
		}
		
		String[] notesOrder = {"AR","I","P","AB","B","TB"};
		
		System.out.println("Ordre lexicographique des notes : ");
		System.out.println("AR < I < P < AB < B < TB");
		System.out.println("Appuyez sur entrée");
		sc=new Scanner(System.in);
		entre=sc.nextLine();
		
		int [][] notesTrie = new int[nbrEleves][nbrEleves];
		
		for(int i=0;i<nbrEleves;i++) {
			if(i==0) {
				for (int j=0;j<nbrEleves;j++) {
					notesTrie[i][j] = indexOf(notesOrder,notes[j][i]);
				}
				Arrays.sort(notesTrie[i]);
			}
			else {
				for (int j=0;j<nbrEleves;j++) {
					notesTrie[i][j]=indexOf(notesOrder,notes[j][i]);
				}
				Arrays.sort(notesTrie[i]);
				int k=i-1;
				boolean elevePlace=false;
				int eleveEnCours=i;
				String nomEleveEnCours;
				while(k>=0 && elevePlace==false) {
					if(isBetterThan(notesTrie[eleveEnCours],notesTrie[k])==0) {
						int [] echangeNotes = new int [nbrEleves];
						for(int u=0;u<nbrEleves;u++) {
							echangeNotes[u] = notesTrie[k][u];
							notesTrie[k][u]=notesTrie[eleveEnCours][u];
							notesTrie[eleveEnCours][u]=echangeNotes[u];
						}
						nomEleveEnCours=nomEleves[k];
						nomEleves[k]=nomEleves[eleveEnCours];
						nomEleves[eleveEnCours]=nomEleveEnCours;
						eleveEnCours--;
						k--;
					}
					else {
						elevePlace=true;
					}
				}
			}
		}
		for(int i=0;i<nbrEleves;i++) {
			System.out.print(nomEleves[i]);
			for(int j=0;j<nbrEleves;j++) {
				System.out.print(notesTrie[i][j]);
			}
			System.out.println();
		}
	
		
		//Pour chaque eleves pas deja fait dans la limite du nombre de groupe de 3
		int elevei = 0;
		while(indAD < nbrGrp3*3){
			int numeroEleve=0;
			while(numeroEleve<nbrEleves && !nomEleves[elevei].equals(nomElevesSauvegarde[numeroEleve])) {
				numeroEleve++;
			}
			if(!trouver(alreadyDone,numeroEleve)){
				int[] bestNoteDuo = new int [2];
				int d;
				if(numeroEleve!=0) {
					bestNoteDuo[0] = indexOf(notesOrder,notes[numeroEleve][0]);
					bestNoteDuo[1] = indexOf(notesOrder,notes[0][numeroEleve]);
					d=1;
				}
				else {
					bestNoteDuo[0] = indexOf(notesOrder,notes[numeroEleve][1]);
					bestNoteDuo[1] = indexOf(notesOrder,notes[1][numeroEleve]);
					d=2;
					}
				Arrays.sort(bestNoteDuo);
				int nbrMA = 1;
				int[] meilleursAmis = new int[nbrEleves];
				
				//On trouve ses meillleurs amis
				for(int elevej = d; elevej < notes[numeroEleve].length; elevej++){
					if(numeroEleve != elevej && !trouver(alreadyDone,elevej)){
						int[] noteDuo = {indexOf(notesOrder,notes[numeroEleve][elevej]),indexOf(notesOrder,notes[elevej][numeroEleve])};
						Arrays.sort(noteDuo);
						if(isBetterThan(noteDuo,bestNoteDuo) == 2){
							bestNoteDuo = noteDuo;
							meilleursAmis[0] = elevej;
							nbrMA = 1;
						}
						else if(isBetterThan(noteDuo,bestNoteDuo) == 1){
							meilleursAmis[nbrMA] = elevej;
							nbrMA++;
						}
					}
				}
				System.out.println("Eleve sélectionné : "+nomElevesSauvegarde[numeroEleve]);
				System.out.println("Binomes sélectionnés : ");
				int pma=0;
				while(meilleursAmis[pma]!=0) {
					System.out.println(nomElevesSauvegarde[meilleursAmis[pma]]);
					pma++;
				}
				sc=new Scanner(System.in);
				entre=sc.nextLine();
				
				
				//On trouve le dernier ami parmi les second amis
				int deuxiemeAmi = -1;
				int troisiemeAmi = -1;
				int[] bestNoteTrio = {-1,-1,-1,-1,-1,-1};
				for(int MAi = 0; MAi <nbrMA; MAi++){
					for(int elevek = 0; elevek < nbrEleves;elevek++){
						if(numeroEleve != elevek && meilleursAmis[MAi] != elevek && !trouver(alreadyDone,elevek)){
							int[] noteTrio = {indexOf(notesOrder,notes[numeroEleve][elevek]),indexOf(notesOrder,notes[elevek][numeroEleve]),
								indexOf(notesOrder,notes[numeroEleve][meilleursAmis[MAi]]),indexOf(notesOrder,notes[meilleursAmis[MAi]][numeroEleve]),
								indexOf(notesOrder,notes[elevek][meilleursAmis[MAi]]),indexOf(notesOrder,notes[meilleursAmis[MAi]][elevek])};
							Arrays.sort(noteTrio);
							
							if(isBetterThan(noteTrio,bestNoteTrio) == 2){
								deuxiemeAmi = meilleursAmis[MAi];
								troisiemeAmi = elevek;
								bestNoteTrio = noteTrio;
								System.out.println("Groupe de 3 possible :");
								System.out.println(nomElevesSauvegarde[deuxiemeAmi]);
								System.out.println(nomElevesSauvegarde[troisiemeAmi]);
								for(int pnt=0;pnt<bestNoteTrio.length;pnt++) {
									System.out.print(notesOrder[bestNoteTrio[pnt]]+" ,");
								}
								System.out.println("");
								System.out.println("");

							}
						}
					}
				}
				//Ils ne sont donc plus disponibles
				alreadyDone[indAD] = numeroEleve;
				alreadyDone[indAD+1] = deuxiemeAmi;
				alreadyDone[indAD+2] = troisiemeAmi;
				indAD += 3;
				//System.out.println("Composition finale du trinôme :");
				//System.out.println(nomEleves[elevei] +"  "+nomEleves[deuxiemeAmi]+"  "+nomEleves[troisiemeAmi]);
				//System.out.println("Appuyez sur entrée");
				sc=new Scanner(System.in);
				entre=sc.nextLine();
				
				res[numeroEleve][deuxiemeAmi] = 1;res[deuxiemeAmi][numeroEleve] = 1;
				res[numeroEleve][troisiemeAmi] = 1;res[troisiemeAmi][numeroEleve] = 1;
				res[troisiemeAmi][deuxiemeAmi] = 1;res[deuxiemeAmi][troisiemeAmi] = 1;
				for(int i = 0; i<alreadyDone.length;i++){
					System.out.print(alreadyDone[i] + ", ");
				}
				System.out.println("");
			}
			elevei++;
		}
		
		//On complete avec les groupes de deux
		while(elevei < nbrEleves){
			int numeroEleve=0;
			while(numeroEleve<nbrEleves && !nomEleves[elevei].equals(nomElevesSauvegarde[numeroEleve])) {
				numeroEleve++;
			}
			if(!trouver(alreadyDone,numeroEleve)){
				System.out.println("Eleve sélectionné : "+nomEleves[elevei]);
				System.out.println();
				int meilleurAmi = -1;
				int[] bestNote = {-1,-1};
				for(int elevej = 0; elevej < nbrEleves;elevej++){
					if(!trouver(alreadyDone,elevej) && numeroEleve != elevej){
						int[] noteDuo = {indexOf(notesOrder,notes[numeroEleve][elevej]),indexOf(notesOrder,notes[elevej][numeroEleve])};
						Arrays.sort(noteDuo);
						if(isBetterThan(noteDuo,bestNote) == 2){
							meilleurAmi = elevej;
							bestNote = noteDuo;
							System.out.println("Groupe de 2 possible :");
							System.out.println(nomEleves[elevei]);
							System.out.println(nomEleves[elevej]);
							for(int pnt=0;pnt<bestNote.length;pnt++) {
								System.out.print(notesOrder[bestNote[pnt]]+" ,");
							}
							System.out.println();
							System.out.println();
						}
					}
				}
				//Ils ne sont donc plus disponibles
				//System.out.println(indAD);
				alreadyDone[indAD] = numeroEleve;
				alreadyDone[indAD+1] = meilleurAmi;
				indAD += 2;
				
				res[numeroEleve][meilleurAmi] = 1;res[meilleurAmi][numeroEleve] = 1;
				
				for(int i = 0; i<alreadyDone.length;i++){
					System.out.print(alreadyDone[i] + ", ");
				}
				System.out.println();
				System.out.println();

			}
			elevei++;
		}
		
		int u=0;
		while (u<alreadyDone.length) {
			if(nbrGrp3!=0) {
				System.out.print(nomElevesSauvegarde[alreadyDone[u]]+" , " +nomElevesSauvegarde[alreadyDone[u+1]]+" , "+nomElevesSauvegarde[alreadyDone[u+2]]);
				System.out.println();
				u+=3;
				nbrGrp3--;
			}
			else if(nbrGrp3==0) {
				System.out.print(nomElevesSauvegarde[alreadyDone[u]]+" , " +nomElevesSauvegarde[alreadyDone[u+1]]);
				System.out.println();
				u+=2;
			}
		}
		
		for(int i = 0; i<nbrEleves;i++) {
			res[i][i] = 1;
		}
		return res;
	}
	
	public static int indexOf(String[] tab, String element){
		return Arrays.asList(tab).indexOf(element);
	}
	
	public static boolean trouver(int[] tab, int element) {
		int i=0;
		boolean t=false;
		while(i<tab.length && t!=true) {
			if(tab[i]==element) {
				t=true;
			}
			i++;
		}
		return t;
	}
	
	// renvoie 0 si inferieur, 1 si egal et 2 si superieur
	public static int isBetterThan(int[] note1,int[] note2){
		int res = 1;
		int i = 0;
		while(i < note1.length && res == 1){
			if(note1[i] > note2[i]){
				res = 2;
			}
			else if(note1[i] < note2[i]){
				res = 0;
			}
			i++;
		}
		return res;
		
	}

}

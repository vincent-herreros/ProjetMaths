import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class main {

	public static void main(String[] args) throws Exception {
		String fichier ="preferences.csv";
		String[][] csv = null;
		int nbrEleves;
		String[] nomEleves=null;
		//lecture du fichier texte	
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
		
	}
	
	
	public static int[][] repartir(String[][] notes, String[] nomEleves){
		
		int nbrEleves = notes.length;
		int[][] res = new int[nbrEleves][nbrEleves];
		
		//Calcul des groupes
		int nbrGrp3 = 0;
		if(nbrEleves%3 == 1){
			nbrGrp3 = (nbrEleves/3) - 1;
		}else if(nbrEleves%3 == 0){
			nbrGrp3 = nbrEleves/3;
		}else if(nbrEleves%3 == 2){
			nbrGrp3 = nbrEleves/3;
		}
		System.out.println("Nombre d'élèves au total : "+nbrEleves);
		System.out.println("Nombre de groupe de 3 : "+nbrGrp3);
		int nbrGrp2=(nbrEleves-(nbrGrp3*3))/2;
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
		
		
		//Pour chaque eleves pas deja fait dans la limite du nombre de groupe de 3
		int elevei = 0;
		while(indAD < nbrGrp3*3){
			if(!find(alreadyDone,elevei)){
				int[] bestNoteDuo = {indexOf(notesOrder,notes[elevei][1]),indexOf(notesOrder,notes[1][elevei])};
				Arrays.sort(bestNoteDuo);
				int nbrMA = 1;
				int[] meilleursAmis = new int[nbrEleves];
				
				//On trouve ses meillleurs amis
				for(int elevej = 2; elevej < notes[elevei].length; elevej++){
					if(elevei != elevej && !find(alreadyDone,elevej)){
						int[] noteDuo = {indexOf(notesOrder,notes[elevei][elevej]),indexOf(notesOrder,notes[elevej][elevei])};
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
				System.out.println("Eleve sélectionné : "+nomEleves[elevei]);
				System.out.println("Binomes sélectionnés : ");
				int pma=0;
				while(meilleursAmis[pma]!=0) {
					System.out.println(nomEleves[meilleursAmis[pma]]);
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
						if(elevei != elevek && meilleursAmis[MAi] != elevek && !find(alreadyDone,elevek)){
							int[] noteTrio = {indexOf(notesOrder,notes[elevei][elevek]),indexOf(notesOrder,notes[elevek][elevei]),
								indexOf(notesOrder,notes[elevei][meilleursAmis[MAi]]),indexOf(notesOrder,notes[meilleursAmis[MAi]][elevei]),
								indexOf(notesOrder,notes[elevek][meilleursAmis[MAi]]),indexOf(notesOrder,notes[meilleursAmis[MAi]][elevek])};
							Arrays.sort(noteTrio);
							
							if(isBetterThan(noteTrio,bestNoteTrio) == 2){
								deuxiemeAmi = meilleursAmis[MAi];
								troisiemeAmi = elevek;
								bestNoteTrio = noteTrio;
								System.out.println("Sélection du trinôme");
								System.out.println(nomEleves[deuxiemeAmi]);
								System.out.println(nomEleves[troisiemeAmi]);
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
				alreadyDone[indAD] = elevei;
				alreadyDone[indAD+1] = deuxiemeAmi;
				alreadyDone[indAD+2] = troisiemeAmi;
				indAD += 3;
				System.out.println("Composition finale du trinôme :");
				System.out.println(nomEleves[elevei] +"  "+nomEleves[deuxiemeAmi]+"  "+nomEleves[troisiemeAmi]);
				System.out.println("Appuyez sur entrée");
				sc=new Scanner(System.in);
				entre=sc.nextLine();
				
				res[elevei][deuxiemeAmi] = 1;res[deuxiemeAmi][elevei] = 1;
				res[elevei][troisiemeAmi] = 1;res[troisiemeAmi][elevei] = 1;
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
			if(!find(alreadyDone,elevei)){
				int meilleurAmi = -1;
				int[] bestNote = {-1,-1};
				for(int elevej = 0; elevej < nbrEleves;elevej++){
					if(!find(alreadyDone,elevej) && elevei != elevej){
						int[] noteDuo = {indexOf(notesOrder,notes[elevei][elevej]),indexOf(notesOrder,notes[elevej][elevei])};
						Arrays.sort(noteDuo);
						if(isBetterThan(noteDuo,bestNote) == 2){
							meilleurAmi = elevej;
							bestNote = noteDuo;
						}
					}
				}
				//Ils ne sont donc plus disponibles
				//System.out.println(indAD);
				alreadyDone[indAD] = elevei;
				alreadyDone[indAD+1] = meilleurAmi;
				indAD += 2;
				
				res[elevei][meilleurAmi] = 1;res[meilleurAmi][elevei] = 1;
				
				for(int i = 0; i<alreadyDone.length;i++){
					System.out.print(alreadyDone[i] + ", ");
				}
				System.out.println("");
			}
			elevei++;
		}
		for(int i = 0; i<nbrEleves;i++) {
			res[i][i] = 1;
		}
		return res;
	}
	
	public static int indexOf(String[] tab, String element){
		return Arrays.asList(tab).indexOf(element);
	}
	public static boolean find(int[] tab, int element){
		return IntStream.of(tab).anyMatch(x -> x == element) && element == 0;
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

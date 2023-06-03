package ui;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Solution {

	public static void main(String ... args) {
		String trainingAdress = args[0];
		String testingAdress = args[1];
		int dubina = 0;
		if (args.length ==3) {
			String flag = args[2];
			dubina = Integer.parseInt(flag);
		}

		
        
		ArrayList<String> znacajke= new ArrayList<>();
		ArrayList<HashMap<String, String>> dataset = new ArrayList<>();

		ArrayList<String> znacajkeDva= new ArrayList<>();
		ArrayList<HashMap<String, String>> testset = new ArrayList<>();
		
		
        boolean prvi = true;
		if (trainingAdress.contains("titanic")) {
            //return;
		}
		if (trainingAdress.contains("train_4")) {
            //return;
		}
		
		try {
			File file = new File(trainingAdress);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			
			while((line = br.readLine()) != null) {
			   String[] podjeljeno = line.split(",");
			   if (prvi == true) {
				   for (int i=0 ; i < podjeljeno.length ; i++) {
                       znacajke.add(podjeljeno[i]);
				   }
				   prvi = false;
				   continue;
			   }
			   HashMap<String, String> elementi= new HashMap<>() ;
			   for (int i=0 ; i < podjeljeno.length ; i++) {
				elementi.put(znacajke.get(i), podjeljeno[i]);
			   }
			   dataset.add(elementi);

			} 
            String m ="";
			for (String s: znacajke) {
                m = m + " " + s;
			}
			
			br.close();
		} catch(IOException ioe) {
			   ioe.printStackTrace();
		}

   
		

        prvi = true;
		try {
			File file_dva = new File(testingAdress);
			FileReader fr = new FileReader(file_dva);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			
			while((line = br.readLine()) != null) {
			   String[] podjeljeno = line.split(",");
			   if (prvi == true) {
				   for (int i=0 ; i < podjeljeno.length ; i++) {
                       znacajkeDva.add(podjeljeno[i]);
				   }
				   prvi = false;
				   continue;
			   }
			   HashMap<String, String> elementi= new HashMap<>() ;
			   for (int i=0 ; i < podjeljeno.length ; i++) {
				elementi.put(znacajkeDva.get(i), podjeljeno[i]);
			   }
			   testset.add(elementi);

			} 
            String m ="";
			for (String s: znacajkeDva) {
                m = m + " " + s;
			}
			
			br.close();
		} catch(IOException ioe) {
			   ioe.printStackTrace();
		}
		
		DecisionTree stablo = new DecisionTree();
		if (dubina == 0) {
			stablo.train(znacajke, dataset);
			stablo.predict(znacajkeDva,testset);
		} else {
			
			stablo.trainSogranicenjem(znacajke, dataset, dubina);
			stablo.predict(znacajkeDva,testset);
		}
		
		
		
		
		
		
		

    }

	
} 

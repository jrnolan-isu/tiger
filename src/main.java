//Author: Jack Nolan
//Date: 9/15/2020
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class main {

	static ArrayList<Double> studentIDs = new ArrayList<Double>();
	static ArrayList<String> majors = new ArrayList<String>();
	static ArrayList<String> genders = new ArrayList<String>();
	static ArrayList<Double> score = new ArrayList<Double>();
	static ArrayList<String> femaleStudents = new ArrayList<String>();
	

	public static void main(String[] args) throws IOException {
		// Excel Files
		File studentInfo = new File("Student Info.xlsx");
		File testRetakeScores = new File("Test Retake Scores.xlsx");
		File testScores = new File("Test Scores.xlsx");

		FileInputStream file = new FileInputStream(studentInfo);
		FileInputStream file2 = new FileInputStream(testRetakeScores);
		FileInputStream file3 = new FileInputStream(testScores);

		// Student Info Table
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();

		// Test Retake Scores
		XSSFWorkbook workbook2 = new XSSFWorkbook(file2);
		XSSFSheet secondSheet = workbook2.getSheetAt(0);
		Iterator<Row> iterator2 = secondSheet.iterator();

		// Test Scores
		XSSFWorkbook workbook3 = new XSSFWorkbook(file3);
		XSSFSheet thirdSheet = workbook3.getSheetAt(0);
		Iterator<Row> iterator3 = thirdSheet.iterator();

		categorize(iterator);
		organize(iterator3);
		compare(iterator2, score);
		int officialAverage = average(score);
		System.out.println("The Average is: "+officialAverage);
		
		computerGirls(majors, genders, studentIDs);
		
		Collections.sort(femaleStudents);
		
		//Print functions for the different ArrayLists
//		arrayPrinter(femaleStudents);
//		arrayPrinter(majors);
//		arrayPrinter(genders);
		
		String id = "jackryan3181@gmail.com";
		String name = "Jack Nolan";
		
		URL url = new URL("http://54.90.99.192:5000/");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type","application/json; utf-8");
		connection.setRequestProperty("Accept", "application/json");
	 	connection.setDoOutput(true);
		
		String input = "{"+id+": "+name+", "+officialAverage+": "+femaleStudents+"}";
		try(OutputStream output = connection.getOutputStream()){
			byte[] line = input.getBytes("utf-8");
			output.write(line, 0, line.length);
		}
		
		try(BufferedReader buff = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))){
			StringBuilder build = new StringBuilder();
			String response = null;
			while((response = buff.readLine()) != null){
				build.append(response.trim());
			}
			System.out.println(build.toString());
		}
		
		

				
	}
	
	
	
	public static void arrayPrinter(ArrayList<String> array) {
		for (int i = 0; i < array.size(); i++) {
			System.out.println(array.get(i));
		}
	}
	
	//STUDENT INFO
	public static void categorize(Iterator<Row> iterator) {
		while (iterator.hasNext()) {
			Row row = iterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				String type = cell.getCellType().toString();
				if (type == "NUMERIC") {
					// Reads in the value into the student ids array
					double temp = cell.getNumericCellValue();
					studentIDs.add(temp);
				} else if (type == "STRING") {
					String tempStringValue = cell.getStringCellValue();
					// GENDER
					if (tempStringValue.charAt(0) == 'M' || tempStringValue.charAt(0) == 'F') {
						genders.add(tempStringValue);
					}
					// eliminates the column titles
					else if (tempStringValue.equals("studentId") || tempStringValue.equals("major")
							|| tempStringValue.equals("gender")) {
					}
					// MAJOR
					else {
						majors.add(tempStringValue);

					}
				}

			}

		}
	}
	
	//TEST RETAKE SCORES
	public static void compare(Iterator<Row> iterator, ArrayList<Double> score) {
		while (iterator.hasNext()) {
			Row row = iterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String type = cell.getCellType().toString();
				if (type == "STRING") {
					// doesn't pay attention to the captions of the columns
				} else if(cell.getNumericCellValue() > 100) {
					for (int i = 0; i < studentIDs.size(); i++) {
						if(cell.getNumericCellValue() == studentIDs.get(i)){
							//update score
							System.out.println("IT DOES");
							double temper = cellIterator.next().getNumericCellValue();
							System.out.println(temper);
							//Swap out the old score with the new score
							if(score.get(i) < temper){
								score.remove(i);
								score.add(i, temper);
							}
							
							
						}
					}
				}
			}
		}
	}
	
	//TEST SCORES
	public static void organize(Iterator<Row> iterator){
		while(iterator.hasNext()){
			Row row = iterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			while(cellIterator.hasNext()){
				Cell cell = cellIterator.next();
				String type = cell.getCellType().toString();
				if (type == "STRING") {
					// doesn't pay attention to the captions of the columns
				}else if(cell.getNumericCellValue() <= 100){
					score.add(cell.getNumericCellValue());
				}
				
			}
		}
	}
	
	//AVERAGE FUNCTION
	public static int average(ArrayList<Double> score){
		int avg = 0;
		for (int i = 0; i < score.size(); i++) {
			avg += score.get(i);
		}
		avg = avg/score.size();
		
		return avg;
	}
	
	//This method adds the IDS of girls who are computer science majors to an ArrayList
	public static void computerGirls(ArrayList<String> majors, ArrayList<String> genders, ArrayList<Double> studentIDs){
		
		for (int i = 0; i < majors.size(); i++) {
			String temp2 = majors.get(i);
			String temp3 = genders.get(i);
			if(temp2.equals("computer science") && temp3.equals("F")){
				String temp4 = studentIDs.get(i).toString();
				femaleStudents.add(studentIDs.get(i).toString());
			}
			
		}
		
	}
}

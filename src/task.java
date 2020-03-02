import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.StringIndexOutOfBoundsException;

/*
 * An application for checking whether an IBAN is correct or not.
 * Application implies that the length of the IBAN input is always* correct and that the only alphabetic characters are the first two ones.
 * Writing a switch/case scenario for each and every country looks like it's tedious hours of work in a simple test scenario.
 * Author - Tomas Lukða.
 */

public class task {

	private static Scanner scanner;
	
	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		begin();
	}
	
	private static void begin(){
		System.out.println("Type in '1' for manual input, '2' for file input, anything else to close.");
		String input = scanner.nextLine();
		
		switch(input){
			case "1": {
				manualInput();
				break;
			}
			case "2": {
				fileInput();
				break;
			}
			default: {
				closeApp();
			}
		}
	}

	// Method to manually input an IBAN. Whether it is correct or not is displayed in the Console.
	private static void manualInput(){
		System.out.println("Input the IBAN to check:");
		String input = scanner.nextLine();
		System.out.println(input + ";" + checkIBAN(input));
		begin();
	}
	
	private static void fileInput(){
		System.out.println("Input file location and name:");
		String input = scanner.nextLine();
		ArrayList<String> IBANlist = new ArrayList<>();
		try {
			File inputFile = new File(input);
		    Scanner myReader = new Scanner(inputFile);
		    while (myReader.hasNextLine()) {
		        String IBAN = myReader.nextLine();
		        IBANlist.add(IBAN);
		    }
		    myReader.close();
		    
	    	FileWriter writer = new FileWriter(input + ".out");
		    for (String IBAN : IBANlist) {
		    	boolean isValid = checkIBAN(IBAN);
		    	writer.write(IBAN + ";" + isValid + "\n");
		    }
		    writer.close();
			System.out.println("Output file filled successfully.");
		} catch (FileNotFoundException e) {
		    System.out.println("File not found. Please try again.");
		    e.printStackTrace();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		} finally {
		    begin();
		}
	}
	
	// Method to end the application and close the scanner.
	private static void closeApp(){
		scanner.close();
	}
	
	// Method to check if IBAN presented is correct. Takes an unedited IBAN as input, returns true if it is correct, false otherwise.
	private static boolean checkIBAN(String originalIBAN){
		originalIBAN = originalIBAN.toUpperCase().trim(); // Converts the characters typed in to upper case and trims it, in case of human error.
		if (originalIBAN.length() > 34) return false;
		
		// The following line moves the first four characters to the end of the string, creates a different String object in case it's 
		String IBAN = originalIBAN.substring(4, originalIBAN.length()) + originalIBAN.substring(0, 4);

		// Replaces the IBAN Letters with corresponding integers.
		String IBANLetters = IBAN.substring(IBAN.length() - 4, IBAN.length() - 2);
		char char1 = IBANLetters.charAt(0);
		int int1 = char1 - 55;
		char char2 = IBANLetters.charAt(1);
		int int2 = char2 - 55;
		String replacement = int1 + "" + int2;
		IBAN = IBAN.replaceFirst(IBANLetters, replacement);
		
		try {
			int IBANint = Integer.valueOf(IBAN.substring(0, 9)); // Throws IndexOutOfBounds and NumberFormatException
			IBANint = IBANint % 97;
			IBAN = IBAN.substring(9, IBAN.length());
			while (IBAN.length() > 7) {
				IBANint = IBANint * 10000000 + Integer.valueOf(IBAN.substring(0, 7));
				IBANint = IBANint % 97;
				IBAN = IBAN.substring(7, IBAN.length());
			} 
			if (IBAN.length() != 0) {
				IBANint = (int) (IBANint * (Math.pow(10, IBAN.length())) + Integer.valueOf(IBAN));
			}
			return IBANint % 97 == 1;
		} catch ( StringIndexOutOfBoundsException e ){
			// IndexOutOfBounds means the IBAN is shorter than 9 digits. Currently shortest IBAN is Norwegian, which is 15 chars, so the IBAN is going to be false.
			return false; 
		} catch ( NumberFormatException e ) {
			// Found a letter in the BBAN. While technically some countries BBAN are alphanumeric (Can contain letters), this just looks like tedious few hours 
			// of manual work to write a case for each individual country (different countries have different places where characters are numeric and where they 
			// are not), so it prints out that there was a character found in the BBAN and returns that IBAN is incorrect. 
		    System.out.println("Alphabetic character found in BBAN.");
			return false;			
		}
	}
	
}

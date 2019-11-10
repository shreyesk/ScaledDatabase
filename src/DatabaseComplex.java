import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

public class DatabaseComplex {
	private HashMap<String, Database> myDatabases;
	private String myFileName;
	
	public DatabaseComplex() {
		Scanner scan = new Scanner("System.in");
		System.out.println("Enter file name.");
		String fileName = scan.nextLine();
		scan.close();
		myFileName = fileName;
		myDatabases = new HashMap<>();
		populate();
	}
	
	public DatabaseComplex(String fileName) {
		myFileName = fileName;
		myDatabases = new HashMap<>();
		populate();
	}
	
	private String quit() {
		save();
		return "Success.";
	}
	
	private String save() {
		try {
			PrintWriter printWriter = new PrintWriter(myFileName);
			int numOfDatabases = myDatabases.size();
			printWriter.println(numOfDatabases);
			for(String databaseName : myDatabases.keySet()) {
				printWriter.println(databaseName);
				
				Database database = myDatabases.get(databaseName);
				
				String rowName = database.getRowName();
				String[] columnNames = database.getColumnNames();
				String names = rowName + "\t";
				for(String columnName : columnNames) {
					names += columnName + " ";
				}
				names = names.substring(0, names.length() - 1);
				printWriter.println(names);
				
				int numOfEntries = database.size();
				printWriter.println(numOfEntries);
				
				String[] rows = database.getRows();
				for(String row : rows) {
					String entry = row + "\t";
					String[] columns = database.getColumns(row);
					for(String column : columns) {
						entry += column + " ";
					}
					entry = entry.substring(0, entry.length() - 1);
					printWriter.println(entry);
				}
			}
			printWriter.flush();
			printWriter.close();
			return "Success.";
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return helpMessage();
	}
	
	private String helpMessage() {
		return "Incorrect usage.";
	}
	
	//arguments = databaseName row
	private String delete(String[] parts) {
		String databaseName = parts[0];
		Database database = myDatabases.get(databaseName);
		
		String row = parts[1];
		database.removeRow(row);
		return "Success.";
	}
	
	private String take(String[] parts) {
		String oldDatabaseName = parts[0];
		Database oldDatabase = myDatabases.get(oldDatabaseName);
		
		String[] currentColumns = oldDatabase.getColumnNames();
		int[] currentColumnsIndexes = oldDatabase.getColumnNamesIndexes(currentColumns);
		String[] columnNamesToRemove = parts[1].split(" ");
		int[] indexesToRemove = oldDatabase.getColumnNamesIndexes(columnNamesToRemove);
		
		String[] columnNamesToRetain = new String[currentColumnsIndexes.length - indexesToRemove.length];
		//int[] indexesToRetain = new int[columnNamesToRetain.length];
		int i = 0;
		for(int index : currentColumnsIndexes) {
			
			columnNamesToRetain[i] = currentColumns[index];
			i++;
			for(int indexToRemove : indexesToRemove) {
				if(index == indexToRemove) {
					i--;
				}
			}
		}
		
		String retainedColumnNames = "";
		for(String columnName : columnNamesToRetain) {
			retainedColumnNames += columnName + " ";
		}
		retainedColumnNames = retainedColumnNames.substring(0, retainedColumnNames.length() - 1);
		
		String[] columnsToAdd = new String[oldDatabase.size()];
		String[] retrieveParts = new String[3];
		retrieveParts[0] = oldDatabaseName;
		retrieveParts[2] = retainedColumnNames;
		i = 0;
		for(String row : oldDatabase.getRows()) {
			retrieveParts[1] = row;
			String columns = retrieve(retrieveParts);
			columnsToAdd[i] = columns;
			i++;
		}
		
		String[] createParts = new String[3];
		createParts[0] = oldDatabaseName;
		createParts[1] = oldDatabase.getRowName();
		createParts[2] = retainedColumnNames;
		create(createParts);
		
		String newDatabaseName = parts[0];
		
		String[] addParts = new String[3];
		addParts[0] = newDatabaseName;
		i = 0;
		for(String row : oldDatabase.getRows()) {
			addParts[1] = row;
			addParts[2] = columnsToAdd[i];
			i++;
			add(addParts);
		}
		return "Success.";
	}
	
	private String eliminate(String[] parts) {
		String databaseName = parts[0];
		myDatabases.remove(databaseName);
		return "Success.";
	}
	
	//arguments = databaseName row columnNames(possibly) newColumns
	private String update(String[] parts) {
		String databaseName = parts[0];
		Database database = myDatabases.get(databaseName);
		
		String row = parts[1];
		String[] oldColumns = database.getColumns(row);
		
		String[] columnNames = parts[2].split(" ");
		String[] newColumns = parts[3].split(" ");
		
		//if columnNames have been supplied
		if(parts.length > 2) {
			int[] indexes = database.getColumnNamesIndexes(columnNames);
			int i = 0;
			for(int index : indexes) {
				oldColumns[index] = newColumns[i];
				i++;
			}
		} else {
			for(int i = 0; i < oldColumns.length; i++) {
				oldColumns[i] = newColumns[i];
			}
		}
		database.addRow(row, oldColumns);
		return "Success.";
	}
	
	private String push(String[] parts) {
		String databaseName = parts[0];
		Database oldDatabase = myDatabases.get(databaseName);
		String oldRowName = oldDatabase.getRowName();
		String[] oldColumnNames = oldDatabase.getColumnNames();
		String oldColumnNamesFormatted = "";
		for(String columnName : oldColumnNames) {
			oldColumnNamesFormatted += columnName + " ";
		}
		String newColumnNames = oldColumnNamesFormatted + parts[1];
		
		String[] createParts = new String[3];
		createParts[0] = databaseName;
		createParts[1] = oldRowName;
		createParts[2] = newColumnNames;
		
		create(createParts);
		Database newDatabase = myDatabases.get(databaseName);
		for(String row: oldDatabase.getRows()) {
			String[] columns = oldDatabase.getColumns(row);
			String[] newColumns = new String[columns.length + 1];
			for(int i = 0; i < columns.length; i++) {
				newColumns[i] = columns[i];
			}
			newColumns[newColumns.length - 1] = "";
			newDatabase.addRow(row, newColumns);
		}
		return "Success.";
	}
	
	//arguments = databaseName row columnNames(possibly)
	private String retrieve(String[] parts) {
		String databaseName = parts[0];
		Database database = myDatabases.get(databaseName);
		
		String row = parts[1];
		String[] columns = database.getColumns(row);
		
		String retrieved = "";
		//if columnNames have been supplied
		if(parts.length > 2) {
			String[] columnNames = parts[2].split(" ");
			int[] indexes = database.getColumnNamesIndexes(columnNames);
			String[] retrievedParts = new String[indexes.length];
			int i = 0;
			for(int index : indexes) {
				retrievedParts[i] = columns[index];
				i++;
			}
			for(String part : retrievedParts) {
				retrieved += part + " ";
			}
			retrieved = retrieved.substring(0, retrieved.length() - 1);
		} else {
			for(String part : columns) {
				retrieved += part + " ";
			}
			retrieved = retrieved.substring(0, retrieved.length() - 1);
		}
		return retrieved;
	}
	
	private String look(String[] parts) {
		String databaseName = parts[0];
		Database database = myDatabases.get(databaseName);
		
		String lookedUp = "";
		String rowName = database.getRowName();
		String[] columnNames = database.getColumnNames();
		String names = rowName + "\t";
		for(String columnName : columnNames) {
			names += columnName + " ";
		}
		names = names.substring(0, names.length() - 1);
		lookedUp += names + "\n";
		
		int numOfEntries = database.size();
		lookedUp += numOfEntries + "\n";
		
		String[] rows = database.getRows();
		for(String row : rows) {
			String entry = row + "\t";
			String[] columns = database.getColumns(row);
			for(String column : columns) {
				entry += column + " ";
			}
			entry = entry.substring(0, entry.length() - 1);
			lookedUp += entry + "\n";
		}
		lookedUp = lookedUp.substring(0, lookedUp.length() - 1);
		return lookedUp;
	}
	
	private String add(String[] parts) {
		String databaseName = parts[0];
		String row = parts[1];
		String[] columns = parts[2].split(" ");
		
		Database database = myDatabases.get(databaseName);
		database.addRow(row, columns);
		
		return "Success.";
	}
	
	//arguments = databaseName rowName columnNames(space separated)
	private String create(String[] parts) {
		if(parts.length == 3) {
			String databaseName = parts[0];
			String rowName = parts[1];
			String[] columnNames = parts[2].split(" ");
			Database database = new Database(rowName, columnNames);
			myDatabases.put(databaseName, database);
			return "Success.";
		} else {
			return helpMessage();
		}
	}
	
	public String decide(String[] components) {
		String type = components[0];
		String[] parts = new String[components.length - 1];
		for(int i = 1; i < components.length; i++) {
			parts[i - 1] = components[i];
		}
		try {
			if(type.equals("create") || type.equals("c")) {
				return create(parts);
			} else if(type.equals("add") || type.equals("a")) {
				return add(parts);
			} else if(type.equals("look") || type.equals("l")) {
				return look(parts);
			} else if(type.equals("retrieve") || type.equals("r")) {
				return retrieve(parts);
			} else if(type.equals("push") || type.equals("p")) {
				return push(parts);
			} else if(type.equals("update") || type.equals("u")) {
				return update(parts);
			} else if(type.equals("eliminate") || type.equals("e")) {
				return eliminate(parts);
			} else if(type.equals("take") || type.equals("t")) {
				return take(parts);
			} else if(type.equals("delete") || type.equals("d")) {
				return delete(parts);
			} else if(type.equals("save") || type.equals("s")) {
				return save();
			} else if(type.equals("quit") || type.equals("q")) {
				return quit();
			} else {
				return helpMessage();
			}
		} catch(Exception e) {
			return helpMessage();
		}
	}
	
	public void changeFileName() {
		Scanner scan = new Scanner(System.in);
		System.out.println("What file should be loaded from?");
		myFileName = scan.nextLine();
		scan.close();
		populate();
	}
	
	public void populate() {
		File file = new File(myFileName);
		try {
			Scanner scan = new Scanner(file);
			int numOfDatabases = scan.nextInt();
			scan.nextLine();
			for(int i = 0; i < numOfDatabases; i++) {
				String databaseName = scan.nextLine();
				String[] fieldNames = scan.nextLine().split("\t");
				String rowName = fieldNames[0];
				String[] columnNames = fieldNames[1].split(" ");
				int databaseSize = scan.nextInt();
				scan.nextLine();
				Database database = new Database(rowName, columnNames);
				for(int j = 0; j < databaseSize; j++) {
					String[] fieldItems = scan.nextLine().split("\t");
					String row = fieldItems[0];
					String[] columns = fieldItems[1].split(" ");
					database.addRow(row, columns);
				}
				myDatabases.put(databaseName, database);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("File of name \"" + myFileName + "\" does not exist.");
			changeFileName();
		}
	}
}

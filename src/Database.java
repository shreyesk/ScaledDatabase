import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Database {
	private Map<String, String[]> myDatabase;
	private String myRowName;
	private String[] myColumnNames;
	private HashMap<String, Integer> myColumnNamesIndexes;
	
	public Database(String rowName, String[] columnNames) {
		myRowName = rowName;
		myColumnNames = columnNames;
		myDatabase = new TreeMap<>();
		myColumnNamesIndexes = new HashMap<>();
		for(int i = 0; i < columnNames.length; i++) {
			myColumnNamesIndexes.put(myColumnNames[i], new Integer(i));
		}
	}
	
	public void removeRow(String row) {
		myDatabase.remove(row);
	}
	
	public void editRow(String row, String column, String newValue) {
		int columnIndex = myColumnNamesIndexes.get(column);
		String[] columns = myDatabase.get(row);
		columns[columnIndex] = newValue;
	}
	
	public void addRow(String row, String[] columns) {
		myDatabase.put(row, columns);
	}
	
	public void addRow(String row) {
		String[] columns = new String[myColumnNames.length];
		myDatabase.put(row, columns);
	}
	
	public String[] getRows() {
		Set<String> rows = myDatabase.keySet();
		String[] formattedRows = new String[rows.size()];
		int i = 0;
		for(String row : rows) {
			formattedRows[i] = row;
			i++;
		}
		return formattedRows;
	}
	
	public String[] getColumns(String row) {
		return myDatabase.get(row);
	}
	
	public String getRowName() {
		return myRowName;
	}
	
	public String[] getColumnNames() {
		return myColumnNames;
	}
	
	public int[] getColumnNamesIndexes(String[] columnNames) {
		int[] indexes = new int[columnNames.length];
		for(int i = 0; i < columnNames.length; i++) {
			indexes[i] = myColumnNamesIndexes.get(columnNames[i]);
		}
		return indexes;
	}
	
	public int size() {
		return myDatabase.size();
	}
	
}

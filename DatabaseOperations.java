package pl.java.read;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import nl.knaw.dans.common.dbflib.CorruptedTableException;
import nl.knaw.dans.common.dbflib.DateValue;
import nl.knaw.dans.common.dbflib.DbfLibException;
import nl.knaw.dans.common.dbflib.Field;
import nl.knaw.dans.common.dbflib.NumberValue;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.StringValue;
import nl.knaw.dans.common.dbflib.Table;
import nl.knaw.dans.common.dbflib.Type;
import nl.knaw.dans.common.dbflib.Value;

public class DatabaseOperations {
	/**
	 * Instance of class that invoke this class. It is used to localization difference messages
	 */
	private Cardinal cardinal;
	/**
	 * Instance of database before beginning operations on database, user ought open table use method <code>open()</code> and after finalize operations,
	 * ought close database by method <code>close()</code>. It is very important because might corrupted table.
	 */
	private Table table;
	/**
	 * This parameter shows position of record which to be for example updated. This parameter default is set on -1 and it is set in method <code>isContains()</code>.
	 */
	private int position = -1;
	
	/**
	 * It is constructor of this class that sets fields of this class using parameters.
	 * @param database contains path to database.
	 * @param cardinal it is instance of class that invoke this constructor, it is using to set locations of graphic messages.
	 */
	public DatabaseOperations(File database, Cardinal cardinal) {
		this.cardinal = cardinal;
		this.table = new Table(database);
	}
	
	/**
	 * Open database, before however operations in database user need use it.
	 */
	public void open() {	
		try {
			table.open();
		} catch(CorruptedTableException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Table is corrupted.", "Error message", JOptionPane.ERROR_MESSAGE);
		} catch(IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Opening database error.", "Error message", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Update database on selected position adding or subtracting quantity of items.
	 * @param position tell method which position ought to be updated
	 * @param quantity specify quantity of item to adding or subtracting
	 */
	public void update(int position, int quantity) {
		System.out.println("W bazie jest " + (table.getRecordCount() - 1) + " wierszy + 1");
		try {
			Record record = table.getRecordAt(position);
			double ilosc = (double) record.getNumberValue("ILOSC") + quantity;
			double ilosc_rz = (double) record.getNumberValue("ILOSC_RZ") + quantity;
			Map<String, Value> map = new HashMap<>();	
			map.put("ROK", new NumberValue(record.getNumberValue("ROK")));
			map.put("SYMBOL", new StringValue(record.getStringValue("SYMBOL")));
			map.put("MIESIAC", new NumberValue(record.getNumberValue("MIESIAC")));
			map.put("NUMER", new NumberValue(record.getNumberValue("NUMER")));
			map.put("DATA_DOK", new DateValue(record.getDateValue("DATA_DOK")));
			map.put("MAGAZYN", new StringValue(record.getStringValue("MAGAZYN")));
			map.put("INDEX", new StringValue(record.getStringValue("INDEX")));
			map.put("CENA", new NumberValue(record.getNumberValue("CENA")));
			map.put("CENA_RZ", new NumberValue(record.getNumberValue("CENA_RZ")));
			map.put("CENA_WAZ", new NumberValue(record.getNumberValue("CENA_WAZ")));
			
			Number number = record.getNumberValue("CENA_ZAK");
			if(number == null) {
				System.out.println("no \"CENA_ZAK\"");
				map.put("CENA_ZAK", new NumberValue(0));
			} else {
				map.put("CENA_ZAK", new NumberValue(record.getNumberValue("CENA_ZAK")));
			}
					
			map.put("ILOSC", new NumberValue(ilosc));
			map.put("ILOSC_RZ", new NumberValue(ilosc_rz));
			map.put("RABAT", new NumberValue(record.getNumberValue("RABAT")));
			map.put("RABAT_RZ", new NumberValue(record.getNumberValue("RABAT_RZ")));
			
			String string = record.getStringValue("JM");
			if(string == null) {
				System.out.println("no \"JM\"");
				map.put("JM", new StringValue(""));
			} else {
				map.put("JM", new StringValue(record.getStringValue("JM")));
			}
			
			map.put("STAWKAVAT", new NumberValue(record.getNumberValue("STAWKAVAT")));
			map.put("STAWKA_RZ", new NumberValue(record.getNumberValue("STAWKA_RZ")));
			map.put("NAZWA", new StringValue(record.getStringValue("NAZWA")));
			
			byte[] tab = record.getRawValue(new Field("OPIS", Type.MEMO));
			if(tab == null) {
				System.out.println("no \"OPIS\"");
				map.put("OPIS", new StringValue(""));
			} else {
				map.put("OPIS", new StringValue(record.getStringValue("OPIS")));
			}
					
			table.updateRecordAt(position, new Record(map));
			
			record = table.getRecordAt(position);
			Number actualAmount = record.getNumberValue("ILOSC");
			String actualAmountAfterFormat = String.format("%.0f", actualAmount);
			JOptionPane.showMessageDialog(cardinal, "Changed record, actual amount equals: " + actualAmountAfterFormat + '.', "Information message", JOptionPane.INFORMATION_MESSAGE);
		} catch(CorruptedTableException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Table is corruptedd.", "Error message", JOptionPane.ERROR_MESSAGE);
		} catch(IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Table operation error.", "Error message", JOptionPane.ERROR_MESSAGE);
		} catch(DbfLibException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Library error.", "Error message", JOptionPane.ERROR_MESSAGE);
		}	
	}
	
	/**
	 * Update database on selected position by updating name of item, changed last 13 characters using code.
	 * @param position position tell method which position ought to be updated
	 * @param barcode by this code ought to be update name of item
	 */
	public void update(int position, String barcode) {
		System.out.println("W bazie jest " + (table.getRecordCount() - 1) + " wierszy + 1");
		try {
			Record record = table.getRecordAt(position);
			String nazwa = record.getStringValue("NAZWA").substring(0, 87) + barcode;
			Map<String, Value> map = new HashMap<>();	
			map.put("ROK", new NumberValue(record.getNumberValue("ROK")));
			map.put("SYMBOL", new StringValue(record.getStringValue("SYMBOL")));
			map.put("MIESIAC", new NumberValue(record.getNumberValue("MIESIAC")));
			map.put("NUMER", new NumberValue(record.getNumberValue("NUMER")));
			map.put("DATA_DOK", new DateValue(record.getDateValue("DATA_DOK")));
			map.put("MAGAZYN", new StringValue(record.getStringValue("MAGAZYN")));
			map.put("INDEX", new StringValue(record.getStringValue("INDEX")));
			map.put("CENA", new NumberValue(record.getNumberValue("CENA")));
			map.put("CENA_RZ", new NumberValue(record.getNumberValue("CENA_RZ")));
			map.put("CENA_WAZ", new NumberValue(record.getNumberValue("CENA_WAZ")));
			
			Number number = record.getNumberValue("CENA_ZAK");
			if(number == null) {
				System.out.println("no \"CENA_ZAK\"");
				map.put("CENA_ZAK", new NumberValue(0));
			} else {
				map.put("CENA_ZAK", new NumberValue(record.getNumberValue("CENA_ZAK")));
			}
					
			map.put("ILOSC", new NumberValue(record.getNumberValue("ILOSC")));
			map.put("ILOSC_RZ", new NumberValue(record.getNumberValue("ILOSC_RZ")));
			map.put("RABAT", new NumberValue(record.getNumberValue("RABAT")));
			map.put("RABAT_RZ", new NumberValue(record.getNumberValue("RABAT_RZ")));
			
			String string = record.getStringValue("JM");
			if(string == null) {
				System.out.println("no \"JM\"");
				map.put("JM", new StringValue(""));
			} else {
				map.put("JM", new StringValue(record.getStringValue("JM")));
			}
			
			map.put("STAWKAVAT", new NumberValue(record.getNumberValue("STAWKAVAT")));
			map.put("STAWKA_RZ", new NumberValue(record.getNumberValue("STAWKA_RZ")));
			map.put("NAZWA", new StringValue(nazwa));
			
			byte[] tab = record.getRawValue(new Field("OPIS", Type.MEMO));
			if(tab == null) {
				System.out.println("no \"OPIS\"");
				map.put("OPIS", new StringValue(""));
			} else {
				map.put("OPIS", new StringValue(record.getStringValue("OPIS")));
			}
					
			table.updateRecordAt(position, new Record(map));
			
			record = table.getRecordAt(position);
			JOptionPane.showMessageDialog(cardinal, "Updated record", "Information message", JOptionPane.INFORMATION_MESSAGE);
		} catch(CorruptedTableException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Table is corrupted.", "Error message", JOptionPane.ERROR_MESSAGE);
		} catch(IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Table operation error.", "Error message", JOptionPane.ERROR_MESSAGE);
		} catch(DbfLibException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Library error.", "Error message", JOptionPane.ERROR_MESSAGE);
		}	
	}
	
	/**
	 * Adding new record to database.
	 * @param record new row to add to database
	 * @return <code>true</code> if adding record is success, <code>false</code> in otherwise
	 */
	public boolean add(Record record) {
		try {
			this.table.addRecord(
				record.getNumberValue("ROK"),
				record.getStringValue("SYMBOL"),
				record.getNumberValue("MIESIAC"),
				record.getNumberValue("NUMER"),
				Calendar.getInstance().getTime(),
				record.getStringValue("MAGAZYN"),
				record.getStringValue("INDEX"),
				record.getNumberValue("CENA"),
				record.getNumberValue("CENA_RZ"),
				record.getNumberValue("CENA_WAZ"),
				record.getNumberValue("CENA_ZAK"),
				record.getNumberValue("ILOSC"),
				record.getNumberValue("ILOSC_RZ"),
				record.getNumberValue("RABAT"),
				record.getNumberValue("RABAT_RZ"),
				record.getStringValue("JM"),
				record.getNumberValue("STAWKAVAT"),
				record.getNumberValue("STAWKA_RZ"),
				record.getStringValue("NAZWA"),
				record.getStringValue("OPIS")
			);
			JOptionPane.showMessageDialog(cardinal, "Added new record to database", "Information message", JOptionPane.INFORMATION_MESSAGE);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Table operation error.", "Error message", JOptionPane.ERROR_MESSAGE);
		} catch (DbfLibException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Library error.", "Error message", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}
	
	/**
	 * This method removing record from database.
	 * @param position represents position of record ought to be removed
	 * @return <code>true</code> if removing record is success, <code>false</code> in otherwise
	 */
	public boolean remove(int position) {
		try {
			this.table.deleteRecordAt(position);
			JOptionPane.showMessageDialog(cardinal, "Removed record from database", "Information message", JOptionPane.INFORMATION_MESSAGE);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Table operation error.", "Error message", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}
	
	/**
	 * This method search expression in database and return <code>boolean</code> value.
	 * @param expression <code>String</code> parameter that is search in all cells of database
	 * @return <code>true</code> if database contains expression, <code>false</code> in otherwise
	 */
	public boolean isContains(String expression) {
		int amount = table.getRecordCount();
		expression = expression.toLowerCase();
		Record record;
		String name;
		try {
			for(int i = 0; i < amount; i++) {
				record = table.getRecordAt(i);
				name = record.getStringValue("NAZWA").toLowerCase();
				if(name.contains(expression)) {
					position = i;
					return true;
				}
			}
		} catch(CorruptedTableException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Table is corrupted.", "Error message", JOptionPane.ERROR_MESSAGE);
		} catch(IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Table operation error.", "Error message", JOptionPane.ERROR_MESSAGE);
		}
		position = -1;
		return false;
	}
	
	/**
	 * This method return first position on which exist finding expression.
	 * @return position on which found expression or -1 if expression did not find
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * This method create table contains all column names.
	 * @return table contains all column names of database
	 */
	public String[] getColumnNames() {
		List<Field> list = table.getFields();
		int length = list.size();
		String[] table = new String[length];
		for(int i = 0; i < length; i++) {
			table[i] = list.get(i).getName();
		}
		return table;
	}
	
	/**
	 * This method create table contains all cells of database.
	 * @return table contains all cells of database or <code>null</code> if create table fail
	 */
	public Object[][] getCellNames() {
		int records = table.getRecordCount();
		int columns = table.getFields().size();
		Object[][] table = new Object[records][columns];
		for(int i = 0; i < records; i++) {
			try {
				Record record = this.table.getRecordAt(i);
				table[i][0] = record.getNumberValue("ROK");
				table[i][1] = record.getStringValue("SYMBOL");
				table[i][2] = record.getNumberValue("MIESIAC");
				table[i][3] = record.getNumberValue("NUMER");
				table[i][4] = record.getDateValue("DATA_DOK");
				table[i][5] = record.getStringValue("MAGAZYN");
				table[i][6] = record.getStringValue("INDEX");
				table[i][7] = record.getNumberValue("CENA");
				table[i][8] = record.getNumberValue("CENA_RZ");
				table[i][9] = record.getNumberValue("CENA_WAZ");
				table[i][10] = record.getNumberValue("CENA_ZAK");
				table[i][11] = record.getNumberValue("ILOSC");
				table[i][12] = record.getNumberValue("ILOSC_RZ");
				table[i][13] = record.getNumberValue("RABAT");
				table[i][14] = record.getNumberValue("RABAT_RZ");
				table[i][15] = record.getStringValue("JM");
				table[i][16] = record.getNumberValue("STAWKAVAT");
				table[i][17] = record.getNumberValue("STAWKA_RZ");
				table[i][18] = record.getStringValue("NAZWA");
				table[i][19] = record.getStringValue("OPIS");
			} catch(CorruptedTableException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(this.cardinal, "Table is corrupted.", "Error message", JOptionPane.ERROR_MESSAGE);
				return null;
			} catch(IOException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(this.cardinal, "Table operation error.", "Error message", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		return table;
	}
	
	/**
	 * Close database, after completed all operations in database user need use it.
	 */
	public void close() {
		try {
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(cardinal, "Closing database error.", "Error message", JOptionPane.ERROR_MESSAGE);
		}
	}
}

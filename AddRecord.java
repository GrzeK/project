package pl.java.read;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import nl.knaw.dans.common.dbflib.DateValue;
import nl.knaw.dans.common.dbflib.NumberValue;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.StringValue;
import nl.knaw.dans.common.dbflib.Value;

/**
 * This class is use for adding new record to database. Implements interface <code>KeyListener</code> that listen key pressed from <code>JTextField</code>. 
 * And extends class <code>JPanel</code> that is used to create <code>JDialog</code>.
 */
public class AddRecord extends JPanel implements KeyListener{
	/**
	 * No changed field that express quantity of columns in database.
	 */
	private static final int databaseRecords = 20;
	/**
	 * Instance of class that creates graphic environment of this class.
	 */
	private JDialog dialog;
	/**
	 * Button that is use for accept of inputed row.
	 */
	private JButton accept;
	/**
	 * Button that is use for close window.
	 */
	private JButton cancel;	
	/**
	 * Table of labels which are near <code>JTextField</code> or <code>JComboBox</code>.
	 */
	private JLabel[] labels;
	/**
	 * Table of components which are use to input data.
	 */
	private JComponent[] components;
	/**
	 * Record of data contains 1 row.
	 */
	private Record record;
	/**
	 * Class represents key and value, key is the name of column, value is component use to input value of key.
	 */
	private Map<String, boolean[]> map;
	//This non-static block fill map
	{
		String names[] = {"ROK", "SYMBOL", "MIESIAC", "NUMER", "DATA_DOK", "MAGAZYN", "INDEX", "CENA", "CENA_RZ", "CENA_WAZ", "CENA_ZAK", "ILOSC", "ILOSC_RZ", "RABAT", "RABAT_RZ", "JM",
			"STAWKAVAT", "STAWKA_RZ", "NAZWA", "OPIS"};
		
		labels = new JLabel[20];
		components = new JComponent[20];
		map = new HashMap<String, boolean[]>();
		map.put("isJTextField", new boolean[] {true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, true, true, true});
		map.put("isJComboBoxString", new boolean[] {false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false});
		map.put("isJComboBoxInteger", new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false});
		map.put("isDisabled", new boolean[] {true, false, true, false, true, true, true, false, true, true, true, false, true, false, true, false, false, true, false, false});
		map.put("isActionListener", new boolean[] {false, false, false, false, false, false, false, true, false, false, false, true, false, true, false, false, true, false, false, false});
		for(int i = 0; i < databaseRecords; i++) {
			labels[i] = new JLabel(names[i]);
			if(map.get("isJTextField")[i]) {
				components[i] = new JTextField();
			} else if(map.get("isJComboBoxString")[i]) {
				if(names[i].equals("SYMBOL")) {
					components[i] = new JComboBox<String>(new String[] {"WZ", "PZ", "KPZ"});
				} else if(names[i].equals("JM")) {
					components[i] = new JComboBox<String>(new String[] {"szt.", "op.", "kpl."});
				}
			} else if(map.get("isJComboBoxInteger")[i]){
				components[i] = new JComboBox<Integer>(new Integer[] {5, 8, 23});
			}
		}
	}
	/**
	 * It represents code which user read from item by reader.
	 */
	private long barcode;
	/**
	 * This value is OK when data inputed are correct.
	 */
	protected boolean ok;
	
	/**
	 * Constructor call two private method, first create graphic panel that will be added to <code>JDialog</code>, second get default values to certain 
	 * inputed fields.
	 */
	public AddRecord() {
		this.addingPanel();
		this.initializePanel();
	}
	
	/**
	 * Create inputed panel using <code>GridBagLayout</code>, added labels and component to which user can input data. This method also  add two <code>JButton</code>.
	 */
	private void addingPanel() {
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.weightx = 100;
		constraints.weighty = 100;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(2, 2, 2, 2);
		
		for(int i = 0; i < databaseRecords; i++) {
			constraints.gridx = 0;
			constraints.gridy = i;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.EAST;
			this.add(labels[i], constraints);
			
			constraints.gridx = 1;
			constraints.gridy = i;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.anchor = GridBagConstraints.CENTER;
			JComponent component = components[i];
			if(map.get("isActionListener")[i]) {
				if(map.get("isJTextField")[i]) {
					((JTextField) component).addKeyListener(this);
				} else if(map.get("isJComboBoxInteger")[i]) {
					((JComboBox<?>) component).addActionListener(new ActionListener() {			
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							Object itemObject = ((JComboBox<?>) component).getSelectedItem();
							int itemInt = (int) itemObject;
							((JTextField) components[17]).setText(String.valueOf(itemInt));
						}
					});
				}
			}
			this.add(components[i], constraints);
		}
		
		constraints.gridx = 0;
		constraints.gridy = 20;
		accept = new JButton("OK");
		accept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					AddRecord.this.checkCorrect();
					AddRecord.this.createRecord();
					
					ok = true;
					dialog.setVisible(false);
				} catch(IllegalArgumentException e) {
					JOptionPane.showMessageDialog(AddRecord.this, e.getMessage(), "Warning message", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		this.add(accept, constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 20;
		cancel = new JButton("Canel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dialog.setVisible(false);
			}
		});
		this.add(cancel, constraints);
	}
	
	/**
	 * This method fill certain components by default values.
	 */
	private void initializePanel() {
		LocalDate localDate = LocalDate.now();
		int year = localDate.getYear();
		((JTextField) components[0]).setText(String.valueOf(year));
		
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM");
		String month = localDate.format(formatter);
		((JTextField) components[2]).setText(String.valueOf(month));
		
		String date = localDate.format(DateTimeFormatter.BASIC_ISO_DATE);
		((JTextField) components[4]).setText(date);
		
		((JTextField) components[5]).setText("001");
		
		((JTextField) components[6]).setText("?????");	//I do not know how to count this field
		
		((JTextField) components[10]).setText("0");
		
		for(int i = 0; i < databaseRecords; i++) {
			if(map.get("isDisabled")[i]) {
				components[i].setEnabled(false);
			}
		}
	}
	
	/**
	 * This method check correct of inputed data if data are incorrect throw an exception.
	 * @throws IllegalArgumentException if data inputed by user are incorrect
	 */
	private void checkCorrect() {
		try {
			int intField = Integer.parseInt(((JTextField) components[3]).getText());
			if(intField < 0) {
				throw new IllegalArgumentException("value of field \"NUMER\" must be positive");
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Field \"NUMER\" is not number");
		}
		
		try {
			String stringField = ((JTextField) components[7]).getText();
			Locale locale = Locale.getDefault();
			
			NumberFormat numberFormatNumber = NumberFormat.getNumberInstance(locale);
			Number numberField = numberFormatNumber.parse(stringField);
			double doubleField = numberField.doubleValue();
			if(doubleField > 0) {
				stringField = String.format(locale, "%.2f", doubleField);
				((JTextField) components[7]).setText(stringField);
				((JTextField) components[8]).setText(stringField);
				((JTextField) components[9]).setText(stringField);
			} else {
				throw new IllegalArgumentException("value of field \"CENA\" must be positive");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException("Field \"CENA\" is not correct");
		}
		
		try {
			int intField = Integer.parseInt(((JTextField) components[11]).getText());
			if(intField < 0) {
				throw new IllegalArgumentException("value of field \"ILOSC\" must be positive");
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Field \"ILOSC\" is not number");
		}
		
		try {
			int intField = Integer.parseInt(((JTextField) components[13]).getText());
			if(intField < 0) {
				throw new IllegalArgumentException("value of field \"RABAT\" must be positive");
			} else if(intField > 100) {
				throw new IllegalArgumentException("value of field \"RABAT\" must be positive");
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Field \"RABAT\" is not number");
		}
		
		if(((JTextField) components[17]).getText().equals("")) {
			throw new IllegalArgumentException("Choose value in field \"STAWKAVAT\"");
		}
		
		if(((JTextField) components[18]).getText().equals("")) {
			throw new IllegalArgumentException("Enter value to field \"NAZWA\"");
		}
	}
	
	/**
	 * This method create record using data inputed that inputed user, and save record in class <code>Record</code> as this class field. 
	 * This method is deprecation, because database DBF used in this program is old, and use old class <code>Date</code> that is do not 
	 * recommend to use.
	 */
	@SuppressWarnings("deprecation")
	private void createRecord() {
		Map<String, Value> map = new HashMap<>();
		
		Number number = null;
		Date date = null;
		String string = null;
		
		number = Integer.parseInt(((JTextField) components[0]).getText());
		map.put("ROK", new NumberValue(number));
		
		string = ((JComboBox<?>) components[1]).getSelectedItem().toString();
		map.put("SYMBOL", new StringValue(string));
		
		number = Integer.parseInt(((JTextField) components[2]).getText());
		map.put("MIESIAC", new NumberValue(number));
		
		number = Integer.parseInt(((JTextField) components[3]).getText());
		map.put("NUMER", new NumberValue(number));
	
		string = ((JTextField) components[4]).getText();
		int year = Integer.parseInt(string.substring(0, 5));
		int month = Integer.parseInt(string.substring(5, 7));
		int day = Integer.parseInt(string.substring(7));
		date = new Date(year, month, day);
		map.put("DATA_DOK", new DateValue(date));
		
		string = ((JTextField) components[5]).getText();
		map.put("MAGAZYN", new StringValue(string));
		
		string = ((JTextField) components[6]).getText();
		map.put("INDEX", new StringValue(string));
		
		string = ((JTextField) components[7]).getText();
		if(string.contains(",")) {
			string = string.replace(',', '.');
		}
		number = Double.parseDouble(string);
		map.put("CENA", new NumberValue(number));
		
		string = ((JTextField) components[8]).getText();
		if(string.contains(",")) {
			string = string.replace(',', '.');
		}
		number = Double.parseDouble(string);
		map.put("CENA_RZ", new NumberValue(number));
		
		string = ((JTextField) components[9]).getText();
		if(string.contains(",")) {
			string = string.replace(',', '.');
		}
		number = Double.parseDouble(string);
		map.put("CENA_WAZ", new NumberValue(number));
		
		number = Integer.parseInt(((JTextField) components[10]).getText());
		map.put("CENA_ZAK", new NumberValue(number));
				
		number = Integer.parseInt(((JTextField) components[11]).getText());
		map.put("ILOSC", new NumberValue(number));
		
		number = Integer.parseInt(((JTextField) components[12]).getText());
		map.put("ILOSC_RZ", new NumberValue(number));
		
		number = Integer.parseInt(((JTextField) components[13]).getText());
		map.put("RABAT", new NumberValue(number));
		
		number = Integer.parseInt(((JTextField) components[14]).getText());
		map.put("RABAT_RZ", new NumberValue(number));
		
		string = ((JComboBox<?>) components[15]).getSelectedItem().toString();
		map.put("JM", new StringValue(string));
		
		number = Integer.parseInt(((JComboBox<?>) components[16]).getSelectedItem().toString());
		map.put("STAWKAVAT", new NumberValue(number));
		
		number = Integer.parseInt(((JTextField) components[17]).getText());
		map.put("STAWKA_RZ", new NumberValue(number));
		
		string = ((JTextField) components[18]).getText();
		if(barcode != 0) {
			StringBuilder builder = new StringBuilder(string);
			while(builder.length() < 88) {
				builder.append(' ');
			}
			string = builder.substring(0, 87) + barcode;
		}
		map.put("NAZWA", new StringValue(string));
		
		string = ((JTextField) components[19]).getText();
		map.put("OPIS", new StringValue(string));
		
		record = new Record(map);
	}
	
	/**
	 * This method return instance of class <code>Record</code>.
	 * @return row of database fill by user
	 */
	public Record getInputRecord() {

		return record;
	}
	
	/**
	 * User uses this method to input into this class new code of item that will be use to create new record.
	 * @param barCode by this code will be update name of item
	 */
	public void putBarCode(long barCode) {
		this.barcode = barCode;
	}
	
	/**
	 * Clear all inputed component of this class.
	 */
	public void clearFields() {
		for(int i = 0; i < databaseRecords; i++) {
			if(map.get("isJTextField")[i]) {
				((JTextField) components[i]).setText("");
			}
		}
		this.initializePanel();
	}
	
	/**
	 * Show graphic window creating as <code>JDialog</code>.
	 * @param parent put to program parent window o which will be show the graphic frame of this class
	 * @return <code>true</code> if creating new record was success, <code>false</code> in otherwise
	 */
	public boolean showDialog(Component parent) {
		ok = false;
		Frame owner = null;
		if(parent instanceof Frame) {
			owner = (Frame) parent;
		} else {
			owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
		}
		if(dialog == null || dialog.getOwner() != owner) {
			dialog = new JDialog(owner, "Add new record", true);
			dialog.add(this);
			dialog.getRootPane().setDefaultButton(accept);
			dialog.pack();
		}
		dialog.setVisible(true);
		return ok;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		if(source == components[7]) {
			String text = ((JTextField) components[7]).getText();
			((JTextField) components[8]).setText(text);
			((JTextField) components[9]).setText(text);
		} else if(source == components[11]) {
			String text = ((JTextField) components[11]).getText();
			((JTextField) components[12]).setText(text);
		} else if(source == components[13]) {
			String text = ((JTextField) components[13]).getText();
			((JTextField) components[14]).setText(text);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
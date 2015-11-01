package pl.java.read;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileView;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import nl.knaw.dans.common.dbflib.BooleanValue;
import nl.knaw.dans.common.dbflib.CorruptedTableException;
import nl.knaw.dans.common.dbflib.DateValue;
import nl.knaw.dans.common.dbflib.DbfLibException;
import nl.knaw.dans.common.dbflib.Field;
import nl.knaw.dans.common.dbflib.NumberValue;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.StringValue;
import nl.knaw.dans.common.dbflib.Table;
import nl.knaw.dans.common.dbflib.Value;

/**
 * Class <code>Cardinal</code> is cardinal class, contains <code>main</code>. Here are created swing components.
 * @author Grzegorz Kopiec
 * @version 1.0 (2015-10-11)
 */
public class Cardinal extends JFrame {
	/**
	 * Instance of class <code>AddRecord</code> which shows window in which, may input date need to add new record.
	 */
	private AddRecord addRecord;
	/**
	 * Instance of class <code>UpdateRecord</code> which shows table of all records. In it can remove, update or show records depend on parameters of constructor.
	 */
	private UpdateRecord updateRecord;
	/**
	 * This reference hold patch of file with database.
	 */
	private File database;
	/**
	 * Preferences of program such as width, height and localization of database.
	 */
	private Preferences preferences;
	
	/**
	 * Constructor of this class which initial look of cardinal window.
	 */
	public Cardinal() {
		super("Read from dbf database");
		this.setLayout(new BorderLayout());
		this.setLocationByPlatform(true);
		this.addWindowListener(new WindowListen());
		this.setProgramIcon();
		this.setLookAndFeel();
		this.initialSwing();
		this.loadPreferences();
	}
	
	/**
	 * This method sets default look of the program to windows look. If the operation fail then look is sets to default.
	 */
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(this, "Did not found windows view, selected on default metal view.", "Warning message", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * This method sets default icon of program.
	 */
	private void setProgramIcon() {
		ImageIcon imageIcon = new ImageIcon("ProgramIcon.png");
		Image image = imageIcon.getImage();
		this.setIconImage(image);
	}
	
	/**
	 * This block of code initialize graphic view of program
	 */
	private void initialSwing() {
		Action openAction = new OpenAction();
		Action closeAction = new CloseAction();
		Action addAction = new AddAction();
		Action newAction = new NewAction();
		Action removeAction = new RemoveAction();
		Action showAction = new ShowAction();
		Action exitAction = new ExitAction();
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(openAction);
		fileMenu.add(closeAction);
		fileMenu.addSeparator();
		fileMenu.add(addAction);
		fileMenu.add(newAction);
		fileMenu.add(removeAction);
		fileMenu.add(showAction);
		fileMenu.addSeparator();
		fileMenu.add(exitAction);
		menuBar.add(fileMenu);
		this.setJMenuBar(menuBar);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.weightx = 100;
		constraints.weighty = 100;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(10, 10, 10, 10);
		centerPanel.add(new JButton(addAction), constraints);
		
		constraints.gridy = 1;
		centerPanel.add(new JButton(newAction), constraints);
		
		constraints.gridy = 2;
		centerPanel.add(new JButton(removeAction), constraints);
		
		constraints.gridy = 3;
		centerPanel.add(new JButton(showAction), constraints);
		
		constraints.gridy = 4;
		centerPanel.add(new JButton(exitAction), constraints);
		this.add(centerPanel, BorderLayout.CENTER);
		this.pack();
	}
	
	/**
	 * Load preferences of program and set it. If preferences load fail, set default preferences of program.
	 */
	private void loadPreferences() {
		preferences = Preferences.userNodeForPackage(this.getClass());
		this.setSize(preferences.getInt("width", 140), preferences.getInt("height", 285));
		String path = preferences.get("path", null);
		if(path != null) {
			database = new File(path);
		}
	}
	
	/**
	 * Save preferences during closing program
	 */
	private void savaPreferences() {
		preferences.putInt("width", Cardinal.this.getWidth());
		preferences.putInt("height", Cardinal.this.getHeight());
		if(Cardinal.this.database == null) {
			preferences.put("path", "");
		} else {
			if(Cardinal.this.database.equals("")) {
				preferences.put("path", "");
			} else {
				preferences.put("path", Cardinal.this.database.getAbsolutePath());
			}
		}
	}
	
	/**
	 * From here, there are called most important method.
	 * @param args command line arguments are ignored.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Cardinal cardinal = new Cardinal();
				cardinal.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				cardinal.setVisible(true);
			}
		});
//		new Cardinal().read();
//		new Cardinal().database();
	}
	
	public void read() {
		Table table = new Table(new File("D:\\db\\MYTABLE.DBF"));
		try {
			table.open();
			System.out.println("W bazie jest " + table.getRecordCount() + " wierszy");
			int value = table.getRecordCount();
			for(int i = 0; i < value; i++) {
				Record record = table.getRecordAt(i);
				String field = record.getStringValue("CHARFLD");
				field = field.trim().toLowerCase();
				System.out.println(field);
				if(field.contains("third")) {
					Map<String, Value> map = new HashMap<>();
					int numberValue = (int) record.getNumberValue("NUMFLD") + 1;
					System.out.println(numberValue);
					map.put("NUMFLD", new NumberValue(numberValue));
					map.put("LOGICFLD", new BooleanValue(record.getBooleanValue("LOGICFLD")));
					map.put("CHARFLD", new StringValue(record.getStringValue("CHARFLD")));
					map.put("MEMOFLD", new StringValue(record.getStringValue("MEMOFLD")));
					map.put("DATEFLD", new DateValue(Calendar.getInstance().getTime()));
					table.updateRecordAt(i, new Record(map));
					break;
				}
			}	
		} catch (CorruptedTableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("B³¹d operacji wejœcia\\wyjœcia");
		} catch (DbfLibException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				System.out.println("B³¹d podczas zamykania bazy");
			}
		}
	}
	
	public void database() {
		Table table = new Table(new File("D:\\db\\ObrotyT.dbf"));
		try {
			table.open();
		} catch (CorruptedTableException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int amount = table.getRecordCount();
		for(int i = 0; i < amount; i++) {
			try {
				Record record = table.getRecordAt(i);
				String name = record.getStringValue("NAZWA");
				name = name.toLowerCase();
				if(name.contains("kalendarz dni siewu")) {
					System.out.println("aaaaaaa");
					double ilosc = (double) record.getNumberValue("ILOSC") + 2;
					double ilosc_rz = (double) record.getNumberValue("ILOSC_RZ") + 2;
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
						System.out.println(number);
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
						System.out.println(string);
						map.put("JM", new StringValue(record.getStringValue("JM")));
					}
					
					map.put("STAWKAVAT", new NumberValue(record.getNumberValue("STAWKAVAT")));
					map.put("STAWKA_RZ", new NumberValue(record.getNumberValue("STAWKA_RZ")));
					map.put("NAZWA", new StringValue(record.getStringValue("NAZWA")));
					
					byte[] tab = record.getRawValue(new Field("OPIS", nl.knaw.dans.common.dbflib.Type.MEMO));
					if(tab == null) {
						System.out.println("no \"OPIS\"");
						map.put("OPIS", new StringValue(""));
					} else {
						System.out.println(new String(tab));
						map.put("OPIS", new StringValue(record.getStringValue("OPIS")));
					}
					
					table.updateRecordAt(i, new Record(map));
					System.out.println(i);
				}
			} catch(CorruptedTableException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(this, "Table is corrupted.", "Error message", JOptionPane.ERROR_MESSAGE);
			} catch(IOException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(this, "Table operation error.", "Error message", JOptionPane.ERROR_MESSAGE);
			} catch(DbfLibException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(this, "Library error.", "Error message", JOptionPane.ERROR_MESSAGE);
			}
		}	
		try {
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This subclass in calling whilst user manipulate window, extends from class <code>WindowAdapter</code>. It override only method <code>windowClosing</code> that
	 * is calling during closing window by user. Here this method ask user for confirmation and if answer is positive call method <code>savePreferences()</code> and
	 * close program, otherwise keep execute program.
	 */
	private class WindowListen extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent event) {
			// TODO Auto-generated method stub
			int state = JOptionPane.showConfirmDialog(Cardinal.this, "Are you sure?", "Question message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(state == JOptionPane.YES_OPTION) {
				Cardinal.this.savaPreferences();
				System.exit(0);
			}
		}
	}
	
	/**
	 * This subclass is in order to select file contains database and save it to class field <code>database</code>.
	 */
	private class OpenAction extends AbstractAction {
		public OpenAction() {
			this.putValue(Action.NAME, "Open");
			this.putValue(Action.SMALL_ICON, new ImageIcon("Open.png"));
			this.putValue(Action.SHORT_DESCRIPTION, "Open database");
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(KeyEvent.VK_O));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("DBF files", "dbf");
			fileChooser.setFileFilter(filter);
			fileChooser.setFileView(new FileView() {
				@Override
				public Icon getIcon(File file) {
					// TODO Auto-generated method stub
					if(!file.isDirectory()) {
						return new ImageIcon("DbfFileIcon.png");
					} else {
						return null;
					}
				}
			});
			int answer = fileChooser.showOpenDialog(Cardinal.this);
			if(answer == JFileChooser.APPROVE_OPTION) {
				String pathOfBase = fileChooser.getSelectedFile().getAbsolutePath();
				Cardinal.this.database = new File(pathOfBase);
			}
		}
	}
	
	/**
	 * This subclass change value of class field <code>database</code> on <code>null</code> value;
	 */
	private class CloseAction extends AbstractAction {
		public CloseAction() {
			this.putValue(Action.NAME, "Close");
			this.putValue(Action.SMALL_ICON, new ImageIcon("Close.png"));
			this.putValue(Action.SHORT_DESCRIPTION, "Close database");
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(KeyEvent.VK_C));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Cardinal.this.database = null;
		}
	}
	
	/**
	 * This subclass is a bit complicated. If database is not null user input code from reader or keyboard. If database contains inputed code then user may input amount
	 * of selected record, else user can add new code using this code or update exist code with this code.
	 */
	private class AddAction extends AbstractAction {
		public AddAction() {
			this.putValue(Action.NAME, "Add");
			this.putValue(Action.SMALL_ICON, new ImageIcon("Add.png"));
			this.putValue(Action.SHORT_DESCRIPTION, "Add data to database");
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(KeyEvent.VK_A));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			if(database != null) {
				String barcode = JOptionPane.showInputDialog(Cardinal.this, "Scan code.", "Input message", JOptionPane.QUESTION_MESSAGE);
				if(barcode != null && !barcode.equals("")) {
					barcode = barcode.trim();
					DatabaseOperations operations = new DatabaseOperations(database, Cardinal.this);
					operations.open();
					if(operations.isContains(barcode)) {
						String string = JOptionPane.showInputDialog(Cardinal.this, "Input amount.", "Input message", JOptionPane.QUESTION_MESSAGE);
						if(string == null) {
							operations.close();
							return;
						}
						try {
							int quantity = Integer.parseInt(string);
							int position = operations.getPosition();
							operations.update(position, quantity);
						} catch(NumberFormatException e) {
							JOptionPane.showMessageDialog(Cardinal.this, "You can input here only numbers.", "Warning message", JOptionPane.WARNING_MESSAGE);
						}
					} else {
						int answer = JOptionPane.showOptionDialog(Cardinal.this, "Database do not contains this code, are you want add new record or update exist record using this code?",
							"Question message", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Add new", "Update exist", "Cancel"}, "Add new");
						try {
							Long.parseLong(barcode);
							if(barcode.length() != 13) {
								JOptionPane.showMessageDialog(Cardinal.this, "It is not barcode.", "Warning message", JOptionPane.WARNING_MESSAGE);
								return;
							}
						} catch(NumberFormatException e) {
							JOptionPane.showMessageDialog(Cardinal.this, "Barcode might contains only numbers.", "Warning message", JOptionPane.WARNING_MESSAGE);
							return;
						}
						if(answer == JOptionPane.YES_OPTION) {
							if(addRecord == null) {
								addRecord = new AddRecord();
							}
							if(addRecord.ok) {
								addRecord.clearFields();
							}		
							if(barcode != null) {
								addRecord.putBarCode(Long.parseLong(barcode));
							}
							if(addRecord.showDialog(Cardinal.this)) {
								if(addRecord.ok) {
									Record record = addRecord.getInputRecord();
									operations.add(record);
								}
							}
						} else if(answer == JOptionPane.NO_OPTION) {
							if(updateRecord == null) {
								updateRecord = new UpdateRecord(operations.getColumnNames(), operations.getCellNames());
							}
							if(updateRecord.showDialog(Cardinal.this, false)) {
								int position = updateRecord.getSelectedRow();
								operations.update(position, barcode);
							}
						}
					}
					operations.close();
				}
			} else {
				JOptionPane.showMessageDialog(Cardinal.this, "First select localization of database", "Warning message", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	/**
	 * This subclass add new record to database. If user inputed correct code, than can add new record using inputed code, else if user do not want use code than can add new record
	 * do not use code. Program additionally checks correct of added data.
	 */
	private class NewAction extends AbstractAction {
		public NewAction() {
			this.putValue(Action.NAME, "New");
			this.putValue(Action.SMALL_ICON, new ImageIcon("New.png"));
			this.putValue(Action.SHORT_DESCRIPTION, "Add new record to database");
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(KeyEvent.VK_N));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			if(database != null) {
				String barcode = null;
				if(addRecord == null) {
					addRecord = new AddRecord();
					barcode = JOptionPane.showInputDialog(Cardinal.this, "Scan code.", "Input message", JOptionPane.QUESTION_MESSAGE);
				}
				if(addRecord.ok) {
					addRecord.clearFields();
					barcode = JOptionPane.showInputDialog(Cardinal.this, "Scan code.", "Input message", JOptionPane.QUESTION_MESSAGE);
				}
				if(barcode != null) {
					while(true) {
						if(barcode == null) {
							barcode = JOptionPane.showInputDialog(Cardinal.this, "Scan code.", "Input message", JOptionPane.QUESTION_MESSAGE);
							if(barcode == null) {
								break;
							}
						}
						barcode = barcode.trim();
						try {
							Long.parseLong(barcode);
							if(barcode.length() != 13) {
								int answer = JOptionPane.showConfirmDialog(Cardinal.this, "It is not barcode, do you want try again?", "Question message",
									JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
								barcode = null;
								if(answer == JOptionPane.YES_OPTION) {
									continue;
								} else {
									addRecord.ok = true;
									return;
								}
							}
						} catch(NumberFormatException e) {
							int answer = JOptionPane.showConfirmDialog(Cardinal.this, "You can input here only numbers, do you want try again?", "Question message",
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
							barcode = null;
							if(answer == JOptionPane.YES_OPTION) {
								continue;
							} else {
								addRecord.ok = true;
								return;
							}
						}
						DatabaseOperations operations = new DatabaseOperations(database, Cardinal.this);
						operations.open();
						if(operations.isContains(barcode)) {
							int answer = JOptionPane.showConfirmDialog(Cardinal.this, "Database contains this code, do you want try again with another?", "Question message",
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
							operations.close();
							barcode = null;
							if(answer == JOptionPane.YES_OPTION) {
								continue;
							} else {
								addRecord.ok = true;
								operations.close();
								return;
							}
						}
						operations.close();
						break;
					}
				}
				if(addRecord.showDialog(Cardinal.this)) {
					if(barcode != null) {
						addRecord.putBarCode(Long.parseLong(barcode));
					}
					if(addRecord.ok) {
						DatabaseOperations operations = new DatabaseOperations(database, Cardinal.this);
						operations.open();
						Record record = addRecord.getInputRecord();
						operations.add(record);
						operations.close();
					}
				}
			} else {
				JOptionPane.showMessageDialog(Cardinal.this, "First select localization of database", "Warning message", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	/**
	 * This subclass remove selected record from database.
	 */
	private class RemoveAction extends AbstractAction {
		public RemoveAction() {
			this.putValue(Action.NAME, "Remove");
			this.putValue(Action.SMALL_ICON, new ImageIcon("Remove.png"));
			this.putValue(Action.SHORT_DESCRIPTION, "Remove data from database");
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(KeyEvent.VK_R));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if(database != null) {
				DatabaseOperations operations = new DatabaseOperations(database, Cardinal.this);
				operations.open();
				if(updateRecord == null) {
					updateRecord = new UpdateRecord(operations.getColumnNames(), operations.getCellNames());
				}
				
				if(updateRecord.showDialog(Cardinal.this, false)) {
					int position = updateRecord.getSelectedRow();
					operations.remove(position);
				}
				operations.close();
			} else {
				JOptionPane.showMessageDialog(Cardinal.this, "First select localization of database", "Warning message", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	/**
	 * This subclass shows table with all records.
	 */
	private class ShowAction extends AbstractAction {
		public ShowAction() {
			this.putValue(Action.NAME, "Show");
			this.putValue(Action.SMALL_ICON, new ImageIcon("Show.png"));
			this.putValue(Action.SHORT_DESCRIPTION, "Show data in database");
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(KeyEvent.VK_S));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(database != null) {
				DatabaseOperations operations = new DatabaseOperations(database, Cardinal.this);
				operations.open();
				if(updateRecord == null) {
					updateRecord = new UpdateRecord(operations.getColumnNames(), operations.getCellNames());
				}
				updateRecord.showDialog(Cardinal.this, true);
				operations.close();
			} else {
				JOptionPane.showMessageDialog(Cardinal.this, "First select localization of database", "Warning message", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
		
	/**
	 * This subclass represents action which exit program
	 */
	private class ExitAction extends AbstractAction {
		public ExitAction() {
			this.putValue(Action.NAME, "Exit");
			this.putValue(Action.SMALL_ICON, new ImageIcon("Exit.png"));
			this.putValue(Action.SHORT_DESCRIPTION, "Exit program");
			this.putValue(Action.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(KeyEvent.VK_E));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			Cardinal.this.savaPreferences();
			System.exit(0);
		}
	}
}
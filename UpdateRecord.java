package pl.java.read;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class UpdateRecord extends JPanel {
	/**
	 * Class that shows this class as window.
	 */
	private JDialog dialog;
	/**
	 * Table in which are show all records from database.
	 */
	private JTable table;
	/**
	 * If user push this button selected row is return by this class or only this window is hide, actions depends on parameters invoke to constructor.
	 */
	private JButton accept;
	/**
	 * The names of columns that is shows in <code>JTable</code>.
	 */
	private String[] columns;
	/**
	 * All records which contain database.
	 */
	private Object[][] cells;
	/**
	 * Number of row that is selected by user, if user do not select any row value amount to -1, this variable is return by method <code>getSelectedRow</code>.
	 */
	private int indexOfRow = -1;
	/**
	 * Variable that becomes <code>true</code> if user set row and press OK button, or it remains <code>false</code> if user do not selected row.
	 */
	protected boolean ok;
	/**
	 * That <code>boolean</code> value specify action that will be do after press by user OK button. If value amount to true dialog is absolutely closed after 
	 * press OK button in otherwise first is check do user selected any row.
	 */
	protected boolean show;
	
	/**
	 * From this constructor are initialize some class fields and call methods that create graphic view of this class.
	 * @param columns table of columns names
	 * @param cells table of all cells
	 */
	public UpdateRecord(String[] columns, Object[][] cells) {
		this.columns = columns;
		this.cells = cells;
		this.initializePanel();
	}
	
	/**
	 * This method call method that make and fill table and add buttons.
	 */
	private void initializePanel() {
		this.setLayout(new BorderLayout());
		this.addTablePanel();
		JPanel panel = new JPanel(new FlowLayout());
		accept = new JButton("OK");
		accept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(show) {
					dialog.setVisible(false);
				} else {
					indexOfRow = table.getSelectedRow();
					if(indexOfRow != -1) {
						ok = true;
						dialog.setVisible(false);
					} else {
						JOptionPane.showMessageDialog(UpdateRecord.this, "Do not selected any record.", "Warning message", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		});
		panel.add(accept);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dialog.setVisible(false);
			}
		});
		panel.add(cancel);
		this.add(panel, BorderLayout.SOUTH);
	}
	
	/**
	 * Create table using class <code>AbstractTableModel</code>, and add table to main panel. 
	 */
	public void addTablePanel() {
		TableModel model = new DBFTableModel(cells, columns);
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane);
	}
	
	/**
	 * This method get row which selected user.
	 * @return number of row that user select of -1 value if never record is selected.
	 */
	public int getSelectedRow() {
		return indexOfRow;
	}
	
	/**
	 * Add graphic view to <code>JDialog</code> and show it.
	 * @param parent the component that call this method
	 * @param show if value is <code>true</code> class only shows records, if value is <code>false</code> class besides showing window return selected record else.
	 * @return <code>true</code> if selected row, <code>false</code> in otherwise
	 */
	public boolean showDialog(Component parent, boolean show) {
		ok = false;
		this.show = show;
		Frame owner = null;
		if(parent instanceof Frame) {
			owner = (Frame) parent;
		} else {
			owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
		}
		if(dialog == null || dialog.getOwner() != parent) {
			dialog = new JDialog(owner, "Choose and update record", true);
			dialog.add(this);
			dialog.getRootPane().setDefaultButton(accept);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Insets bounds = Toolkit.getDefaultToolkit().getScreenInsets(dialog.getGraphicsConfiguration());
			double width = screenSize.getWidth() - bounds.left - bounds.right;
			double height = screenSize.getHeight() - bounds.top - bounds.bottom;
			screenSize.setSize(width, height);
			dialog.setSize(screenSize);
		}
		dialog.setVisible(true);
		return ok;
	}
	
	/**
	 * Subclass that is use to create table.
	 */
	private class DBFTableModel extends AbstractTableModel {
		/**
		 * All records which contain database.
		 */
		private Object[][] cells;
		/**
		 * The names of columns that is shows in <code>JTable</code>.
		 */
		private String[] columns;
		/**
		 * Number of columns.
		 */
		private int columnCount;
		/**
		 * Number of rows.
		 */
		private int rowCount;
		
		/**
		 * Constructor of subclass that set all class fields.
		 * @param cells all records which contain database
		 * @param columns the names of columns that is shows in <code>JTable</code>
		 */
		public DBFTableModel(Object[][] cells, String[] columns) {
			columnCount = columns.length;
			rowCount = cells.length;
			this.cells = cells;
			this.columns = columns;
		}

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return columnCount;
		}

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return rowCount;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			return cells[rowIndex][columnIndex];
		}
		
		@Override
		public String getColumnName(int column) {
			// TODO Auto-generated method stub
			return columns[column];
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}

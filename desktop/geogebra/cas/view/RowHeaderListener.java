package geogebra.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.util.debug.Log;
import geogebra.main.AppD;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Handles mouse and key events in row headers of the CAS table
 *
 */
public class RowHeaderListener extends MouseAdapter implements KeyListener, ListSelectionListener, MouseMotionListener {

	private final CASTableD table;
	private final JList rowHeader;
	private int mousePressedRow;
	private boolean rightClick;

	/**
	 * @param table CAS table
	 * @param rowHeader row headers
	 */
	public RowHeaderListener(CASTableD table, JList rowHeader) {
		this.table = table;
		this.rowHeader = rowHeader;
	}

	
	@Override
	public void mousePressed(MouseEvent e) {
		rightClick = AppD.isRightClick(e);
		table.stopEditing();
		mousePressedRow = rowHeader.locationToIndex(e.getPoint());
		table.setClickedRow(mousePressedRow);
		rowHeader.requestFocus();
	}

	public void mouseDragged(MouseEvent e) {
		e.consume();

		// update selection
		int mouseDraggedRow = rowHeader.locationToIndex(e.getPoint());

		// make sure mouse pressed is initialized, this may not be the case
		// after closing the popup menu
		if (mousePressedRow < 0) {
			table.stopEditing();
			mousePressedRow = mouseDraggedRow;
		}
		if(AppD.isControlDown(e))
			rowHeader.addSelectionInterval(mousePressedRow, mouseDraggedRow);
		else
			rowHeader.setSelectionInterval(mousePressedRow, mouseDraggedRow);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		e.consume();

		// handle marble click
		int releasedRow = table.rowAtPoint(e.getPoint());
		
		try {
			RowHeaderRenderer rhr = (RowHeaderRenderer) rowHeader
					.getCellRenderer().getListCellRendererComponent(rowHeader,
							(releasedRow + 1) + "", releasedRow, false, false);
			boolean marbleVisible = rhr.getComponent(1).isVisible();
			if (releasedRow == mousePressedRow && marbleVisible && !rightClick) {
				int totalHeight = 0;
				for (int i = 0; i < releasedRow; i++) {
					totalHeight += table.getRowHeight(i);
				}
				// not using the renderer to get the marble top because
				// sometimes it gives wrong? values
				// see Ticket #3439, comments 8, 12
				int marbleTop = table.getRowHeight(releasedRow) / 2 + 4;
				if (e.getY() > marbleTop + totalHeight - 4
						&& e.getY() < marbleTop + totalHeight + 16) {
					GeoCasCell clickedCell = table.getGeoCasCell(table
							.rowAtPoint(e.getPoint()));
					if (table.isEditing()) {
						table.stopEditing();
					}
					clickedCell.toggleTwinGeoEuclidianVisible();
				}
			}
		} catch (IndexOutOfBoundsException ex) {
			// this can come, if one clicked on an empty header
			Log.warn("No cas cell " + releasedRow);
		}
		
		mousePressedRow = -1;
		// handle right click
		

		if (rightClick) {
			if(!rowHeader.isSelectedIndex(releasedRow)){
				rowHeader.setSelectedIndex(releasedRow);
			}
			if(rowHeader.getSelectedIndices().length>0){
				RowHeaderPopupMenu popupMenu = new RowHeaderPopupMenu(rowHeader, table);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//not needed, we handle mouse events in mouseReleased
	}

	public void mouseMoved(MouseEvent e) {
		e.consume();
	}

	public void keyPressed(KeyEvent e) {
		boolean undoNeeded = false;

		switch (e.getKeyCode()) {
		case KeyEvent.VK_DELETE:
		case KeyEvent.VK_BACK_SPACE:
			int[] selRows = rowHeader.getSelectedIndices();
			undoNeeded = table.getCASView().deleteCasCells(selRows);
			if (selRows != null && selRows.length > 0) {
				int row = selRows[0];
				rowHeader.setSelectedIndex(row);
			}
			break;
		}

		if (undoNeeded) {
			// store undo info
			table.getApplication().storeUndoInfo();
		}
		
	}

	public void keyReleased(KeyEvent e) {
		//not needed, we handle key events in keyPressed
	}

	public void keyTyped(KeyEvent e) {
		//not needed, we handle key events in keyPressed
	}


	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		int minIndex = lsm.getMinSelectionIndex();
        int maxIndex = lsm.getMaxSelectionIndex();
        if (minIndex == maxIndex)
        	table.startEditingRow(minIndex);
	}
}

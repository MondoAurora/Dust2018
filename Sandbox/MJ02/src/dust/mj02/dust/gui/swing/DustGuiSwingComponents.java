package dust.mj02.dust.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.table.AbstractTableModel;

import dust.mj02.dust.gui.DustGuiComponents;
import dust.utils.DustUtilsJava;
import dust.utils.DustUtilsSwingComponents;

public interface DustGuiSwingComponents extends DustGuiComponents, DustUtilsSwingComponents {
	Dimension ANCHOR_SIZE = new Dimension(16, 16);

	Color COL_ENTITY_HEAD_NORM = Color.LIGHT_GRAY;
	Color COL_ENTITY_HEAD_SEL = Color.YELLOW;

	int ENTITY_PANEL_BORDER = 10;

	int HR = 6;

	enum AnchorType {
		EntityHead, MessageHead, PrimaryModel, Model, Link;

		final ImageIcon icon;

		private AnchorType() {
			icon = new ImageIcon("images/" + name() + ".png");
		}
	}

	enum AnchorLocation {
		Left(BorderLayout.WEST), Right(BorderLayout.EAST);

		final String swingConst;

		private AnchorLocation(String swingConst) {
			this.swingConst = swingConst;
		}

		public String getSwingConst() {
			return swingConst;
		}
	}

	class EntityRendererDefault extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel tc = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			tc.setText(DustUtilsJava.toString(value));
			return tc;
		}
	}

	class EntityListModelDefault extends AbstractListModel<DustEntity> {
		private static final long serialVersionUID = 1L;

		ArrayList<DustEntity> data;

		public EntityListModelDefault(ArrayList<DustEntity> data) {
			super();
			this.data = data;
		}

		@Override
		public DustEntity getElementAt(int index) {
			return data.get(index);
		}

		@Override
		public int getSize() {
			return data.size();
		}

		public void update() {
			fireContentsChanged(this, 0, getSize());
		}
	}

	abstract class EntityTableModelBase extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		protected final Object[] columns;
		protected final ArrayList<DustEntity> data;

		public EntityTableModelBase(ArrayList<DustEntity> data, Object[] cols) {
			this.columns = cols;
			this.data = data;
		}

		public void update() {
			fireTableDataChanged();
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@Override
		public int getRowCount() {
			return data.size();
		}
	}

}

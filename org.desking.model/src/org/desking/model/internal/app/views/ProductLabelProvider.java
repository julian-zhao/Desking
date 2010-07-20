package org.desking.model.internal.app.views;

import org.desking.model.client.Product;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ProductLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Product product = (Product) element;
		switch (columnIndex) {
		case 0:
			return product.getId();
		case 1:
			return product.getName();
		case 2:
			return product.getDescription();
		default:
			throw new RuntimeException("Should not happen");
		}
	}

}

package org.desking.model.internal.app.views;

import java.util.List;

import org.desking.model.client.Product;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ProductContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		List<Product> products = (List<Product>) inputElement;
		return products.toArray();
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}



}

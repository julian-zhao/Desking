package org.desking.model.internal.app.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.desking.model.IModel;
import org.desking.model.ModelException;
import org.desking.model.ModelService;
import org.desking.model.client.Product;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class ProductEditor extends EditorPart {

	private Product product;
	private Text id;
	private Text name;
	private Text desc;
	private boolean dirty;
	
	private PropertyChangeListener changeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setDirty(true);
		}
		
	};
	
	public ProductEditor() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		product = ((ProductEditorInput) input).getProduct();

		product.addPropertyChangeListener(changeListener);
		
	}
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		Label label = new Label(parent, SWT.NONE);
		label.setText("ID:");
		
		id = new Text(parent, SWT.BORDER);
		id.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		label = new Label(parent, SWT.NONE);
		label.setText("Name:");
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		label = new Label(parent, SWT.NONE);
		label.setText("Description:");
		
		desc = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		desc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		
		DataBindingContext bindingContext = new DataBindingContext();

		IObservableValue uiElement;
		IObservableValue modelElement;
		// Lets bind it
		uiElement = SWTObservables.observeText(id, SWT.Modify);
		modelElement = BeansObservables.observeValue(product, "id");
		// The bindValue method call binds the text element with the model
		bindingContext.bindValue(uiElement, modelElement, null, null);

		uiElement = SWTObservables.observeText(name, SWT.Modify);
		modelElement = BeansObservables.observeValue(product, "name");
		// Remember the binding so that we can listen to validator problems
		// See below for usage
		bindingContext.bindValue(uiElement, modelElement, null, null);
		
		uiElement = SWTObservables.observeText(desc, SWT.Modify);
		modelElement = BeansObservables.observeValue(product, "description");
		// Remember the binding so that we can listen to validator problems
		// See below for usage
		bindingContext.bindValue(uiElement, modelElement, null, null);
	}
	
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		IModel<Product> model = null;
		
		try {
			model = ModelService.getInstance().getModel(Product.class);
			model.update(product);
			setDirty(false);
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {

	}

	private void setDirty(boolean dirty) {
		if (this.dirty != dirty) {
			this.dirty = dirty;
			this.firePropertyChange(PROP_DIRTY);
		}
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		product.removePropertyChangeListener(changeListener);
		super.dispose();
	}

}

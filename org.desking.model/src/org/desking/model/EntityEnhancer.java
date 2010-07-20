package org.desking.model;

import org.objectweb.asm.Type;

import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Enhancer;

public class EntityEnhancer extends Enhancer {

	private static final String ENTITY_DATA_FIELD = "$ENTITYDATA$";
	private static final Type ENTITY_DATA = Type.getType(IEntityData.class);
	private static final Signature GET_ENTITY_DATA =
	      new Signature("getEntityData", ENTITY_DATA, new Type[0]);
	private static final Signature SET_ENTITY_DATA =
	      new Signature("setEntityData", Type.VOID_TYPE, new Type[] { ENTITY_DATA });
	
	public EntityEnhancer() {
		super();
		setCustomInterface(IEntity.class);
	}
	
	@Override
	protected void generateCustomInterface(ClassEmitter ce) {
		ce.declare_field(Constants.ACC_PRIVATE, ENTITY_DATA_FIELD, ENTITY_DATA, null);
		
		emitGetEntityData(ce);
		emitSetEntityData(ce);
	}

	private void emitSetEntityData(ClassEmitter ce) {
		CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, SET_ENTITY_DATA, null);
		e.load_this();
        e.load_arg(0);
		e.putfield(ENTITY_DATA_FIELD);
		e.return_value();
        e.end_method();
	}

	private void emitGetEntityData(ClassEmitter ce) {
		CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, GET_ENTITY_DATA, null);
		e.load_this();
		e.getfield(ENTITY_DATA_FIELD);
		e.return_value();
        e.end_method();
	}



}

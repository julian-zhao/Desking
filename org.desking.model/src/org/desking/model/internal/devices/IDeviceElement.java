package org.desking.model.internal.devices;

import java.util.Comparator;

public interface IDeviceElement {
    public static final Comparator<Object> NAME_COMPARATOR = new Comparator<Object>() {

        public int compare(Object arg1, Object arg2) {
            String str1 = getCompareString(arg1);
            String str2 = getCompareString(arg2);
            return str1.compareTo(str2);
        }
        
        private String getCompareString(Object object) {
            if (object instanceof String) {
				return (String) object;
			} else if (object instanceof IDeviceElement) {
				return ((IDeviceElement) object).getName();
			}
            return ""; //$NON-NLS-1$
        }
    };
    
    
	public String getName();
}

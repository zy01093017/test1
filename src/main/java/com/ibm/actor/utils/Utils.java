package com.ibm.actor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;



/**
 * Assorted utility functions (sort of a kitchen sink). 
 * Some methods used but core to Actor implementation.
 * Additional unused methods may be included and should be ignored.  
 * 
 * @author bfeigenb
 * 
 */
public class Utils {
    
    public static final Logger logger = LoggerFactory.getLogger(Utils.class);
    
    /** Test if array is null or empty. */
	public static boolean isEmpty(Object[] oa) {
		return oa == null || oa.length == 0;
	}

	/** Test if string is null or empty. */
	public static boolean isEmpty(CharSequence s) {
		return s == null || s.length() == 0;
	}
    
    /** Test if map is null or empty. */
	public static boolean isEmpty(Map<?, ?> m) {
		return m == null || m.size() == 0;
	}

	/** Test if collection is null or empty. */
	public static boolean isEmpty(Collection<?> c) {
		return c == null || c.size() == 0;
	}

	/** Truncate a text string to 100 characters. */
	public static String truncate(Object s) {
		return s != null ? truncate(s.toString(), 100) : "null";
	}

	/** Remove repeated whitespace (including newlines) */
	public static String removeMultipleSpaces(String s) {
		return removeMultipleSpaces(s, ' ');
	}

	/** Remove repeated whitespace (including newlines) */
	public static String removeMultipleSpaces(String s, char nlReplace) {
		if (!isEmpty(s)) {
			// s = s.replace('\n', nlReplace);
			s = s.replaceAll("\\s\\s+", " ");
		}
		return s;
	}

	/** Truncate a text string to size characters. */
	public static String truncate(String s, int size) {
		if (!isEmpty(s)) {
			s = removeMultipleSpaces(s);
			if (s.length() > size) {
				int leadLength = size / 2;
				int tailLength = size / 2;
				s = s.substring(0, leadLength) + " ... "
						+ s.substring(s.length() - tailLength);
			}
		}
		return s;
	}
    
    @Retention(RetentionPolicy.RUNTIME)
	public @interface ForToString {
		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ForEquals {
		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ForCompareTo {
		String value() default "";
	}

	@Override
	public String toString() {
		return toString(this);
	}

	/** Reflectively implement toString() using ForToString annotations. */
	public String toString(Object target) {
		Map<String, Object> xfields = getMineAndParentFields(ForToString.class,
				target);
		return formatToString(xfields, target);
	}

	public static boolean useShortClassName = true;

	protected String formatToString(Map<String, Object> xfields, Object target) {
		StringBuilder sb = new StringBuilder();
		String cname = useShortClassName ? target.getClass().getSimpleName()
				: target.getClass().getName();
		if (isEmpty(cname)) {
			cname = target.getClass().getName();
		}
		sb.append(cname);
		sb.append('[');
		if (!isEmpty(xfields)) {
			for (String key : xfields.keySet()) {
				if (sb.charAt(sb.length() - 1) != '[') {
					sb.append(',');
				}
				Object v = xfields.get(key);
				sb.append(key);
				sb.append('=');
				if (v != null) {
					if (v.getClass().isArray()) {
						Object[] va = new Object[] { v };
						v = Arrays.deepToString(va);
					}
				}
				sb.append(v);
			}
		}
		sb.append(']');
		sb.append('@');
		sb.append(Integer.toHexString(System.identityHashCode(this))
				.toUpperCase());
		return sb.toString();
	}

	@Override
	/** Reflectively implement equals() using ForEquals annotations. */
	public boolean equals(Object that) {
		boolean res = that != null;
		if (res) {
			res = this.getClass().isAssignableFrom(that.getClass());
		}
		if (res) {
			res = testEquals(that);
		}
		return res;
	}

	protected boolean testEquals(Object that) {
		boolean res = true;
		Map<String, Object> fields1 = getMineAndParentFields(ForEquals.class,
				this);
		Map<String, Object> fields2 = getMineAndParentFields(ForEquals.class,
				that);
		if (!isEmpty(fields1) && fields2.size() >= fields1.size()) {
			for (String key : fields1.keySet()) {
				Object v1 = fields1.get(key);
				Object v2 = fields2.get(key);
				if (v1 != null) {
					if (v2 != null) {
						res &= v1.equals(v2);
					} else {
						res = false;
					}
				} else {
					if (v2 != null) {
						res = false;
					} else {
						// res &= true;
					}
				}
				if (!res) {
					break;
				}
			}
		} else {
			res = super.equals(that);
		}
		return res;
	}
    
    
    @Override
	/** Reflectively implement hashCode() using ForEquals annotations. */
	public int hashCode() {
		int res = 31;
		Map<String, Object> fields = getMineAndParentFields(ForEquals.class,
				this);
		if (!isEmpty(fields)) {
			for (String key : fields.keySet()) {
				Object v = fields.get(key);
				if (v != null) {
					res ^= v.hashCode();
				}
			}
		} else {
			res = super.hashCode();
		}
		return res;
	}

	protected Map<String, Object> getMineAndParentFields(
			Class<? extends Annotation> targetAnnoation, Object target) {
		Map<String, Object> fields = new TreeMap<String, Object>();
		Class<? extends Object> clazz = target.getClass();
		// getFieldValues(clazz, clazz.getFields(), fields);
		while (clazz != null) {
			getFieldValues(targetAnnoation, clazz, clazz.getDeclaredFields(),
					fields, target);
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	protected void getFieldValues(Class<? extends Annotation> targetAnnoation,
			Class<? extends Object> clazz, Field[] fields,
			Map<String, Object> xfields, Object target) {
		for (Field field : fields) {
			// printf("getFieldValues: class=%s, field=%s%n", clazz, field);
			if (!Modifier.isStatic(field.getModifiers())) {
				getFieldValue(targetAnnoation, xfields, field, target);
			}
		}
	}

	protected void getFieldValue(Class<? extends Annotation> targetAnnoation,
			Map<String, Object> xfields, Field field, Object target) {
		Annotation anno = field.getAnnotation(targetAnnoation);
		if (anno != null) {
			try {
				boolean acc = field.isAccessible();
				try {
					field.setAccessible(true);
					Object o = field.get(target);
					String name = null;
					Method value = anno.annotationType().getMethod("value");
					if (value != null) {
						name = (String) value.invoke(anno);
					}
					if (isEmpty(name)) {
						name = field.getName();
					}
					xfields.put(name, o);
				} finally {
					field.setAccessible(acc);
				}
			} catch (Exception e) {
				e.printStackTrace(); // temp
			}
		}
	}

	/** Safely implement sleep(). */
	public static void sleep(long millis) {
		if (millis >= 0) {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				// e.printStackTrace(System.out);
			}
		}
	}
    
    public static String safeFormat(String format, Object... args) {
		return String.format(format, args);
	}
}

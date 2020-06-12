package com.crivano.jsync;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncUtils {
	private static SynchronizableComparator sc = new SynchronizableComparator();
	private static Logger logger = LoggerFactory.getLogger(SyncUtils.class);

	private static boolean logAndReturnFalse(String log) {
		logger.debug(log);
		return false;
	}

	public static boolean alike(Synchronizable s, Synchronizable obj, int nivel) {
		if (obj == null)
			return false;

		try {
			for (Field fld : SyncUtils.getFieldList(s.getClass())) {
				if (fld.isAnnotationPresent(IgnoreForSimilarity.class)
						|| (fld.isAnnotationPresent(IgnoreForSimilarityIfCondition.class)
								&& validCondition(fld, IgnoreForSimilarityIfCondition.class, s))
						|| (fld.isAnnotationPresent(IgnoreForSimilarityIfDependent.class) && (nivel > 0)))
					continue;

				logger.debug("fld: " + fld.getName());
				Object o1 = fld.get(s);
				Object o2 = fld.get(obj);

				if (o1 == null) {
					if (o2 != null)
						return logAndReturnFalse("o1 is null");
				} else {
					if (o2 == null)
						return logAndReturnFalse("o2 is null");
					if (o1 instanceof Synchronizable) {
						if (((Synchronizable) o1).getSyncKey() == null)
							return logAndReturnFalse("o1 has no key");
						if (!((Synchronizable) o1).getSyncKey().equals(((Synchronizable) o2).getSyncKey()))
							return logAndReturnFalse("o2 key is different");
						if (!((Synchronizable) o1).isSyncSimilar((Synchronizable) o2, nivel + 1))
							return logAndReturnFalse("o1 not alike o2");
					} else if (o1 instanceof Synchronizable) {
						if (!((Synchronizable) o1).isSyncSimilar((Synchronizable) o2, nivel + 1))
							return logAndReturnFalse("o1 not alike o2");
					} else if (o1 instanceof Date) {
						if (!o1.equals(o2)) {
							// Nato: Esse "if" corrige um problema que
							// ocorre na meia noite de um dia de mudança
							// para o horário de verão.
							if (((Date) o2).getTimezoneOffset() - ((Date) o1).getTimezoneOffset() != 60
									|| (((Date) o1).getTime() - ((Date) o2).getTime()) != 3600000)
								return logAndReturnFalse("date o1 != o2");
						}
					} else if (o1 instanceof String) {
						// Nato: Esse "if" corrige um problema que
						// ocorre quando existe um campo no banco de dados
						// que é do tipo CHAR, em vez de VARCHAR2.
						if (!((String) o1).trim().equals(((String) o2).trim())) {
							return logAndReturnFalse("string o1 != o2");
						}
					} else if (o1 instanceof Collection) {
						Collection c1 = (Collection) o1;
						Collection c2 = (Collection) o2;

						if (!collectionAlike(c1, c2, nivel + 1))
							return logAndReturnFalse("collection o1 not alike o2");
					} else {
						if (!o1.equals(o2))
							return logAndReturnFalse("object o1 != o2");
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	private static boolean collectionAlike(Collection c1, Collection c2, int nivel) {
		SortedSet<Synchronizable> set1 = new TreeSet<>(sc);
		SortedSet<Synchronizable> set2 = new TreeSet<>(sc);
		set1.addAll(c1);
		set2.addAll(c2);

		Iterator<Synchronizable> i1 = set1.iterator();
		Iterator<Synchronizable> i2 = set2.iterator();

		Synchronizable o1 = null;
		Synchronizable o2 = null;

		if (i2.hasNext())
			o2 = i2.next();
		if (i1.hasNext())
			o1 = i1.next();
		while (o2 != null || o1 != null) {
			if ((o2 == null) || (o1 != null && sc.compare(o1, o2) != 0)) {
				return false;
			} else {
				if (o2 == null) {
					int i = 0;
				}
				// O registro existe no corp e no xml
				if (!o1.isSyncSimilar(o2, nivel + 1))
					return false;
				if (i1.hasNext())
					o1 = i1.next();
				else
					o1 = null;
				if (i2.hasNext())
					o2 = i2.next();
				else
					o2 = null;
			}
		}
		return true;
	}

	public static int getDependencyLevel(Synchronizable s) {
		int nivel = 0;
		try {
			for (Field fld : SyncUtils.getFieldList(s.getClass())) {
				if (fld.getAnnotation(IgnoreForDependecyLevel.class) != null)
					continue;
				Object o1 = fld.get(s);
				if (o1 != null) {
					if (o1 instanceof Synchronizable) {
						int n = ((Synchronizable) o1).getSyncDependencyLevel();
						if (nivel <= n)
							nivel = n + 1;
					} else if (o1 instanceof Collection<?>) {
						for (Object o2 : (Collection<?>) o1) {
							if (o2 instanceof Synchronizable) {
								int n = ((Synchronizable) o2).getSyncDependencyLevel();
								if (nivel <= n)
									nivel = n + 1;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return nivel;
	}

	private static boolean validCondition(Field fld, Class<IgnoreForSimilarityIfCondition> clazz, Synchronizable obj) {
		String[] condicao = fld.getAnnotation(clazz).condition();
		if (condicao[0].length() == 0) {
			return true;
		}
		try {
			String conditionField = condicao[0];
			Boolean conditionValue = Boolean.valueOf(condicao[1]);

			Field f = fld.getDeclaringClass().getDeclaredField(conditionField);
			f.setAccessible(true);
			return f.getBoolean(obj) == conditionValue;

		} catch (Exception e) {
			return false;
		}

	}

	public static List<Class<?>> getClassHierarchy(Class<?> baseClass) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (Class<?> clazz = baseClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
			classes.add(0, clazz);
		}
		return classes;
	}

	public static List<Field> getFieldList(Class<?> baseClass) {
		List<Field> l = new ArrayList<>();
		for (Class<?> clazz : getClassHierarchy(baseClass)) {
			Field fieldlist[] = clazz.getDeclaredFields();
			for (int i = 0; i < fieldlist.length; i++) {
				Field fld = fieldlist[i];
				if (((fld.getModifiers() & Modifier.STATIC) != 0))
					continue;
				fld.setAccessible(true);
				l.add(fld);
			}
		}
		return l;
	}

}

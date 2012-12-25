package util;

import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class SetFactory {

	public static <E> HashSet<E> newHashSet() {
		return new HashSet<E>();
	}

	public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
		return new CopyOnWriteArraySet<E>();
	}

}

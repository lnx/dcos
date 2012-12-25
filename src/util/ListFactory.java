package util;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListFactory {

	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}

	public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
		return new CopyOnWriteArrayList<E>();
	}

}

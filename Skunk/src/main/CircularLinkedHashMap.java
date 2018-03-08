package main;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CircularLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 567281930410799845L;

	public K getKeyAfter(K key) {
		ArrayList<K> keys = new ArrayList<K>(this.keySet());
		return keys.indexOf(key) == keys.size()-1 ? keys.get(0) : keys.get(keys.indexOf(key) + 1);
	}
}

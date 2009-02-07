package edu.ucla.loni.ccb.itools.view;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class AutoCompleteDocument extends PlainDocument {
	private static final long serialVersionUID = 1L;
	private Collection dictionary = new HashSet();
	// private List dictionary = new LinkedList();
	private JTextComponent comp;

	public AutoCompleteDocument(JTextComponent field) {
		comp = field;
	}

	public AutoCompleteDocument(JTextComponent field, String[] aDictionary) {
		comp = field;
		dictionary.addAll(Arrays.asList(aDictionary));
	}

	public void addDictionaryEntry(String item) {
		dictionary.add(item);
	}

	public void addDictionaryEntry(Collection entries) {
		// dictionary.addAll(entries);
		dictionary = entries;
	}

	public void setDictionary(Collection dictionary) {
		this.dictionary = dictionary;
	}

	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		super.insertString(offs, str, a);

		String text = getText(0, getLength());
		int i = text.lastIndexOf(' ');
		int j = text.lastIndexOf(',');
		if (j > i)
			i = j;
		if (i > 0)
			text = text.substring(i + 1);
		if (text.length() == 0)
			return;

		String word = autoComplete(text);
		if (word != null) {
			super.insertString(offs + str.length(), word, a);
			comp.setCaretPosition(offs + str.length());
			comp.moveCaretPosition(getLength());
		}
	}

	public String autoComplete(String text) {
		int length = text.length();
		for (Iterator i = dictionary.iterator(); i.hasNext();) {
			String word = (String) i.next();
			if (word.length() >= length) {
				String starts = word.substring(0, length);
				if (text.equalsIgnoreCase(starts)) {
					return word.substring(length);
				}
			}
		}
		return null;
	}
}

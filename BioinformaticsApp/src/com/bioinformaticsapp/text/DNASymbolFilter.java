package com.bioinformaticsapp.text;

import android.text.InputFilter;
import android.text.Spanned;

public class DNASymbolFilter implements InputFilter {

	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		// TODO Auto-generated method stub
		char[] symbols = new char[]
				{'A', 'C', 'G', 'T', 'U', 'W', 'S', 'M', 'K', 'R', 'Y', 'B', 'D', 'H', 'V'};
		
		String validDNASymbols = new String(symbols);
		
		if(!source.toString().toUpperCase().matches("["+validDNASymbols+"]+")){
			return "";			
		}
		
		return null;
	}

}

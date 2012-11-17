package com.bioinformaticsapp.io;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import com.bioinformaticsapp.models.BLASTHit;

public interface BLASTHitsParser {
	
	public List<BLASTHit> parse(InputStream file) throws Exception;

}

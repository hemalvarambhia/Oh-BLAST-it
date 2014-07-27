package com.bioinformaticsapp.io;

import java.io.InputStream;
import java.util.List;

import com.bioinformaticsapp.domain.BLASTHit;

public interface BLASTHitsParser {
	
	public List<BLASTHit> parse(InputStream file) throws Exception;

}

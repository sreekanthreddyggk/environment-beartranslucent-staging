package com.fms.carrier.enums;

import java.util.regex.Pattern;

public enum StatusPattern {
	p1(Pattern.compile("(.+) on (.+) Voyage No.(.+)"));
	
	private Pattern pattern;
	
	StatusPattern(Pattern pattern){
		this.pattern = pattern;
	}
	
	public Pattern getPattern(){
		return pattern;
	}
	
}

package com.fms.carrier.enums;

import java.util.regex.Pattern;

public enum StatusPattern {
	p1(Pattern.compile("(.+) on vessel (.+) for (.+) On (.+) which sailed on (.+)")),
	p2(Pattern.compile("(.+) from vessel (.+) at (.+) On (.+)")),
	p3(Pattern.compile("(.+) for vessel (.+) at (.+) On (.+)")),
	p4(Pattern.compile("(.+) from (.+) from vessel (.+) On (.+)")),
	p5(Pattern.compile("(.+) at (.+) On (.+)")),
	p6(Pattern.compile("(.+) from (.+) On (.+)")),
	p7(Pattern.compile("(.+) for (.+) On (.+)"));
	
	private Pattern pattern;
	
	StatusPattern(Pattern pattern){
		this.pattern = pattern;
	}
	
	public Pattern getPattern(){
		return pattern;
	}
	
}

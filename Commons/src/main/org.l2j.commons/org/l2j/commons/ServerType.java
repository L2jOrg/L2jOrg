package org.l2j.commons;

public enum ServerType {
	NORMAL(1),
	RELAX(2),
	RESTRICTED(16),
	EVENT(32),
	FREE(64),
	NEW(512),
	CLASSIC(1024);

    private final int id;

    ServerType(int id) {
	    this.id = id;
    }

	public int getId() {
		return id;
	}
}
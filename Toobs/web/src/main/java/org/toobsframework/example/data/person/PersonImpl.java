package org.toobsframework.example.data.person;

import java.util.Date;

public class PersonImpl {

	private String guid;
	private int versionInt;
	private String username;
	private String password;
	private Date createTs;

	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public int getVersionInt() {
		return versionInt;
	}
	public void setVersionInt(int versionInt) {
		this.versionInt = versionInt;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getCreateTs() {
		return createTs;
	}
	public void setCreateTs(Date createTs) {
		this.createTs = createTs;
	}
}

package com.avlija.furniture.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "persistent_logins")
public class Logins {
	 
	 @Id
	 @Column(name="series")
	 private String series;
	 
	 @Column(name="username")
	 private String username;
	 
	 @Column(name="token")
	 private String token;
	 
	 @Column(name="last_used")
	 private Date lastUsed;
	 
	 public Logins() {
		 
	 }

	public Logins(String series, String username, String token, Date lastUsed) {
		this.series = series;
		this.username = username;
		this.token = token;
		this.lastUsed = lastUsed;
	}

	/**
	 * @return the series
	 */
	public String getSeries() {
		return series;
	}

	/**
	 * @param series the series to set
	 */
	public void setSeries(String series) {
		this.series = series;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the lastUsed
	 */
	public Date getLastUsed() {
		return lastUsed;
	}

	/**
	 * @param lastUsed the lastUsed to set
	 */
	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}
}

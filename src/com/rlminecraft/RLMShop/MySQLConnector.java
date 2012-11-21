package com.rlminecraft.RLMShop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLConnector {
	
	private Connection con;
	private Statement st;
	private ResultSet rs;
	
	private String url;
	private String user;
	private String password;
	
	private boolean connected;
	
	/**
	 * Constructor
	 * @param url the URL of the database
	 * @param user username to login to the database
	 * @param password password to login to the database
	 */
	public MySQLConnector (String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * Executes the given query on the database
	 * @param query
	 * @return whether the query was successful
	 */
	public boolean execute (String query) {
		if (query == null) return false;
		try {
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			Logger console = Logger.getLogger(MySQLConnector.class.getName());
			console.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	/**
	 * Opens a connection to the database
	 * @return true if connection was successful, false otherwise
	 */
	public boolean connect () {
		try {
			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			connected = true;
		} catch (SQLException e) {
			Logger console = Logger.getLogger(MySQLConnector.class.getName());
			console.log(Level.SEVERE, e.getMessage(), e);
			connected = false;
		}
		return connected;
	}
	
	/**
	 * Closes the database connection
	 */
	public void close () {
		try {
			if (rs != null) rs.close();
			if (st != null) st.close();
			if (con != null) con.close();
		} catch (SQLException e) {
			Logger console = Logger.getLogger(MySQLConnector.class.getName());
			console.log(Level.WARNING, e.getMessage(), e);
		} finally {
			this.connected = false;
		}
	}
	
	/**
	 * @return results of the previous query
	 */
	public ResultSet getResults () {
		return this.rs;
	}
	
	/**
	 * @return true if the database connection is active<br>false otherwise
	 */
	public boolean isConnected () {
		return this.connected;
	}
	
}

package ca.bungo.main;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import ca.bungo.events.JoinLeaveEvent;
import net.md_5.bungee.api.ChatColor;

public class Core extends JavaPlugin {
	
	private class Database {
		
		private String host = "localhost";
		private int port = 3306;
		private String database = "";
		private String user = "";
		private String password = "";
		
		public String getHost() {
			return host;
		}


		public int getPort() {
			return port;
		}


		public String getDatabase() {
			return database;
		}


		public String getUser() {
			return user;
		}


		public String getPassword() {
			return password;
		}
		
		public String toString() {
			return host + ":" + port + " | " + database + " | " + user + " | " + password;
		}


		public Database() {
			this.host = getConfig().getString("database.host");
			this.port = getConfig().getInt("database.port");
			this.database = getConfig().getString("database.database");
			this.user = getConfig().getString("database.user");
			this.password = getConfig().getString("database.password");
		}
	}
	
	public static Core instance;
	
	public MysqlDataSource source;
	
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		logConsole("&aCore Systems are loading!");
		logConsole("&3Connecting to MySql Database...");
		source = initMySql();
		
		if(source == null) {
			logConsole("&4Failed to obtain MySql Database!");
			return;
		}else {
			logConsole("&aConnected to MySql Server!");
		}
		
		logConsole("&3Starting API Service...");
		logConsole("&3Registering Events...");
		registerEvents();
	}
	
	@Override
	public void onDisable() {
		logConsole("&4Core Systems shuting down");
	}
	
	public void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new JoinLeaveEvent(), this);
	}
	
	
	public void logConsole(String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	
	private void testDataSource(MysqlDataSource dataSource) throws SQLException {
		try (Connection conn = dataSource.getConnection()) {
	        if (!conn.isValid(4000)) {
	            throw new SQLException("Could not establish database connection.");
	        }
	    }
	}
	
	
	private MysqlDataSource initMySql() {
		
		MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
		
		Database database = new Database();
		
		dataSource.setServerName(database.getHost());
		dataSource.setPortNumber(database.getPort());
		dataSource.setDatabaseName(database.getDatabase());
		dataSource.setUser(database.getUser());
		dataSource.setPassword(database.getPassword());
		
		logConsole(database.toString());
		
		
		try {
			testDataSource(dataSource);
		} catch(SQLException e) {
			logConsole("&4Something Went Wrong...");
			e.printStackTrace();
			return null;
		}
		
		return dataSource;
	}
	
}

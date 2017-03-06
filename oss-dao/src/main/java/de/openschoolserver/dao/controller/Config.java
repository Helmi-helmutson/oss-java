/* (c) 2017 Péter Varkoly <peter@varkoly.de> - all rights reserved */
package de.openschoolserver.dao.controller;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;


public class Config {

	protected static Path OSS_CONFIG = Paths.get("/etc/sysconfig/schoolserver");

	private Map<String,String>   ossConfig;
	private Map<String,String>   ossConfigPath;
	private Map<String,Boolean>  readOnly;
	private List<String>         ossConfigFile;
	
	public Config() {
		this.InitConfig();
	}
	
	public Config(String configPath) {
		OSS_CONFIG = Paths.get(configPath);
		this.InitConfig();
	}
	
	public void InitConfig() {
		ossConfig     = new HashMap<>();
		readOnly      = new HashMap<>();
		ossConfigPath = new HashMap<>();
		try {
			ossConfigFile = Files.readAllLines(OSS_CONFIG);
		}
		catch( IOException e ) { 
			e.printStackTrace();
		}
		Boolean ro = false;
		String  path = "Backup";
		for ( String line : ossConfigFile ){
			if( line.startsWith("#") && line.contains("readonly")) {
				ro = true;
			}
			if( line.startsWith("## Path:")) {
				String[] l = line.split("\\/");
				if( l.length == 3 )
				  path = l[2];
			}
			if( !line.startsWith("#") ) {
				String[] sline = line.split("=", 2);
				if( sline.length == 2 )
				{
					String value = sline[1];
					if( value.startsWith("\"") || value.startsWith("'") ){
						value = value.substring(1);
					}
					if( value.endsWith("\"") || value.endsWith("'") ){
						value = value.substring(0,value.length()-1);
					}
					readOnly.put(sline[0], ro);
					ossConfig.put(sline[0], value);
					ossConfigPath.put(sline[0],path);
					ro = false;
					// System.out.println(sline[0] + "=>" + value);
				}
			}
		}
	}

	public Boolean isConfgiReadOnly(String key){
		return readOnly.get(key);
	}
	
	public String getConfigValue(String key){
		return ossConfig.get(key);
	}
	
	public String getConfigPath(String key){
		return ossConfigPath.get(key);
	}
	
	public List<String> getConfigPaths() {
 		List<String> paths = new ArrayList<String>();
		for ( String path : ossConfigPath.values() )
		{
		   if(!paths.contains(path))
			   paths.add(path);
		}
		return paths;
	}
	
	public List<String> getKeysOfPath(String path) {
		List<String> keys = new ArrayList<String>();
		for ( String key : ossConfigPath.keySet() ) {
			if( ossConfigPath.get(key).startsWith(path) )
			  keys.add(key);
		}
		System.out.println(path);
		Collections.sort(keys);
		return keys;
	}
	
	public void setConfigValue(String key, String value){
		if(readOnly.get(key)){
			return;
		}
		ossConfig.put(key, value);
		List<String> tmpConfig =  new ArrayList<String>();
		for ( String line : ossConfigFile ){
			if(line.startsWith(key)){
				tmpConfig.add( key + "=\"" + value + "\"" );  
			}
			else{
				tmpConfig.add( line );
			}
		}
		ossConfigFile = tmpConfig;
		try {
			Files.write(OSS_CONFIG, tmpConfig );
		}
		catch( IOException e ) { 
			e.printStackTrace();
		}
	}

/*
*
	public static void main(String[] args) {
		Config c = new Config();
		c.Set("SCHOOL_REG_CODE", "BLA_BALÖ");
		System.out.println(c.GetPaths());
		System.out.println(c.GetKeysOfPath("Backup"));
	}
*
*/
}
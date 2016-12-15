package de.openschoolserver.dao.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

	static Path OSS_CONFIG = Paths.get("/etc/sysconfig/schoolserver");

	private Map<String,String>  ossConfig;
	private Map<String,Boolean> readOnly;
	private List<String>        ossConfigFile;
	
	public Config() {
		ossConfig = new HashMap<>();
		readOnly  = new HashMap<>();
		try {
			ossConfigFile = Files.readAllLines(OSS_CONFIG);
		}
		catch( IOException e ) { 
			e.printStackTrace();
		}
		Boolean ro = false;
		for ( String line : ossConfigFile ){
			if( line.startsWith("#") && line.contains("readonly")) {
				ro = true;
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
					ro = false;
					System.out.println(sline[0] + "=>" + value);
				}
			}
		}
	}
	
	public Boolean IsReadOnly(String key){
		return readOnly.get(key);
	}
	
	public String Get(String key){
		return ossConfig.get(key);
	}
	
	public void Set(String key, String value){
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
}

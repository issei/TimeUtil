package br.com.issei.time.entity;


import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class Data {
	
	private String data;
	private String dataPattern;
	
	public LocalDate  getData()
	{
		return DateTimeFormat.forPattern(this.dataPattern).parseLocalDate(this.data);
	}

	public String getDataPattern() {
		return dataPattern;
	}

	public void setDataPattern(String dataPattern) {
		this.dataPattern = dataPattern;
	}

	public void setData(String data) {
		this.data = data;
	}

}

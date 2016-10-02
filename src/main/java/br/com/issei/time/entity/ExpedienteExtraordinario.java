package br.com.issei.time.entity;


import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

public class ExpedienteExtraordinario {
	
	private String data;
	private String dataPattern;
	private String horaInicioExpediente;
	private String horaFimExpediente;
	private String timePattern;
	
	public LocalDate getData()
	{
		return DateTimeFormat.forPattern(this.dataPattern).parseLocalDate(this.data);
	}
	
	public LocalTime getHoraInicioExpediente()
	{
		return LocalTime.parse(this.horaInicioExpediente, DateTimeFormat.forPattern(this.timePattern));
	}
	
	public LocalTime getHoraFimExpediente()
	{
		return LocalTime.parse(this.horaFimExpediente, DateTimeFormat.forPattern(this.timePattern));
	}
	
	public Expediente getExpediente() {
		return new Expediente(this.getHoraInicioExpediente(),this.getHoraFimExpediente());
	}

	public String getDataPattern() {
		return dataPattern;
	}

	public void setDataPattern(String dataPattern) {
		this.dataPattern = dataPattern;
	}

	public String getTimePattern() {
		return timePattern;
	}

	public void setTimePattern(String timePattern) {
		this.timePattern = timePattern;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setHoraInicioExpediente(String horaInicioExpediente) {
		this.horaInicioExpediente = horaInicioExpediente;
	}

	public void setHoraFimExpediente(String horaFimExpediente) {
		this.horaFimExpediente = horaFimExpediente;
	}
	
	

}

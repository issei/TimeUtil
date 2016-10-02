package br.com.issei.time.entity;

import org.joda.time.LocalTime;

public class Expediente {
	private final LocalTime horaInicioExpediente;
	private final LocalTime horaFimExpediente;
	
	public Expediente(final LocalTime horaInicioExpediente,final LocalTime horaFimExpediente) {
		super();
		this.horaInicioExpediente = horaInicioExpediente;
		this.horaFimExpediente = horaFimExpediente;
	}

	public LocalTime getHoraInicioExpediente() {
		return horaInicioExpediente;
	}

	public LocalTime getHoraFimExpediente() {
		return horaFimExpediente;
	}
	
	

}

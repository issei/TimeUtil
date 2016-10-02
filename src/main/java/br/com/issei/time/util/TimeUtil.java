package br.com.issei.time.util;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;

import br.com.issei.time.entity.Expediente;
import de.jollyday.Holiday;
import de.jollyday.HolidayManager;
import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
import net.objectlab.kit.datecalc.joda.LocalDateKitCalculatorsFactory;

/**
 * 
 * @author Issei
 * 
 */
public class TimeUtil {
	
	private  final String DATETIMEPATTERN = "dd/MM/yyyy HH:mm:ss";
	private  final String TIMEPATTERN = "HH:mm:ss";
	private final Set<LocalDate> diasNaoUteis = new HashSet<LocalDate>();
	private final Set<LocalDate> diasUteisExcecao = new HashSet<LocalDate>();
	private HashMap<LocalDate,Expediente> expedientesExtraordinarios;
	private final Expediente expediente;
	private final String horaInicioExpedienteDefault = "00:00:00";
	private final String horaFimExpedienteDefault = "23:59:59";
	//TODO: prever periodos de intervalos de desconso nos dias: private Seconds intervaloDescanso; 
	
	/**
	 * Default Constructor
	 */
	public TimeUtil() {
		super();
		this.expediente = new Expediente(LocalTime.parse(horaInicioExpedienteDefault, DateTimeFormat.forPattern(TIMEPATTERN)), LocalTime.parse(horaFimExpedienteDefault, DateTimeFormat.forPattern(TIMEPATTERN)));
	}
	
	/**
	 * 
	 * @param expediente
	 */
	public TimeUtil(Expediente expediente) {
		super();
		this.expediente = expediente;
	}
	/**
	 * 
	 * @param expediente
	 * @param diasNaoUteis
	 */
	public TimeUtil(Expediente expediente, Set<LocalDate> diasNaoUteis) {
		super();
		this.expediente = expediente;
		if(diasNaoUteis!=null && !diasNaoUteis.isEmpty())
			this.diasNaoUteis.addAll(diasNaoUteis);
	}
	
	/**
	 * 
	 * @param expediente
	 * @param diasNaoUteis
	 * @param diasUteisExcecao
	 */
	public TimeUtil(Expediente expediente, Set<LocalDate> diasNaoUteis, Set<LocalDate> diasUteisExcecao) {
		super();
		this.expediente = expediente;
		if(diasNaoUteis!=null && !diasNaoUteis.isEmpty())
			this.diasNaoUteis.addAll(diasNaoUteis);
		if(diasUteisExcecao!=null && !diasUteisExcecao.isEmpty())
			this.diasUteisExcecao.addAll(diasUteisExcecao);
	}
	
	/**
	 * 
	 * @param expediente
	 * @param diasNaoUteis
	 * @param diasUteisExcecao
	 * @param expedientesExtraordinarios
	 */
	public TimeUtil(Expediente expediente, Set<LocalDate> diasNaoUteis, Set<LocalDate> diasUteisExcecao,
			HashMap<LocalDate, Expediente> expedientesExtraordinarios) {
		this.expediente = expediente;
		if(diasNaoUteis!=null && !diasNaoUteis.isEmpty())
			this.diasNaoUteis.addAll(diasNaoUteis);
		if(diasUteisExcecao!=null && !diasUteisExcecao.isEmpty())
			this.diasUteisExcecao.addAll(diasUteisExcecao);
		if(expedientesExtraordinarios!=null && !expedientesExtraordinarios.isEmpty())
			this.expedientesExtraordinarios = expedientesExtraordinarios;
	}

	/**
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return HH:mm
	 */
	public String getDiferencaEmHorasFormatado(DateTime dataInicial,DateTime dataFinal) {
		BigDecimal qtdeHorasDiasUteisNoPeriodo;
		qtdeHorasDiasUteisNoPeriodo = getDiferencaEmHoras(dataInicial,dataFinal);
		String tempo = qtdeHorasDiasUteisNoPeriodo.intValue() +":"+parserMinutos(qtdeHorasDiasUteisNoPeriodo);
		return tempo;
	}
	
	/**
	 * 
	 * @param qtdeHorasDiasUteisNoPeriodo
	 * @return HH:mm
	 */
	public String getDiferencaEmHorasFormatado(BigDecimal qtdeHorasDiasUteisNoPeriodo) {
		String tempo = qtdeHorasDiasUteisNoPeriodo.intValue() +":"+parserMinutos(qtdeHorasDiasUteisNoPeriodo);
		return tempo;
	}

	private String parserMinutos(BigDecimal qtdeHorasDiasUteisNoPeriodo) {
		return fillLeftZeros(qtdeHorasDiasUteisNoPeriodo.subtract(new BigDecimal(qtdeHorasDiasUteisNoPeriodo.intValue())).multiply(new BigDecimal(60.5)).setScale(2,BigDecimal.ROUND_HALF_UP).intValue(),2);
	}
	
	/**
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public BigDecimal getDiferencaEmMinutos(DateTime dataInicial, DateTime dataFinal) {
		return getDiferencaEmHoras(dataInicial,dataFinal).multiply(new BigDecimal(60.5));
	}

	/**
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public BigDecimal getDiferencaEmHoras(DateTime dataInicial,
			DateTime dataFinal) {
		BigDecimal qtdeHorasDiasUteisNoPeriodo;
		BigDecimal minutos = new BigDecimal(Minutes.minutesBetween(dataInicial,dataFinal).getMinutes());
		BigDecimal horas = minutos.divide(new BigDecimal("60"), 3,RoundingMode.HALF_UP);
		int diasNaoUteis = getDiasNaoUteis(dataInicial, dataFinal);
		qtdeHorasDiasUteisNoPeriodo = horas.subtract(new BigDecimal(24 * diasNaoUteis));
		return qtdeHorasDiasUteisNoPeriodo;
	}
	
	/**
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @param horasUteisPorDia
	 * @return
	 */
	public BigDecimal getDiferencaEmHorasUteis(DateTime dataInicial,
			DateTime dataFinal,Double horasUteisPorDia) {
		BigDecimal qtdeHorasDiasUteisNoPeriodo;
		BigDecimal minutos = new BigDecimal(Minutes.minutesBetween(dataInicial,dataFinal).getMinutes());
		BigDecimal horas = minutos.divide(new BigDecimal("60"), 3,RoundingMode.HALF_UP);
		int diasNaoUteis = getDiasNaoUteis(dataInicial, dataFinal);
		qtdeHorasDiasUteisNoPeriodo = horas.divide(new BigDecimal(24).divide(new BigDecimal(horasUteisPorDia),1,RoundingMode.HALF_UP),2,RoundingMode.HALF_UP).subtract(new BigDecimal(24 * diasNaoUteis).divide(new BigDecimal(24).divide(new BigDecimal(horasUteisPorDia),2,RoundingMode.HALF_UP),2,RoundingMode.HALF_UP));
		return qtdeHorasDiasUteisNoPeriodo;
	}
	
	/**
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public Integer getDiferencaEmDiasUteis(DateTime dataInicial, DateTime dataFinal) {
		
		int diascorridos = Days.daysBetween(dataInicial, dataFinal).getDays();		
		int diasNaoUteis = getDiasNaoUteis(dataInicial, dataFinal);
		int diasuteis = diascorridos- diasNaoUteis;
		return diasuteis;
	}

	/**
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public int getDiasNaoUteis(DateTime dataInicial, DateTime dataFinal) {
		int diasNaoUteis = 0;
		DateCalculator<LocalDate> calendario = getCalendarioFeriados();
		LocalDateTime dataInicialTemporaria = new LocalDateTime(dataInicial);
//		System.out.println("dataInicialTemporaria: " + dataInicialTemporaria.toString("dd/MM/yyyy hh:mm"));
		LocalDateTime dataFinalTemporaria = new LocalDateTime(dataFinal);
//		System.out.println("dataFinalTemporaria: " + dataFinalTemporaria.toString("dd/MM/yyyy hh:mm"));
		BigDecimal horas ;		
		BigDecimal minutos = new BigDecimal(Minutes.minutesBetween(dataInicial,dataFinal).getMinutes());
		horas = minutos.divide(new BigDecimal("60"), 3,	RoundingMode.HALF_UP);
//		System.out.println(horas); 
		while (!dataInicialTemporaria.isAfter(dataFinalTemporaria) && horas.intValue()>24) {
		    if (ehDiaDeFolga(calendario, dataInicialTemporaria.toLocalDate())) {
		       diasNaoUteis++;
		    }
		    dataInicialTemporaria = dataInicialTemporaria.plusDays(1);
		}
		return diasNaoUteis;
	}
	
	private boolean ehDiaDeFolga(DateCalculator<LocalDate> calendario, LocalDate data) {
		return calendario.isNonWorkingDay(data) && !diasUteisExcecao.contains(data);
	}
	
	/**
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public Seconds getSegundosUteisEntre(DateTime dataInicial,DateTime dataFinal) {
		//int diasNaoUteis = 0;
		DateCalculator<LocalDate> calendario = getCalendarioFeriados();
		LocalDateTime dataInicialTemporaria = new LocalDateTime(dataInicial);
//			System.out.println("dataInicialTemporaria: " + dataInicialTemporaria.toString(DATETIMEPATTERN));
		LocalDateTime dataFinalTemporaria = new LocalDateTime(dataFinal);
		Seconds segundosUteis = Seconds.seconds(0);
		while (!dataInicialTemporaria.isAfter(dataFinalTemporaria)) {
		    if (ehDiaDeFolga(calendario, dataInicialTemporaria.toLocalDate())) {
		       //diasNaoUteis++;//24 horas ou 1440 minutos
		    }else{
		    	segundosUteis = segundosUteis.plus(validaHorario(dataInicialTemporaria.toDateTime(),dataFinalTemporaria.toDateTime()));
		    }
		    dataInicialTemporaria = dataInicialTemporaria.plusDays(1);
		    dataInicialTemporaria = getExpediente(dataInicialTemporaria.toLocalDate()).getHoraInicioExpediente().toDateTime(dataInicialTemporaria.toDateTime()).toLocalDateTime();
		   
		}
		return segundosUteis;
	}

	/**
	 * 
	 * @return
	 */
	private  DateCalculator<LocalDate> getCalendarioFeriados() {
		HolidayManager gerenciadorDeFeriados = HolidayManager.getInstance(de.jollyday.HolidayCalendar.BRAZIL);
		Set<Holiday> feriados = gerenciadorDeFeriados.getHolidays(new DateTime().getYear(),"br","cs");
		Set<LocalDate> dataDosFeriados = new HashSet<LocalDate>();
		for (Holiday h : feriados) {
			dataDosFeriados.add(new LocalDate(h.getDate(), ISOChronology.getInstance()));
		}
		for(LocalDate pontes : diasNaoUteis)
		{
			dataDosFeriados.add(pontes);
		}
		// popula com os feriados brasileiros
		HolidayCalendar<LocalDate> calendarioDeFeriados  = new DefaultHolidayCalendar<LocalDate>(dataDosFeriados);
		LocalDateKitCalculatorsFactory.getDefaultInstance().registerHolidays("BR", calendarioDeFeriados);
		DateCalculator<LocalDate> calendario =  LocalDateKitCalculatorsFactory.getDefaultInstance().getDateCalculator("BR", HolidayHandlerType.FORWARD);
		return calendario;
	}
	
	/**
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	private Seconds validaHorario(DateTime dataInicio,DateTime dataFim) {
		Seconds secInicio = null;		
		
		if(dataInicio.toLocalTime().isBefore(getExpediente(dataInicio.toLocalDate()).getHoraInicioExpediente())  && dataInicio.toLocalTime().isBefore(getExpediente(dataInicio.toLocalDate()).getHoraFimExpediente()))
		{
			secInicio = validaHorarioMesmoDia(dataInicio, dataFim );
			secInicio = secInicio.minus(Seconds.secondsBetween(dataInicio, getExpediente(dataInicio.toLocalDate()).getHoraInicioExpediente().toDateTime(dataInicio)));
			
		}else if(dataInicio.toLocalTime().isAfter(getExpediente(dataInicio.toLocalDate()).getHoraInicioExpediente()) && dataInicio.toLocalTime().isBefore(getExpediente(dataInicio.toLocalDate()).getHoraFimExpediente()))
		{
			secInicio = validaHorarioMesmoDia(dataInicio, dataFim);
		}else if(dataInicio.toLocalTime().isEqual(getExpediente(dataInicio.toLocalDate()).getHoraInicioExpediente()) && dataInicio.toLocalTime().isBefore(getExpediente(dataInicio.toLocalDate()).getHoraFimExpediente()))
		{
			secInicio = validaHorarioMesmoDia(dataInicio, dataFim);
			
		}else{
			secInicio = Seconds.seconds(0);
		}			
		return secInicio;
	}

	/**
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	private  Seconds validaHorarioMesmoDia(DateTime dataInicio,DateTime dataFim ) {
		Seconds secInicio;
		if(dataInicio.toLocalDate().isEqual(dataFim.toLocalDate()) && dataFim.toLocalTime().isBefore(getExpediente(dataFim.toLocalDate()).getHoraFimExpediente()))
		{
			secInicio =  Seconds.secondsBetween(dataInicio, dataFim);
		}else{
			secInicio =  Seconds.secondsBetween(dataInicio, getExpediente(dataInicio.toLocalDate()).getHoraFimExpediente().toDateTime(dataInicio));
		}
		return secInicio;
	}

	/**
	 * 
	 * @param n
	 * @param len
	 * @return
	 */
	public String fillLeftZeros(int n, int len) {
	
	    StringBuffer sb = new StringBuffer(Integer.toString(n));
	    while (sb.length() < len)
	    {
	        sb.insert(0, "0");
	    }
	    return sb.toString();
	}
	
	/**
	 * 
	 * @param dataInicial
	 * @param qtdeSeconds
	 * @return
	 */
	public LocalDateTime plusUtilSeconds(DateTime dataInicial, int qtdeSeconds) {
		DateCalculator<LocalDate> calendario = getCalendarioFeriados();
		LocalDateTime dataInicialTemporaria = new LocalDateTime(dataInicial);
		LocalDateTime dataFinalTemporaria = new LocalDateTime(dataInicial);
		Seconds segundosUteis = Seconds.seconds(0);
		while (segundosUteis.getSeconds() < (qtdeSeconds)) {
			if (ehDiaDeFolga(calendario, dataFinalTemporaria.toLocalDate())) {
				dataFinalTemporaria = getExpediente(dataFinalTemporaria.toLocalDate()).getHoraInicioExpediente().toDateTime(dataFinalTemporaria.toDateTime()).toLocalDateTime().plusDays(1);
			}
			dataFinalTemporaria = dataFinalTemporaria.plusSeconds((qtdeSeconds) - segundosUteis.getSeconds());
			segundosUteis = getSegundosUteisEntre(dataInicialTemporaria.toDateTime(), dataFinalTemporaria.toDateTime());
		}
		return dataFinalTemporaria;
	}
	
	/**
	 * 
	 * @return ExpedientesExtraordinarios
	 */
	public HashMap<LocalDate, Expediente> getExpedientesExtraordinarios() {
		return expedientesExtraordinarios;
	}
	
	/**
	 * 
	 * @param expedientesExtraordinarios
	 * @return TimeUtil
	 */
	public TimeUtil setExpedientesExtraordinarios(HashMap<LocalDate, Expediente> expedientesExtraordinarios) {
		return new TimeUtil(this.expediente,this.diasNaoUteis,this.diasUteisExcecao,expedientesExtraordinarios);
	}
	
	/**
	 * 
	 * @param data
	 * @return Expediente
	 */
	private Expediente getExpediente(LocalDate data)
	{
		if(this.expedientesExtraordinarios!=null && this.expedientesExtraordinarios.containsKey(data))
		{
			return this.expedientesExtraordinarios.get(data);
		}else
			return this.expediente;
	}

	

}
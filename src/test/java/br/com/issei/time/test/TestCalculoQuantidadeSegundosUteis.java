package br.com.issei.time.test;

import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import br.com.issei.time.entity.Expediente;
import br.com.issei.time.util.TimeUtil;

public class TestCalculoQuantidadeSegundosUteis {
	
	private String TIMEPATTERN = "HH:mm";
	private String horaInicioExpediente = "09:00";
	private String horaFimExpediente = "18:00";
	private int segundosDiaUtilNoveHoras = 32400;
	private LocalTime hiex;
	private LocalTime hfex;
	private DateTime dataAnterior;
	private DateTime dataPosterior;
	private TimeUtil timeUtil;

	@Before
	public void setUp() throws Exception {
		horaInicioExpediente = "09:00";
		horaFimExpediente = "18:00";
		hiex = LocalTime.parse(horaInicioExpediente, DateTimeFormat.forPattern(TIMEPATTERN));
		hfex = LocalTime.parse(horaFimExpediente, DateTimeFormat.forPattern(TIMEPATTERN));
		timeUtil = new TimeUtil(new Expediente(hiex,hfex));
	}
	
	@Test
	public void testContagemSegundosDuranteMesmoDiaComecandoAntesExpediente() {
		dataAnterior = new DateTime(2016, 9, 28, 8, 00);
		dataPosterior = new DateTime(2016, 9, 28, 10, 00);
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(60*60));
	}
	@Test
	public void testContagemSegundosDuranteMesmoDiaComecandoDuranteExpediente() {
		dataAnterior = new DateTime(2016, 9, 28, 10, 00);
		dataPosterior = new DateTime(2016, 9, 28, 11, 00);
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(60*60));
	}
	
	@Test
	public void testContagemSegundosDuranteMesmoDiaComecandoDuranteExpedienteTerminandoDepois() {
		dataAnterior = new DateTime(2016, 9, 28, 17, 00);
		dataPosterior = new DateTime(2016, 9, 28, 19, 00);
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(60*60));
	}

	@Test
	public void testContagemSegundosDeUmDiaUtilParaOutro() {		
		dataAnterior = new DateTime(2016, 9, 27, 17, 59);//Terca-feira
		dataPosterior = new DateTime(2016, 9, 28, 9, 00);//Quarta-feira
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==60);
	}
	
	@Test
	public void testContagemSegundosDeUmaSextaParaSegunda() {
		dataAnterior = new DateTime(2016, 9, 23, 17, 59);//Sexta-feira
		dataPosterior = new DateTime(2016, 9, 26, 9, 00);//Segunda-Feira
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==60);
	}
	
	@Test
	public void testContagemSegundosDuranteFeriado() {
		dataAnterior = new DateTime(2016, 10, 11, 17, 59);//Terca-feira vespera de feriado
		dataPosterior = new DateTime(2016, 10, 13, 9, 00);//Quinta-feira apos feriado
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==60);
	}
	
	@Test
	public void testContagemSegundosDuranteDias() {
		dataAnterior = new DateTime(2016, 8, 12, 2, 00);//Comecando bem antes do expediente na sexta-feira
		dataPosterior = new DateTime(2016, 8, 15, 22, 00);//Terminando bem depois do expediente na segunda-feira
		//Deve contar 2 dias de full de expediente
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(segundosDiaUtilNoveHoras*2));
	}
	
	@Test
	public void testContagemSegundosDuranteUmMesSemFeriados() {
		dataAnterior = new DateTime(2016, 8, 1, 9, 00);//Primeiro dia do mês
		dataPosterior = new DateTime(2016, 8, 31, 18, 00);//Ultimo dia do mês
		//Mês de Agosto de 2016 possui 23 dias uteis
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(segundosDiaUtilNoveHoras*23));
	}
	
	@Test
	public void testContagemSegundosDuranteUmMesComFeriados() {
		dataAnterior = new DateTime(2016, 11, 1, 9, 00);//Primeiro dia do mês
		dataPosterior = new DateTime(2016, 11, 30, 18, 00);//Ultimo dia do mês
		//Mês de Novembro de 2016 possui 20 dias uteis
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(segundosDiaUtilNoveHoras*20));
	}
	
	@Test
	public void testQuantoTempoParaAcabarAno() {
		dataAnterior = new DateTime(System.currentTimeMillis());//Primeiro dia do mês
		dataPosterior = new DateTime(2017, 1, 1, 0, 0);//Ultimo dia do mês
		//Mês de Novembro de 2016 possui 20 dias uteis
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		System.out.println(segundos/60/60);
	}

}

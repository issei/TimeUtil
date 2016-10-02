package br.com.issei.time.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import br.com.issei.time.entity.Expediente;
import br.com.issei.time.util.TimeUtil;

public class TestCalculoQuantidadeSegundosUteisExtraordinarios {
	
	private String TIMEPATTERN = "HH:mm";
	private int segundosDiaUtil = 32400;
	private int segundosDiaExtraordinario = 10800;
	private DateTime dataAnterior;
	private DateTime dataPosterior;
	private TimeUtil timeUtil;

	@Before
	public void setUp() throws Exception {
		
		//Expediente default para todos os dias
		Expediente expDefault = new Expediente(LocalTime.parse("09:00", DateTimeFormat.forPattern(TIMEPATTERN)),LocalTime.parse("18:00", DateTimeFormat.forPattern(TIMEPATTERN)));
		
		//Expediente especial para determinados dias
		HashMap<LocalDate,Expediente> horariosDiferenciados = new HashMap<LocalDate,Expediente>();
		Expediente expExtraordinario = new Expediente(LocalTime.parse("14:00", DateTimeFormat.forPattern(TIMEPATTERN)),LocalTime.parse("17:00", DateTimeFormat.forPattern(TIMEPATTERN)));
		horariosDiferenciados.put(new LocalDate(2016,9,27), expExtraordinario);
		
		//Dias de folga que não são feriados ou final de semana
		HashSet<LocalDate> pontesFeriados = new HashSet<LocalDate>();
		pontesFeriados.add(new LocalDate(2016,10,13));//Ponte ficticia para feriado do dia 12/10
		
		
		//Dias que foram trabalhados, independente se for feriado ou final de semana ou ponte
		HashSet<LocalDate> diaUtilDeterminadoPelaDiretoria = new HashSet<LocalDate>();
		diaUtilDeterminadoPelaDiretoria.add(new LocalDate(2016,9,24));//Ponte ficticia para feriado do dia 12/10
		
		timeUtil = new TimeUtil(expDefault,pontesFeriados,diaUtilDeterminadoPelaDiretoria,horariosDiferenciados);
	}
	
	@Test
	public void testContagemSegundosDuranteMesmoDiaComecandoAntesExpediente() {
		dataAnterior = new DateTime(2016, 9, 28, 8, 00);//Sexta-feira
		dataPosterior = new DateTime(2016, 9, 28, 10, 00);//Segunda-Feira
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(60*60));
	}
	@Test
	public void testContagemSegundosDuranteMesmoDiaComecandoDuranteExpediente() {
		dataAnterior = new DateTime(2016, 9, 28, 10, 00);//Sexta-feira
		dataPosterior = new DateTime(2016, 9, 28, 11, 00);//Segunda-Feira
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(60*60));
	}
	
	@Test
	public void testContagemSegundosDuranteMesmoDiaComecandoDuranteExpedienteTerminandoDepois() {
		dataAnterior = new DateTime(2016, 9, 28, 17, 00);//Sexta-feira
		dataPosterior = new DateTime(2016, 9, 28, 19, 00);//Segunda-Feira
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(60*60));
	}

	@Test
	public void testContagemSegundosDeUmDiaUtilParaOutro() {		
		dataAnterior = new DateTime(2016, 9, 13, 17, 59);//Terca-feira
		dataPosterior = new DateTime(2016, 9, 14, 9, 00);//Quarta-feira
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==60);
	}
	
	@Test
	public void testContagemSegundosDeUmDiaUtilParaOutroComExpedienteReduzido() {		
		dataAnterior = new DateTime(2016, 9, 26, 17, 59);//Segunda-feira
		dataPosterior = new DateTime(2016, 9, 29, 9, 00);//Quarta-feira
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==segundosDiaExtraordinario+segundosDiaUtil+60);
	}
	
	@Test
	public void testContagemSegundosDeUmaSextaParaSegunda() {
		dataAnterior = new DateTime(2016, 9, 23, 17, 59);//Sexta-feira
		dataPosterior = new DateTime(2016, 9, 26, 9, 00);//Segunda-Feira
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		//Como foi definido que dia 24 seria util então espera-se segundosDiaUtil+60
		assertTrue(segundos==(segundosDiaUtil+60));
	}
	
	@Test
	public void testContagemSegundosDuranteFeriado() {
		dataAnterior = new DateTime(2016, 10, 11, 17, 59);//Terca-feira vespera de feriado
		dataPosterior = new DateTime(2016, 10, 14, 9, 00);//Sexta-feira apos feriado
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		//Como foi considerado dia 13 ponte do feriado então espera-se 60 segundos
		assertTrue(segundos==60);
	}
	
	@Test
	public void testContagemSegundosDuranteDias() {
		dataAnterior = new DateTime(2016, 8, 12, 2, 00);//Comecando bem antes do expediente na sexta-feira
		dataPosterior = new DateTime(2016, 8, 15, 22, 00);//Terminando bem depois do expediente na segunda-feira
		//Deve contar 2 dias de full de expediente
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(segundosDiaUtil*2));
	}
	
	@Test
	public void testContagemSegundosDuranteUmMesSemFeriados() {
		dataAnterior = new DateTime(2016, 8, 1, 9, 00);//Primeiro dia do mês
		dataPosterior = new DateTime(2016, 8, 31, 18, 00);//Ultimo dia do mês
		//Mês de Agosto de 2016 possui 23 dias uteis
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(segundosDiaUtil*23));
	}
	
	@Test
	public void testContagemSegundosDuranteUmMesComFeriados() {
		dataAnterior = new DateTime(2016, 11, 1, 9, 00);//Primeiro dia do mês
		dataPosterior = new DateTime(2016, 11, 30, 18, 00);//Ultimo dia do mês
		//Mês de Novembro de 2016 possui 20 dias uteis
		int segundos = timeUtil.getSegundosUteisEntre(dataAnterior, dataPosterior).getSeconds();
		assertTrue(segundos==(segundosDiaUtil*20));
	}

}

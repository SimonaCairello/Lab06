package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private MeteoDAO meteoDao = new MeteoDAO();
	private List<Rilevamento> soluzione;
	private List<List<Rilevamento>> rilevamenti = new ArrayList<List<Rilevamento>>();
	private int bestCosto;
	private int costoParziale = 0;

	public Model() {

	}
	
	public List<Rilevamento> getAllRilevamenti() {
		return meteoDao.getAllRilevamenti();
	}
	
	public List<Rilevamento> getAllRilevamentiMese(int mese) {
		return meteoDao.getAllRilevamentiMese(mese);
	}
	
	public List<String> getAllLocalitaPerMese(int mese) {
		return meteoDao.getAllLocalitaPerMese(mese);
	}
	
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		return meteoDao.getAllRilevamentiLocalitaMese(mese, localita);
	}
	
	public List<Rilevamento> getAllRilevamentiMeseGiorno(int mese, int giorno) {
		return meteoDao.getAllRilevamentiMeseGiorno(mese, giorno);
	}

	public List<String> getUmiditaMedia(int mese) {
		List<String> umiditaMedia= new ArrayList<String>();
		List<Rilevamento> rilevamenti = meteoDao.getAllRilevamentiMese(mese);
		List<String> localita = this.getAllLocalitaPerMese(mese);
		
		String s = "";
		int somma;
		int counter;
		double media = 0.0;
		
		for(String l : localita) {
			somma = 0;
			counter = 0;
			
			for(Rilevamento r : rilevamenti) {
				if(l.equals(r.getLocalita())) {
					somma += r.getUmidita();
					counter++;
				}
			}
			
			media = ((double) somma)/((double) counter);
			
			s = l+" "+media+"\n";
			umiditaMedia.add(s);
		}
		
		return umiditaMedia;	
	}
	
	public void ricorsiva(List<Rilevamento> parziale, int livello, List<List<Rilevamento>> rilevamenti) {
		if(livello==15) {
			if(this.controllaSequenza(parziale)) {
				costoParziale = this.calcolaCosto(parziale); 
				if(bestCosto==0) {
					bestCosto = costoParziale;
					this.soluzione.addAll(parziale);
					costoParziale = 0;
				}
				else if(costoParziale<=bestCosto) {
					bestCosto = costoParziale;
					this.soluzione.clear();
					this.soluzione.addAll(parziale);
					costoParziale = 0;
				}
			}
			return;
		}
		
		for(Rilevamento r : rilevamenti.get(livello)) {			
			parziale.add(r);
			
			this.ricorsiva(parziale, livello+1, rilevamenti);
			
			parziale.remove(r);
		}
	}
	
	public List<Rilevamento> trovaSequenza(int mese) {
		this.soluzione = new ArrayList<Rilevamento>();
		
		for(int i=1; i<16; i++) {
			rilevamenti.add(this.getAllRilevamentiMeseGiorno(mese, i));
		}
			
		List<Rilevamento> parzialeVuoto = new ArrayList<Rilevamento>();
			
		ricorsiva(parzialeVuoto, 0, rilevamenti); 
		return this.soluzione;
	}
	
	public int calcolaCosto(List<Rilevamento> parziale) {
		int costoP = 0;
		
		for(int i=0; i<NUMERO_GIORNI_TOTALI; i++) {
			costoP += parziale.get(i).getUmidita();
			
			if(i>0 && !parziale.get(i).getLocalita().equals(parziale.get(i-1).getLocalita())) {
				costoP += COST;
			}
		}
		return costoP;
	}
	
	public boolean controllaSequenza(List<Rilevamento> parziale) {
		int contatoreSequenzaMin = 0;
		int contatoreTorino = 0;
		int contatoreMilano = 0;
		int contatoreGenova = 0;
		
		for(int i=0; i<15; i++) {
			if(i>0 && parziale.get(i).getLocalita().equals(parziale.get(i-1).getLocalita())) {
				contatoreSequenzaMin++;
			}
			
			if(parziale.get(i).getLocalita().toLowerCase().equals("torino")) {
				contatoreTorino++;
			}
			
			if(parziale.get(i).getLocalita().toLowerCase().equals("milano")) {
				contatoreMilano++;
			}
			
			if(parziale.get(i).getLocalita().toLowerCase().equals("genova")) {
				contatoreGenova++;
			}
		}
		
		if(contatoreSequenzaMin>=10 && contatoreTorino<=NUMERO_GIORNI_CITTA_MAX && contatoreMilano<=NUMERO_GIORNI_CITTA_MAX && contatoreGenova<=NUMERO_GIORNI_CITTA_MAX)
			return true;
		return false;
	}

}

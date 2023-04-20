package it.polito.tdp.meteo.model;

import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	private List<Citta> citta = new LinkedList<>();
	private List<Rilevamento> rilevamenti;
	MeteoDAO dao;
	int mese;
	
	public Model() {
		dao = new MeteoDAO();
		rilevamenti = dao.getAllRilevamenti();
		for(Rilevamento r : rilevamenti) {
			Citta c = new Citta(r.getLocalita());
			if (! citta.contains(c)){
				citta.add(c);
			}
		}
	} 

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		this.mese = mese;
		String s="";
		for (Citta c : citta) {
			s += c.getNome() + ": " + dao.getUmiditaMedia(mese, c.getNome()) + "\n";
		}
		return s;
	}
	
	
	LinkedList<Citta> sequenza;
	double costoattuale = Integer.MAX_VALUE;
	LinkedList<Citta> parziale;
	double costo;
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		
		sequenza = new LinkedList<>();
		parziale = new LinkedList<>();
		
		cercasequenza(parziale);
		
		String s="";
		for(Citta c : sequenza) {
			s += c.getNome()+" \n";
		}
		return s;
	}

	public void cercasequenza(LinkedList<Citta> parziale) {
		
		if (! controllo(parziale)) {
			return;
		}
		
		if (parziale.size()>15) {
			return;
		}
		
		else if (parziale.size() == Model.NUMERO_GIORNI_TOTALI) {
			double d = calcolaCosto(parziale);
			if (d<costoattuale) {
				costoattuale = d;
				sequenza = new LinkedList<>(parziale);
			}
			return;
		}
		
		else if(parziale.size() == 0){
			for (Citta c : citta) {
				//aggiungi tre citta
				parziale.add(c);
				parziale.add(c);
				parziale.add(c);
				c.increaseCounter(3);
				
				//cerca
				cercasequenza(parziale);
		
				//remove
				parziale.removeLast();
				parziale.removeLast();
				parziale.removeLast();
				c.decreaseCounter(3);
			}
			
		}
		
		else {
			for (Citta c : citta) {
				
				//se la città è uguale all'ultima in lista
				if(parziale.getLast().equals(c)) {
					
					//aggiungi di nuovo ultima citta in lista
					parziale.add(c);
					c.increaseCounter(1);
					
					//cerca
					cercasequenza(parziale);
					
					//remove una citta
					parziale.removeLast();
					c.decreaseCounter(1);
				}//se c non è uguale all'ultima in lista
				else {

					//aggiungi tre citta
					parziale.add(c);
					parziale.add(c);
					parziale.add(c);
					c.increaseCounter(3);
					
					//cerca
					cercasequenza(parziale);
			
					//remove
					parziale.removeLast();
					parziale.removeLast();
					parziale.removeLast();
					c.decreaseCounter(3);
					
				}
			}
		}
	}
	
	public boolean controllo(List<Citta> parziale) {
		
		boolean flag = true;
		
		for(Citta c : citta) {
			if(c.getCounter()>6) {
				flag = false;
			}
		}
		
		if(parziale.size()==Model.NUMERO_GIORNI_TOTALI) {
			for(Citta c : citta) {
				if (!parziale.contains(c)) {
					flag = false;
				}
			}
		}
		
		return flag;
	}
	
	private double calcolaCosto(LinkedList<Citta> parziale) {
		double costo = 0;
		for(int i= 1; i<parziale.size(); i++) {
			if(!parziale.get(i).equals(parziale.get(i-1))){
				costo += 100;
			}
			for(Rilevamento r : rilevamenti) {
				if (r.getData().getMonthValue()==mese && parziale.get(i).getNome().compareTo(r.getLocalita())==0 && r.getData().getDayOfMonth()==i) {
					costo += r.getUmidita();
				}
			}
		}	
		
		return costo;
	}
	
	
	/*private double calcolacostoumidita(List<Citta> parziale) {
		double costorelativo = 0;
		for (Citta c : parziale) {
			for(Rilevamento r : rilevamenti) {
				if (r.getData().getMonthValue()==mese && c.getNome().compareTo(r.getLocalita())==0 && r.getData().getDayOfMonth()==parziale.indexOf(c)) {
					costorelativo += r.getUmidita();
				}
			}
		}
		return costorelativo;
	}*/
}

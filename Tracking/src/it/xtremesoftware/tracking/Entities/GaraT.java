package it.xtremesoftware.tracking.Entities;

import java.util.Date;

public class GaraT {
	private static final String LOG_TITLE= "GaraT";
	

	//PROPERTY dell'OGGETTO
    private Long idGara;
    private Long idRuoloINGara;
    private String codRuolo;
    private String gara;
    private String codiceAttivazione;
    private String status;
    private Date inizio;
    private Date fine;
	
	public Long getIdGara() {
		return idGara;
	}
	public void setIdGara(Long idGara) {
		this.idGara = idGara;
	}

	public Long getIdRuoloINGara() {
		return idRuoloINGara;
	}
	public void setIdRuoloINGara(Long idRuoloINGara) {
		this.idRuoloINGara = idRuoloINGara;
	}
	public String getCodRuolo() {
		return codRuolo;
	}
	public void setCodRuolo(String codRuolo) {
		this.codRuolo = codRuolo;
	}
	public String getGara() {
		return gara;
	}
	public void setGara(String gara) {
		this.gara = gara;
	}
	public String getCodiceAttivazione() {
		return codiceAttivazione;
	}
	public void setCodiceAttivazione(String codiceAttivazione) {
		this.codiceAttivazione = codiceAttivazione;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getInizio() {
		return inizio;
	}
	public void setInizio(Date inizio) {
		this.inizio = inizio;
	}
	public Date getFine() {
		return fine;
	}
	public void setFine(Date fine) {
		this.fine = fine;
	}
	
	
    
}

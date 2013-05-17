package it.xtremesoftware.tracking.Entities;

public class TrackingT {
	
private static final String LOG_TITLE= "TrackingT";
	
	//PROPERTY dell'OGGETTO
    private Long idGara;
    private Integer idRuoloGara;
	private String codRuolo;
    private Float latitudine;
    private Float longitudine;
    private Integer secReliability;
    private String status;
    private Integer progressivo;
    private Float bearing;
    private Float speed;
	
	public Long getIdGara() {
		return idGara;
	}
	public void setIdGara(Long idGara) {
		this.idGara = idGara;
	}
	public Integer getIdRuoloGara() {
		return idRuoloGara;
	}
	public void setIdRuoloGara(Integer idRuoloGara) {
		this.idRuoloGara = idRuoloGara;
	}
	public String getCodRuolo() {
		return codRuolo;
	}
	public void setCodRuolo(String codRuolo) {
		this.codRuolo = codRuolo;
	}

	public Float getLatitudine() {
		return latitudine;
	}
	public void setLatitudine(Float latitudine) {
		this.latitudine = latitudine;
	}
	public Float getLongitudine() {
		return longitudine;
	}
	public void setLongitudine(Float longitudine) {
		this.longitudine = longitudine;
	}
	public Integer getSecReliability() {
		return secReliability;
	}
	public void setsecReliability(Integer secReliability) {
		this.secReliability = secReliability;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getProgressivo() {
		return progressivo;
	}
	public void setProgressivo(Integer progressivo) {
		this.progressivo = progressivo;
	}
	public Float getBearing() {
		return bearing;
	}
	public void setBearing(Float bearing) {
		this.bearing = bearing;
	}
	public Float getSpeed() {
		return speed;
	}
	public void setSpeed(Float speed) {
		this.speed = speed;
	}
	
}

package ch.fdehedin.concerts.model;

import java.io.Serializable;
import java.util.Date;

public class TimeRange implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 582607164293075923L;

	private Date dateTimeStart = new Date();

	private Date dateTimeEnd = new Date();

	public TimeRange() {
		super();
		
	}
	
	public TimeRange(Date dateTimeStart, Date dateTimeEnd) {
		super();
		this.dateTimeStart = dateTimeStart;
		this.dateTimeEnd = dateTimeEnd;
	}

	public Date getDateTimeStart() {
		return dateTimeStart;
	}

	public void setDateTimeStart(Date dateTimeStart) {
		this.dateTimeStart = dateTimeStart;
	}

	public Date getDateTimeEnd() {
		return dateTimeEnd;
	}

	public void setDateTimeEnd(Date dateTimeEnd) {
		this.dateTimeEnd = dateTimeEnd;
	}
}

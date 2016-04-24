package ch.fdehedin.concerts.controller;

import java.io.Serializable;

import javax.faces.context.ResponseWriter;

import javax.servlet.http.HttpServletResponse;

import biweekly.ICalendar;
import biweekly.component.VEvent;

import ch.belsoft.tools.JSFUtil;
import ch.belsoft.tools.Util;
import ch.fdehedin.concerts.model.Concert;
import ch.fdehedin.concerts.model.TimeRange;

/*

 <!--
 Copyright 2016 Frédéric Dehédin
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and limitations under the License
 -->

 */

public class ControllerConcertsIcsFeed extends ControllerConcerts implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String CONTENT_TYPE = "text/calendar";
	private static final String FILE_NAME = "concerts_<%CITY%>.ics";
	private static final String FILE_NAME_PLACEHOLDER_CITY = "<%CITY%>";
	private static final String HEADER_CONTENTDISPOSITION = "Content-disposition";

	public void createIcsFeed() {
		try {
			HttpServletResponse res = JSFUtil.getResponse();
			res.setContentType(CONTENT_TYPE);
			String fileName = FILE_NAME.replace(FILE_NAME_PLACEHOLDER_CITY,
					this.getSubmittedCity());
			res.setHeader(HEADER_CONTENTDISPOSITION, "filename=\"" + fileName
					+ "\"");
			ResponseWriter writer = JSFUtil.getResponseWriter();

			writer.write(getConcertsAsIcs());
 
			writer.flush();

		} catch (Exception e) {
			Util.logError(e);
		}

	}

	private String getSubmittedCity() {
		String city = JSFUtil.getQueryString("city");
		if (city.equals("")) {
			return CITY_ALL;
		} else {
			return city;
		}
	}

	/**
	 * Creates a ICS Feed of all the concerts. The concerts are retrieved by the
	 * parent class ControllerConcerts
	 * 
	 * @return ICS Feed as String
	 */
	public String getConcertsAsIcs() {

		String result = "";
		try {

			// initiate the biweekly ICalendar object
			ICalendar ical = new ICalendar();

			// Setting the product ID / Description of the feed.
			ical.setProductId("Events as ICS Feed, City: "
					+ this.getSubmittedCity());

			// Retrieving the concerts and fill the ICS Feed
			for (Concert concert : this.getConcerts()) {
				addIcsFeedElement(ical, concert);
			}

			// return the result as string
			result = ical.write();

		} catch (Exception e) {
			Util.logError(e);
		}

		return result;
	}

	/**
	 * Creates and adds a single VEvent Object to the ICalendar feed
	 * 
	 * @param ical
	 *            ICalendar Object
	 * @param concert
	 *            Single Concert Object
	 */
	private void addIcsFeedElement(ICalendar ical, Concert concert) {
		try {
			// Loops through the time ranges, since an event can have multiple
			// dates
			for (TimeRange tr : concert.getDates()) {
				VEvent event = new VEvent();
				event.setSummary(concert.getName() + "," + concert.getCity());
				event.setLocation(concert.getCity());
				event.setDateStart(tr.getDateTimeStart());
				event.setDateEnd(tr.getDateTimeEnd());
				ical.addEvent(event);
			}
		} catch (Exception e) {
			Util.logError(e);
		}
	}

}

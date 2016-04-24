package ch.fdehedin.concerts.controller;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import lotus.domino.DateTime;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;
import ch.belsoft.controller.DataAccessController;
import ch.belsoft.tools.JSFUtil;
import ch.belsoft.tools.Util;
import ch.fdehedin.concerts.model.Concert;
import ch.fdehedin.concerts.model.TimeRange;

public class ControllerConcerts {

	private List<Concert> concerts = new ArrayList<Concert>();
	private List<String> cities = new ArrayList<String>();

	private String city = "";

	private static final String VIEW_CONCERTS = "vwConcertsByCity";
	protected static final String CITY_ALL = "all";

	private static final String ICSFEED_PAGENAME = "concert_icsfeed.xsp";
	private static final String ICSFEED_WEBCAL_PROTOCOL = "webcal";
	private static final String ICSFEED_HTTP_PROTOCOL = "http";
	private static final String ICSFEED_SUFFIX = "?openxpage&city=#CITY#";
	private static final String ICSFEED_CITY = "#CITY#";

	public String getIcsFeedLink() {
		String result = "";

		try {

			result = JSFUtil.getCurrentUrl().replace(
					JSFUtil.getCurrentPageName(), ICSFEED_PAGENAME);

			if (!this.city.equals("")) {
				result += ICSFEED_SUFFIX.replace(ICSFEED_CITY, this.city);
			}

		} catch (Exception e) {
			Util.logError(e);
		}
		return result;
	}

	public String getIcsFeedDirectLink() {
		String result = "";

		try {
			result = this.getIcsFeedLink().replace(ICSFEED_HTTP_PROTOCOL,
					ICSFEED_WEBCAL_PROTOCOL);
		} catch (Exception e) {
			Util.logError(e);
		}
		return result;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<String> getCities() {
		if (this.cities.size() == 0) {
			this.refreshConcerts(this.city);
		}

		return cities;
	}

	public void setCities(List<String> cities) {
		this.cities = cities;
	}

	public void clearConcerts() {
		this.concerts.clear();
	}

	public List<Concert> getConcerts() {

		try {
			if (concerts.size() == 0) {
				this.refreshConcerts(this.city);
			}
		} catch (Exception e) {
			Util.logError(e);
		}
		return concerts;
	}

	public String getConcertListTitle() {

		String result = "";

		try {
			if (this.city.equals(CITY_ALL) || this.city.equals("")) {
				result = "All Concerts";
			} else {
				result = "Concerts in " + this.city;
			}
		} catch (Exception e) {
			Util.logError(e);
		}
		return result;
	}

	public void refreshConcerts(String city) {

		View vw = null;
		ViewNavigator nav = null;

		ViewEntry entry = null;
		ViewEntry tentry = null;

		try {

			vw = DataAccessController.getView(VIEW_CONCERTS);
			String key = CITY_ALL;
			if (city != null && !city.equals("")) {
				key = city;
			}
			nav = DataAccessController.getViewNav(vw, key);

			entry = nav.getFirst();
			tentry = null;

			while (entry != null) {

				Concert concert = new Concert();
				refreshValuesConcert(concert, entry);
				if (!this.cities.contains(concert.getCity())) {
					this.cities.add(concert.getCity());

				}
				concerts.add(concert);
				tentry = nav.getNext(entry);

				entry.recycle();
				entry = tentry;
			}

		} catch (Exception e) {
			Util.logError(e);
		} finally {
			DataAccessController.recycleObjects(vw, nav);
		}
	}

	private void refreshValuesConcert(Concert concert, ViewEntry entry) {
		try {

			String name = (String) entry.getColumnValues().elementAt(1);
			String city = (String) entry.getColumnValues().elementAt(2);

			Vector<Object> oStart = Util.getVector((Object) entry
					.getColumnValues().elementAt(4));
			Vector<Object> oEnd = Util.getVector((Object) entry
					.getColumnValues().elementAt(5));

			for (int i = 0; i < oStart.size(); i++) {
				if (oStart.elementAt(i).getClass().getName().endsWith(
						"DateTime")) {
					Date dtStart = ((DateTime) oStart.elementAt(i))
							.toJavaDate();

					// we assume that the end date is the same as the start
					// date..
					Date dtEnd = ((DateTime) oEnd.elementAt(i)).toJavaDate();
					TimeRange timeRange = new TimeRange(dtStart, dtEnd);
					concert.addDate(timeRange);
				}
			}

			concert.setUnid(entry.getUniversalID());
			concert.setName(name);
			concert.setCity(city);

		} catch (Exception e) {
			Util.logError(e);
		}
	}

}

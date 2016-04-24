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

package ch.fdehedin.concerts.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.belsoft.model.DataObject;
import ch.fdehedin.concerts.model.TimeRange;

public class Concert extends DataObject {

	private String name = "";
	private String city = "";
	private Date startDate = null;
	private Calendar calStartDate = null;
	private Date startTime = null;
	private Date endTime = null;

	private List<TimeRange> dates = new ArrayList<TimeRange>();

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TimeRange getFirstTimeRange() {
		return dates.get(0);
	}

	public List<TimeRange> getDates() {
		return dates;
	}

	public void addDate(TimeRange dates) {
		this.dates.add(dates);
	}

	public void setDates(List<TimeRange> dates) {
		this.dates = dates;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Calendar getCalStartDate() {
		return calStartDate;
	}

	public void setCalStartDate(Calendar calStartDate) {
		this.calStartDate = calStartDate;
	}

}

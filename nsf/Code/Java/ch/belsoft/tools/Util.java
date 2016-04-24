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

package ch.belsoft.tools;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import com.paulwithers.openLog.OpenLogItem;

import lotus.domino.DateTime;

public class Util {

	public static void logEvent(String sError) {
		OpenLogItem
				.logEvent(null, sError, OpenLogItem.getSeverity().INFO, null);
	}

	public static void logError(Exception e) {
		OpenLogItem.logError(e);
	}

	public static String parseBoolenToString(boolean b) {
		return b ? "1" : "0";
	}

	public static boolean parseBool(String s) {
		return "1".equals(s);
	}

	public static <K, V> K getFirstKey(LinkedHashMap<K, V> map) {
		Iterator<K> i = map.keySet().iterator();
		return i.hasNext() ? i.next() : null;
	}

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size
				/ Math.pow(1024, digitGroups))
				+ " " + units[digitGroups];
	}

	public static Vector<Object> getVector(Object object) {
		Vector<Object> result = new Vector<Object>();
		try {
			if (!object.getClass().getName().endsWith("Vector")) {
				result.add(object);
			} else {
				result = (Vector<Object>) object;
			}
		} catch (Exception e) {
			Util.logError(e);
		}
		return result;
	}

	public static String getFileExtension(String attachmentName) {
		String result = attachmentName;
		try {
			int i = attachmentName.lastIndexOf('.');
			if (i > 0) {
				result = attachmentName.substring(i + 1);
			}
		} catch (Exception e) {
			Util.logError(e);
		}
		return result;
	}

	public static String getAttachmentNameShort(String attachmentName) {
		String result = attachmentName;

		try {
			String[] tokens = attachmentName.split("\\.(?=[^\\.]+$)");

			String base = tokens[0];

			if (tokens[0].length() > 5) {
				attachmentName = base.substring(0, 5) + "..";
				attachmentName = attachmentName + tokens[1];
			}

			result = attachmentName;
		} catch (Exception e) {
			Util.logError(e);
		}
		return result;
	}

	public static String getUsDate(Date dt) {
		if (dt == null) {
			return "";
		} else {
			SimpleDateFormat sdfDestination = new SimpleDateFormat("MM/dd/yyyy");
			return sdfDestination.format(dt);
		}
	}

	public static String getDateString(Date dt) {
		// TODO Auto-generated method stub

		if (dt == null) {
			return "";
		} else {
			SimpleDateFormat sdfDestination = new SimpleDateFormat("dd.MM.yyyy");
			return sdfDestination.format(dt);
		}
	}

	public static DateTime getDominoDate(Date javaDate) {
		DateTime result = null;
		try {
			result = JSFUtil.getCurrentSession().createDateTime(javaDate);
		} catch (Exception e) {
			Util.logError(e);
		}

		return result;
	}

	public static DateTime getDominoDate(Vector<DateTime> dateTimeArray) {
		DateTime result = null;
		try {
			if (dateTimeArray.size() > 0) {
				result = (DateTime) dateTimeArray.firstElement();
			}
		} catch (Exception e) {
			Util.logError(e);
		}

		return result;
	}

	public static Calendar getCalendarFromDominoDate(
			Vector<DateTime> dateTimeArray) {
		Calendar cal = Calendar.getInstance();
		try {
			Date dtTemp = getJavaDateFromDominoDate(dateTimeArray);
			cal.setTime(dtTemp);
		} catch (Exception e) {
			Util.logError(e);
		}

		return cal;
	}

	public static Date getJavaDateFromDominoDate(Vector<DateTime> dateTimeArray) {
		Date result = new Date();
		try {
			DateTime dtTemp = getDominoDate(dateTimeArray);
			if (dtTemp != null) {
				result = dtTemp.toJavaDate();
				dtTemp.recycle();
			}
		} catch (Exception e) {
			Util.logError(e);
		}

		return result;
	}

	public static String implode(List<String> lst) {
		String result = "";
		try {
			for (String s : lst) {
				if (!result.equals("")) {
					result += ", ";
				}
				result += s;
			}
		} catch (Exception e) {
			Util.logError(e);
		}
		return result;
	}

	public static List<Double> objectToDoubleList(Object object) {
		List<Double> result = new ArrayList<Double>();
		try {
			if (!object.getClass().getName().endsWith("Vector")) {
				result.add((Double) object);
			} else {
				result = (Vector<Double>) object;
			}
		} catch (Exception e) {
			Util.logError(e);
		}
		return result;
	}

	public static List<String> objectToStringList(Object object) {
		List<String> result = new ArrayList<String>();
		try {
			if (!object.getClass().getName().endsWith("Vector")) {
				result.add((String) object);
			} else {
				result = (Vector<String>) object;
			}
		} catch (Exception e) {
			Util.logError(e);
		}
		return result;
	}

	public static List<String> vectorToList(Vector itemValue) {
		List<String> lst = itemValue;
		return lst;
	}
}

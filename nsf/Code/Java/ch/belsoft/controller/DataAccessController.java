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

package ch.belsoft.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lotus.domino.Base;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import lotus.domino.ViewNavigator;
import ch.belsoft.tools.JSFUtil;
import ch.belsoft.tools.Util;

import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import com.ibm.xsp.model.domino.wrapped.DominoRichTextItem;

public class DataAccessController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2847064229957786852L;

	// private static final String PROFILE = "fmProfile";
	private static final String QUERYSTRING_DOCUMENTID = "documentId";
	private static final String PROFILE = "fmProfile";

	// private HashMap<String, List<String>> hmGroupMembersCache = new
	// HashMap<String, List<String>>();

	public static void remove(String unid) {
		Document doc = null;
		try {
			doc = getDb().getDocumentByUNID(unid);
			if (doc != null) {
				doc.remove(true);
			}
		} catch (Exception e) {
			Util.logError(e);
		} finally {
			recycleObjects(doc);
		}

	}

	public static Document getNewDocument() {
		Document doc = null;
		try {
			doc = getDb().createDocument();
			doc.replaceItemValue("Form", "fmVerkehrsDaten");
		} catch (Exception e) {
			Util.logError(e);
		}
		return doc;
	}

	public static Document getCurrentDocument() {
		return DataAccessController.getCurrentDocument(false);
	}

	public static Document getCurrentDocument(boolean create) {
		Document doc = null;

		try {
			String unid = JSFUtil.getQueryString(QUERYSTRING_DOCUMENTID);
			if (!"".equals(unid)) {
				doc = DataAccessController.getDocByUnid(unid);
			}
			if (doc == null && create) {
				doc = DataAccessController.getNewDocument();
			}
		} catch (Exception e) {
			Util.logError(e);
		}

		return doc;
	}

	public static Database getDb() {
		Database db = null;
		try {
			db = JSFUtil.getCurrentSession().getCurrentDatabase();
		} catch (Exception e) {
			Util.logError(e);
		}
		return db;
	}

	public static Document getProfile() {
		Document profile = null;

		try {
			profile = getDb().getProfileDocument(PROFILE, "");
		} catch (Exception e) {
			Util.logError(e);
		}
		return profile;
	}

	public static String getProfileFieldValue(String fieldName) {
		String result = "";
		Document profile = null;
		try {
			profile = DataAccessController.getProfile();
			fieldName = fieldName.toLowerCase();
			result = profile.getItemValueString(fieldName);
			if ("".equals(result)) {
				result = profile.getItemValueString(fieldName);
			}
		} catch (Exception e) {
			Util.logError(e);
		} finally {
			DataAccessController.recycleObjects(profile);
		}
		return result;
	}

	public static Database getDbAsSigner() {
		Database db = null;
		try {
			db = JSFUtil.getSessionAsSigner().getCurrentDatabase();
		} catch (Exception e) {
			Util.logError(e);
		}
		return db;
	}

	public static Document getDocByKey(String sViewName, String sKey) {

		Document docResult = null;

		try {
			View vw = getView(sViewName);

			docResult = vw.getDocumentByKey(sKey);

			if (docResult == null) {
				Util.logEvent("Could not find document with key " + sKey
						+ ", view: " + sViewName);
			}
			vw.recycle();
		} catch (Exception e) {
			Util.logError(e);
		}

		return docResult;

	}

	public static Document getDocByUnidAsSigner(String unid) {
		Document doc = null;
		try {
			doc = getDbAsSigner().getDocumentByUNID(unid);
			if (doc == null) {
				Util.logEvent("Document with unid: " + unid + " not found.");
			}
		} catch (Exception e) {
			Util.logError(e);
		}
		return doc;
	}

	public static Document getDocByUnid(String unid) {
		Document doc = null;
		try {
			doc = getDb().getDocumentByUNID(unid);
			if (doc == null) {
				Util.logEvent("Document with unid: " + unid + " not found.");
			}
		} catch (Exception e) {
			Util.logError(e);
		}
		return doc;
	}

	public static String getHtmlOfRichTextField(Document doc, String itemName) {
		String html = "";

		try {
			DominoDocument ddoc = wrapDocument(doc, itemName);
			if (ddoc.hasItem(itemName)) {
				DominoRichTextItem drti = ddoc.getRichTextItem(itemName);
				html = drti.getHTML();
			}
		} catch (Exception e) {
			Util.logError(e);
		}

		return html;
	}

	public static View getView(String sViewName, boolean autoupdate) {
		View vw = null;

		try {
			Database db = getDb();
			vw = db.getView(sViewName);
			vw.setAutoUpdate(autoupdate);

		} catch (Exception e) {
			Util.logError(e);
		}
		return vw;
	}

	public static View getView(String sViewName) {
		return getView(sViewName, true);
	}

	/**
	 * @param sDbKey
	 *            : Key of the Database to access ("Schaden", "Kontakte", etc.)
	 * @param sViewName
	 *            : Name of the view
	 * @param sKey
	 *            : Key
	 * @return the single view entry, RECYCLE AT THE END
	 */
	public static ViewEntry getViewEntry(View vw, String sKey) {
		ViewEntry entryResult = null;
		try {
			// View vw = getView(sDbKey, sViewName);
			vw.setAutoUpdate(false);

			entryResult = vw.getEntryByKey(sKey);

			if (entryResult == null) {
				Util.logEvent("Could not find entry with key " + sKey
						+ " in DB: " + vw.getParent().getFilePath()
						+ ", view: " + vw.getAliases());
			}

		} catch (Exception e) {
			Util.logError(e);
		}

		return entryResult;
	}

	public static List<String> getViewLookup(String sViewName, int iCol) {
		// Util.logEvent("getViewLookup: " + sDbKey + ", " + sViewName + ", "
		// + iCol);

		return getViewLookup(sViewName, "", iCol, false);
	}

	/**
	 * 
	 * @param sDbKey
	 *            : Key of the Database to access ("Schaden", "Kontakte", etc.)
	 * @param sViewName
	 *            : Name of the view
	 * @param sKey
	 *            : Key
	 * @param iCol
	 *            : Column Position
	 * @param isCategory
	 *            : if the Column is a categorized column
	 * @return a list with label|value items
	 * 
	 */

	@SuppressWarnings("unchecked")
	public static List<String> getViewLookup(String sViewName, String sKey,
			int iCol, boolean isCategory) {
		List<String> vResult = new ArrayList<String>();

		View vw = null;
		ViewNavigator nav = null;

		ViewEntry entry = null;
		ViewEntry tentry = null;

		try {
			vw = getView(sViewName);
			vw.setAutoUpdate(false);

			if (sKey == null || sKey.equals("")) {
				nav = vw.createViewNav();
			} else {
				nav = vw.createViewNavFromCategory(sKey);
			}

			// Util.logEvent("getViewLookup: found " + nav.getCount());

			nav.setBufferMaxEntries(200);
			nav
					.setEntryOptions(lotus.domino.ViewNavigator.VN_ENTRYOPT_NOCOUNTDATA);

			entry = nav.getFirst();
			tentry = null;
			String value = "";
			while (entry != null) {
				if (isCategory && entry.isCategory()) {
					Vector<Object> entryValues = entry.getColumnValues();
					if (entryValues.size() >= iCol) {
						value = (String) entry.getColumnValues()
								.elementAt(iCol);
						if (!value.equals("")) {
							vResult.add(value);
						}
					}
				}
				if (!isCategory && !entry.isCategory()) {
					value = (String) entry.getColumnValues().elementAt(iCol);
					if (!vResult.contains(value)) {
						vResult.add(value);
					}
				}
				if (isCategory) {
					tentry = nav.getNextSibling();
				} else {
					tentry = nav.getNext(entry);
				}

				entry.recycle();
				entry = tentry;

			}

		} catch (Exception e) {
			Util.logError(e);
			e.printStackTrace();
		} finally {
			recycleObjects(vw, nav, entry, tentry);
		}

		return vResult;
	}

	public static ViewNavigator getViewNav(View vw) {
		return getViewNav(vw, "", false);
	}

	public static ViewNavigator getViewNav(View vw, String sKey) {
		return getViewNav(vw, sKey, false);
	}

	/**
	 * Returns a View Navigator.. RECYCLE AT THE END!
	 * 
	 * @param sDbKey
	 * @param sViewName
	 * @param sKey
	 * @param autoupdate
	 * @return
	 */
	public static ViewNavigator getViewNav(View vw, String sKey,
			boolean autoupdate) {
		ViewNavigator vwNav = null;
		try {
			// View vw = getView(sDbKey, sViewName);
			vw.setAutoUpdate(autoupdate);

			if (sKey.equals("")) {
				vwNav = vw.createViewNav();
			} else {
				// Util.logEvent("create createViewNavFromCategory:" + sKey);
				vwNav = vw.createViewNavFromCategory(sKey);
			}

			if (vwNav.getCount() == 0) {
				// Util.logEvent("getViewNav:no Documents found. dbKey: "
				// + ", view: " + vw.getName() + ", key: " + sKey);
			} else {

				// Util.logEvent("getViewNav:" + vwNav.getCount()
				// + " Documents . dbKey: " + ", view: " + vw.getName()
				// + ", key: " + sKey);

			}

			vwNav.setBufferMaxEntries(100);

		} catch (Exception e) {
			Util.logError(e);
		}

		return vwNav;
	}

	/*
	 * Wraps a lotus.domino.Document as a
	 * com.ibx.xsp.model.domino.wrapped.DominoDocument, including a RichText
	 * item
	 * 
	 * @param doc document to be wrapped
	 * 
	 * @param richTextItemName name of the rich text item containing standard
	 * RichText or MIME contents that need to be wrapped
	 */
	private static DominoDocument wrapDocument(final Document doc,
			final String richTextItemName) throws NotesException {

		DominoDocument wrappedDoc = null;

		Database db = doc.getParentDatabase();

		// disable MIME to RichText conversion
		db.getParent().setConvertMIME(false);

		// wrap the lotus.domino.Document as a lotus.domino.DominoDocument
		// see
		// http://public.dhe.ibm.com/software/dw/lotus/Domino-Designer/JavaDocs/DesignerAPIs/com/ibm/xsp/model/domino/wrapped/DominoDocument.html
		wrappedDoc = DominoDocument.wrap(doc.getParentDatabase().getFilePath(),
				doc, null, null, false, null, null);

		// see
		// http://public.dhe.ibm.com/software/dw/lotus/Domino-Designer/JavaDocs/DesignerAPIs/com/ibm/xsp/model/domino/wrapped/DominoRichTextItem.html
		DominoRichTextItem drti = null;

		Item itemRT = doc.getFirstItem(richTextItemName);

		if (null != itemRT) {

			if (itemRT.getType() == Item.RICHTEXT) {

				// create a DominoRichTextItem from the RichTextItem
				RichTextItem rt = (RichTextItem) itemRT;
				drti = new DominoRichTextItem(wrappedDoc, rt);

			} else if (itemRT.getType() == Item.MIME_PART) {

				// create a DominoRichTextItem from the Rich Text item that
				// contains MIME
				MIMEEntity rtAsMime = doc.getMIMEEntity(richTextItemName);
				drti = new DominoRichTextItem(wrappedDoc, rtAsMime,
						richTextItemName);

			}
		}

		wrappedDoc.setRichTextItem(richTextItemName, drti);

		return wrappedDoc;

	}

	public static void recycleObjects(Base... nObjects) {
		for (Base nObject : nObjects) {
			if (nObject != null) {
				try {
					(nObject).recycle();
				} catch (Exception ne) {
				}
			}
		}
	}

	public static ViewEntryCollection getViewEntriesFtSearch(View vw,
			String searchQuery, int colSort) {
		ViewEntryCollection col = null;
		try {
			// View vw = getView(sDbKey, sViewName);
			// vw.refresh();
			vw.setAutoUpdate(false);

			// Util.logEvent("search with searchQuery: " + searchQuery);

			int count = vw.FTSearchSorted(searchQuery, 100, colSort);

			// Util.logEvent("The FTSearch with query: " + searchQuery +
			// " returned " + count + " values.");

			col = vw.getAllEntries();

		} catch (Exception e) {
			Util.logError(e);
			Util.logEvent("There was a problem with search query:"
					+ searchQuery);
		}
		return col;

	}

	public static Document createDocument() {
		Document doc = null;
		try {
			doc = getDb().createDocument();

		} catch (Exception e) {
			Util.logError(e);
		}
		return doc;
	}

}

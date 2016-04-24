/*

<!--
Copyright 2016 Fr�d�ric Deh�din
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.application.DesignerApplicationEx;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.designer.context.XSPUserAgent;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;

@SuppressWarnings("unchecked")
public class JSFUtil {
	private static final String ROLE_ADMINISTRATOR = "[Administrator]";

	public static DominoDocument getCurrentDocument() {
		return getCurrentDocument("currentDocument");
	}

	public static DominoDocument getCurrentDocument(String datasourceName) {
		DominoDocument dominoDoc = (DominoDocument) getVariableValue(datasourceName);
		return dominoDoc;
	}

	/**
	 * The method creates a {@link javax.faces.el.ValueBinding} from the
	 * specified value binding expression and returns its current value.<br>
	 * <br>
	 * If the expression references a managed bean or its properties and the
	 * bean has not been created yet, it gets created by the JSF runtime.
	 * 
	 * @param ref
	 *            value binding expression, e.g. #{Bean1.property}
	 * @return value of ValueBinding throws
	 *         javax.faces.el.ReferenceSyntaxException if the specified
	 *         <code>ref</code> has invalid syntax
	 */

	public static Object getBindingValue(String ref) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		return application.createValueBinding(ref).getValue(context);
	}

	public static ValueBinding getValueBinding() {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		String value = "#document1.subject";
		ValueBinding binding = application.createValueBinding(value);

		return binding;
	}

	/**
	 * The method creates a {@link javax.faces.el.ValueBinding} from the
	 * specified value binding expression and sets a new value for it.<br>
	 * <br>
	 * If the expression references a managed bean and the bean has not been
	 * created yet, it gets created by the JSF runtime.
	 * 
	 * @param ref
	 *            value binding expression, e.g. #{Bean1.property}
	 * @param newObject
	 *            new value for the ValueBinding throws
	 *            javax.faces.el.ReferenceSyntaxException if the specified
	 *            <code>ref</code> has invalid syntax
	 */
	public static void setBindingValue(String ref, Object newObject) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ValueBinding binding = application.createValueBinding(ref);
		binding.setValue(context, newObject);
	}

	/**
	 * The method returns the value of a global JavaScript variable.
	 * 
	 * @param varName
	 *            variable name
	 * @return value
	 * @throws javax.faces.el.EvaluationException
	 *             if an exception is thrown while resolving the variable name
	 */
	public static Object getVariableValue(String varName) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().getVariableResolver().resolveVariable(
				context, varName);
	}

	/**
	 * Finds an UIComponent by its component identifier in the current component
	 * tree.
	 * 
	 * @param compId
	 *            the component identifier to search for
	 * @return found UIComponent or null
	 * 
	 * @throws NullPointerException
	 *             if <code>compId</code> is null
	 */
	public static UIComponent findComponent(String compId) {
		return findComponent(FacesContext.getCurrentInstance().getViewRoot(),
				compId);
	}

	/**
	 * Finds an UIComponent by its component identifier in the component tree
	 * below the specified <code>topComponent</code> top component.
	 * 
	 * @param topComponent
	 *            first component to be checked
	 * @param compId
	 *            the component identifier to search for
	 * @return found UIComponent or null
	 * 
	 * @throws NullPointerException
	 *             if <code>compId</code> is null
	 */
	public static UIComponent findComponent(UIComponent topComponent,
			String compId) {
		if (compId == null)
			throw new NullPointerException(
					"Component identifier cannot be null");

		if (compId.equals(topComponent.getId()))
			return topComponent;

		if (topComponent.getChildCount() > 0) {
			List<UIComponent> childComponents = topComponent.getChildren();

			for (UIComponent currChildComponent : childComponents) {
				UIComponent foundComponent = findComponent(currChildComponent,
						compId);
				if (foundComponent != null)
					return foundComponent;
			}
		}
		return null;
	}

	private static Session _signerSess;

	public static DesignerApplicationEx getApplication() {
		return (DesignerApplicationEx) getFacesContext().getApplication();
	}

	public static Map<String, Object> getApplicationScope() {
		return getServletContext().getApplicationMap();
	}

	public static Map<String, Object> getCompositeData() {
		return (Map<String, Object>) getVariableResolver().resolveVariable(
				getFacesContext(), "compositeData");
	}

	public static Database getCurrentDatabase() {
		return (Database) getVariableResolver().resolveVariable(
				getFacesContext(), "database");
	}

	public static Session getCurrentSession() {
		return (Session) getVariableResolver().resolveVariable(
				getFacesContext(), "session");
	}

	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public static Map<String, Object> getRequestScope() {
		return getServletContext().getRequestMap();
	}

	public static ExternalContext getServletContext() {
		return getFacesContext().getExternalContext();
	}

	public static Map<String, Object> getSessionScope() {
		return getServletContext().getSessionMap();
	}

	private static VariableResolver getVariableResolver() {
		return getApplication().getVariableResolver();
	}

	public static UIViewRootEx getViewRoot() {
		return (UIViewRootEx) getFacesContext().getViewRoot();
	}

	public static Map<String, Object> getViewScope() {
		return getViewRoot().getViewMap();
	}

	public static XSPContext getXSPContext() {
		return XSPContext.getXSPContext(getFacesContext());
	}

	public static String getCurrentUrl() {
		return getXSPContext().getUrl().toString();
	}

	/**
	 * @return the current xpage name, without .xsp ending
	 */
	public static String getCurrentPageName() {
		String pageName = getViewRoot().getPageName();
		pageName = pageName.replace("/", "");
	//	pageName = pageName.replace(".xsp", "");
		return pageName;
	}

	public static String getHostName() {
		return getXSPContext().getUrl().getHost();
	}

	public static XSPUserAgent getXSPUserAgent() {
		return getXSPContext().getUserAgent();
	}

	public static Object resolveVariable(String variable) {
		return FacesContext.getCurrentInstance().getApplication()
				.getVariableResolver().resolveVariable(
						FacesContext.getCurrentInstance(), variable);
	}

	public static String getCommonUserName(String userName) {
		String sResult = "";
		try {
			Name nam = JSFUtil.getCurrentSession().createName(userName);
			sResult = nam.getCommon();
			nam.recycle();
		} catch (Exception e) {
			Util.logError(e);
		}

		return sResult;

	}

	/**
	 * @since 3.0.0 Added by Paul Withers to get current session as signer
	 */
	public static Session getSessionAsSigner() {
		if (_signerSess == null) {
			_signerSess = NotesContext.getCurrent().getSessionAsSigner();
		} else {
			try {
				@SuppressWarnings("unused")
				boolean pointless = _signerSess.isOnServer();
			} catch (NotesException recycleSucks) {
				// our database object was recycled so we'll need to get it
				// again
				try {
					_signerSess = NotesContext.getCurrent()
							.getSessionAsSigner();
				} catch (Exception e) {

				}
			}
		}
		return _signerSess;
	}

	/*
	 * public static DateTime getDominoDate(Date dt) { DateTime result = null;
	 * try { result = JSFUtil.getCurrentSession().createDateTime(dt); } catch
	 * (Exception e) { Util.logError(e); }
	 * 
	 * return result; }
	 */

	public static DateTime getToday() {
		DateTime result = null;
		try {
			result = JSFUtil.getCurrentSession().createDateTime("Today");
		} catch (Exception e) {
			Util.logError(e);
		}

		return result;
	}

	public static DateTime getNow() {
		DateTime result = null;
		try {
			result = getToday();
			result.setNow();
		} catch (Exception e) {
			Util.logError(e);
		}

		return result;
	}

	/**
	 * @return the Current User Name in Canonical Format
	 */
	public static String getUserNameCanonical() {
		String sResult = "";

		try {
			Session s = JSFUtil.getCurrentSession();
			Name nam = s.createName(s.getEffectiveUserName());
			sResult = nam.getCanonical();
			nam.recycle();
		} catch (Exception e) {
			Util.logError(e);
		}
		return sResult;
	}

	/**
	 * @param UserName
	 * @return the Given User Name in Canonical Format
	 */
	public static String getUserNameCanonical(String userName) {
		String sResult = "";

		try {
			Session s = JSFUtil.getCurrentSession();
			Name nam = s.createName(userName);
			sResult = nam.getCanonical();
			nam.recycle();
		} catch (Exception e) {
			Util.logError(e);
		}
		return sResult;
	}

	/**
	 * @return the Current User Name in Abbreviated Format
	 */
	public static String getUserNameAbbreviated() {
		String sResult = "";

		try {
			Session s = JSFUtil.getCurrentSession();
			Name nam = s.createName(s.getEffectiveUserName());
			sResult = nam.getAbbreviated();
			nam.recycle();
		} catch (Exception e) {
			Util.logError(e);
		}
		return sResult;
	}

	/**
	 * @param UserName
	 * @return the Given User Name in Abbreviaated Format
	 */
	public static String getUserNameAbbreviated(String userName) {
		String sResult = "";

		try {
			Session s = JSFUtil.getCurrentSession();
			Name nam = s.createName(userName);
			sResult = nam.getAbbreviated();
			nam.recycle();
		} catch (Exception e) {
			Util.logError(e);
		}
		return sResult;
	}

	private static List<String> getUserRoles() {
		List<String> lstResult = new ArrayList<String>();

		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			XSPContext context = XSPContext.getXSPContext(facesContext);
			lstResult = context.getUser().getRoles();

		} catch (Exception e) {
			Util.logError(e);
		}

		return lstResult;
	}

	public static boolean isRole(String role) {
		boolean bResult = false;

		try {
			List<String> roles = getUserRoles();
			if (roles.contains(role)) {
				bResult = true;
			} else {
				bResult = false;
			}
		} catch (Exception e) {
			Util.logError(e);
		}

		return bResult;
	}

	public static boolean isRoleAdministrator() {
		return isRole(ROLE_ADMINISTRATOR);
	}

	public static String getQueryString(String paramName) {
		String result = getXSPContext().getUrlParameter(paramName);
		if (result == null) {
			result = "";
		}

		return result;
	}

	public static void showErrorMessage(String errorMessage) {
		showErrorMessage(errorMessage, "");
	}

	public static void showErrorMessage(String errorMessage, String componentId) {
		try {
			FacesContext facesContext = getFacesContext();
			UIComponent component = null;
			if (!componentId.equals("")) {
				component = JSFUtil.findComponent(componentId);
				facesContext.addMessage(component.getClientId(facesContext),
						new FacesMessage(errorMessage));
			} else {
				facesContext.addMessage("", new FacesMessage(errorMessage));
			}

		} catch (Exception e) {
			Util.logError(e);
		}
	}

	public static void redirect(String url) {
		try {
			getServletContext().redirect(url);
		} catch (Exception e) {
			Util.logError(e);
		}
	}
	

	public static HttpServletRequest getRequest() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext exCon = ctx.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) exCon.getRequest();
		return request;
	}

	@SuppressWarnings("unused")
	public static HttpServletResponse getResponse() {

		HttpServletResponse response = null;
		try {

			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext exCon = ctx.getExternalContext();

			response = (HttpServletResponse) exCon.getResponse();

		//	response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setCharacterEncoding("utf-8");
		} catch (Exception ex) {
			Util.logError(ex);
		}
		return response;
	}

	public static ResponseWriter getResponseWriter()
	{
		ResponseWriter writer = null;
		try
		{

			FacesContext ctx = FacesContext.getCurrentInstance();
			writer = ctx.getResponseWriter();

		} catch (Exception ex)
		{
			Util.logError(ex);
		}
		return writer;
	}
	
}
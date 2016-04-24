var loadingDialog; // global dialog object

function displayWait() {
	
	txtContent = "Daten werden geladen<br/>Bitte warten...";
	txtContent = "<div style=\"text-align:center;padding:20px;\"><img src=\"ajax-loader.gif\" alt=\"\" /><br/><br/>"
			+ txtContent + "</div>";
	
	loadingDialog = new dijit.Dialog( {
		title : "",
		content : txtContent
	});
	
	dojo.body().appendChild(loadingDialog.domNode);
	loadingDialog.titleBar.style.display = 'none';
	loadingDialog.startup();
	loadingDialog.show();
}

function hideWait() {
	loadingDialog.hide()
}
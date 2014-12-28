function loadXMLDoc() {
	var xmlhttp;
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	} else {// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange=function() {
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			element = document.getElementById ("data");
			var obj = JSON.parse(xmlhttp.responseText);
			for (meeting in obj) {
				var row = document.createElement ("tr");
				var col1 = document.createElement ("td");
				var col2 = document.createElement ("td");

				col1.innerHTML = meeting;
				col2.innerHTML = meeting.length;
				
				element.appendChild (row)
				element.appendChild (col1);
				element.appendChild (col2);
				
			}
		}
	}
	xmlhttp.open("GET","myeavesdrop",true);
	xmlhttp.send();
}